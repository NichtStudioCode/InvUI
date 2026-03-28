package xyz.xenondevs.invui.internal.menu;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.MapMaker;
import com.google.common.hash.HashCode;
import com.mojang.serialization.DynamicOps;
import io.papermc.paper.adventure.PaperAdventure;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.IntLinkedOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.kyori.adventure.text.Component;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.HashedPatchMap;
import net.minecraft.network.HashedStack;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundPingPacket;
import net.minecraft.network.protocol.common.ServerboundPongPacket;
import net.minecraft.network.protocol.game.*;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.HashOps;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.BundleContents;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftInventory;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.Click;
import xyz.xenondevs.invui.InvUI;
import xyz.xenondevs.invui.internal.network.PacketListener;
import xyz.xenondevs.invui.internal.util.FakeInventoryView;
import xyz.xenondevs.invui.internal.util.InventoryUtils;
import xyz.xenondevs.invui.internal.util.MathUtils;
import xyz.xenondevs.invui.internal.util.PingData;
import xyz.xenondevs.invui.window.Window;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

/**
 * A packet-based container menu.
 */
public abstract class CustomContainerMenu {
    
    /**
     * The amount of slots in the lower (player) inventory.
     */
    private static final int LOWER_INVENTORY_SIZE = 36;
    
    /**
     * The slot number of the off-hand slot in the inventory menu.
     */
    private static final int OFF_HAND_SLOT = 45;
    
    /**
     * The timeout for {@link #pendingPongs}, in ms.
     */
    private static final long PING_TIMEOUT_MS = 10_000;
    
    /**
     * Hash generator for {@link HashedStack}.
     */
    private static final HashedPatchMap.HashGenerator HASH_GENERATOR = new HashedPatchMap.HashGenerator() {
        
        // layer 1 cache uses identity hash code to avoid expensive component hash code calculations
        private final ConcurrentMap<Object, Integer> layer1 = new MapMaker()
            .weakKeys() // also enables identity-based lookup
            .makeMap();
        
        private final LoadingCache<TypedDataComponent<?>, Integer> layer2 = CacheBuilder.newBuilder()
            .expireAfterAccess(Duration.ofMinutes(1))
            .build(new CacheLoader<>() {
                
                private final DynamicOps<HashCode> hashOps = RegistryOps.create(
                    HashOps.CRC32C_INSTANCE,
                    MinecraftServer.getServer().registryAccess()
                );
                
                @Override
                public Integer load(TypedDataComponent<?> key) {
                    return key.encodeValue(hashOps).getOrThrow().asInt();
                }
                
            });
        
        @Override
        public Integer apply(TypedDataComponent<?> typedDataComponent) {
            return layer1.computeIfAbsent(
                typedDataComponent.value(),
                _ -> layer2.getUnchecked(typedDataComponent)
            );
        }
        
    };
    
    /**
     * An item stack used for marking a remote slot dirty.
     */
    protected static final HashedStack DIRTY_MARKER = HashedStack.create(new ItemStack(
        BuiltInRegistries.ITEM.wrapAsHolder(Items.DIRT), 1,
        DataComponentPatch.builder()
            .set(DataComponents.ITEM_MODEL, Identifier.fromNamespaceAndPath("invui", "dirty_marker"))
            .build()
    ), HASH_GENERATOR);
    
    private final MenuType<?> menuType;
    protected final int containerId;
    protected final Player player;
    private final ServerPlayer serverPlayer;
    private @Nullable Window window;
    private final ContainerMenuProxy proxy;
    
    private final NonNullList<ItemStack> items;
    private final NonNullList<HashedStack> remoteItems;
    private ItemStack carried = ItemStack.EMPTY;
    private HashedStack remoteCarried = HashedStack.EMPTY;
    private HashedStack remoteOffHand;
    protected final int[] dataSlots;
    protected final int[] remoteDataSlots;
    private int stateId;
    private Component title = Component.empty();
    
    private final IntSet dragSlots = new IntLinkedOpenHashSet();
    private ClickType dragMode = ClickType.LEFT;
    
    protected final Queue<Packet<? super ServerGamePacketListener>> incoming = new ConcurrentLinkedQueue<>();
    private final Map<Integer, PingData> pendingPongs = new ConcurrentHashMap<>();
    
    // Only items marked as dirty via the fields below will be compared with their remote counterparts.
    // Then, only if they differ, an update packet will be created for them.
    // The intention of these dirty fields is to avoid expensive comparisons between the remote and server items.
    private final BitSet dirtyItems;
    private boolean dirtyCarried;
    private boolean dirtyOffHand;
    
    /**
     * Creates a new {@link CustomContainerMenu} for the specified player.
     *
     * @param menuType The type of the menu
     * @param player   The player that will see the menu
     */
    protected CustomContainerMenu(MenuType<?> menuType, org.bukkit.entity.Player player) {
        this.menuType = menuType;
        this.player = player;
        this.serverPlayer = ((CraftPlayer) player).getHandle();
        this.containerId = serverPlayer.nextContainerCounter();
        
        int size = InventoryUtils.getSizeOf(menuType) + LOWER_INVENTORY_SIZE;
        this.items = NonNullList.withSize(size, ItemStack.EMPTY);
        this.remoteItems = NonNullList.withSize(size, HashedStack.EMPTY);
        this.remoteOffHand = HashedStack.create(serverPlayer.getOffhandItem(), HASH_GENERATOR);
        this.dirtyItems = new BitSet(size);
        
        int dataSize = InventoryUtils.getDataSlotCountOf(menuType);
        this.dataSlots = new int[dataSize];
        this.remoteDataSlots = new int[dataSize];
        
        this.proxy = new ContainerMenuProxy();
    }
    
    /**
     * Puts an {@link org.bukkit.inventory.ItemStack} into the specified slot.
     *
     * @param slot The slot to put the item into
     * @param item The {@link org.bukkit.inventory.ItemStack} to put into the slot
     */
    public void setItem(int slot, org.bukkit.inventory.@Nullable ItemStack item) {
        if (slot < 0 || slot >= items.size())
            throw new IllegalArgumentException("Slot out of bounds: " + slot);
        
        items.set(slot, item == null ? ItemStack.EMPTY : CraftItemStack.unwrap(item));
        dirtyItems.set(slot);
    }
    
    public ItemStack getItem(int slot) {
        if (slot < 0 || slot >= items.size())
            throw new IllegalArgumentException("Slot out of bounds: " + slot);
        
        return items.get(slot);
    }
    
    /**
     * Sets the {@link org.bukkit.inventory.ItemStack} on the cursor.
     *
     * @param item The {@link org.bukkit.inventory.ItemStack} to set on the cursor
     */
    public void setCursor(org.bukkit.inventory.@Nullable ItemStack item) {
        carried = item == null ? ItemStack.EMPTY : CraftItemStack.unwrap(item);
        dirtyCarried = true;
    }
    
    /**
     * Gets the {@link org.bukkit.inventory.ItemStack} on the cursor.
     *
     * @return The {@link org.bukkit.inventory.ItemStack} on the cursor
     */
    public org.bukkit.inventory.ItemStack getCursor() {
        return CraftItemStack.asCraftMirror(carried);
    }
    
    //<editor-fold desc="synchronization">
    protected void setRemoteItem(int slot, HashedStack item) {
        if (slot < 0 || slot >= remoteItems.size())
            throw new IllegalArgumentException("Slot out of bounds: " + slot);
        
        remoteItems.set(slot, item);
        dirtyItems.set(slot);
    }
    
    protected void setRemoteCarried(HashedStack item) {
        remoteCarried = item;
        dirtyCarried = true;
    }
    
    protected void setRemoteOffHand(HashedStack item) {
        remoteOffHand = item;
        dirtyOffHand = true;
    }
    
    /**
     * Sends all changes to the remote client.
     *
     * @param pingId Optional ping id to also send a ping packet, or any negative number to not send a ping packet.
     */
    public void sendChangesToRemote(int pingId) {
        var packets = new ArrayList<Packet<? super ClientGamePacketListener>>();
        
        int itemSlot = -1;
        while ((itemSlot = dirtyItems.nextSetBit(itemSlot + 1)) != -1) {
            var item = items.get(itemSlot);
            var remoteItem = remoteItems.get(itemSlot);
            if (remoteItem == DIRTY_MARKER || !remoteItem.matches(item, HASH_GENERATOR)) {
                packets.add(new ClientboundContainerSetSlotPacket(containerId, incrementStateId(), itemSlot, item.copy()));
                remoteItems.set(itemSlot, HashedStack.create(item, HASH_GENERATOR));
            }
        }
        dirtyItems.clear();
        
        if (dirtyOffHand) {
            var offHand = serverPlayer.getOffhandItem();
            if (remoteOffHand == DIRTY_MARKER || !remoteOffHand.matches(offHand, HASH_GENERATOR)) {
                packets.add(new ClientboundContainerSetSlotPacket(serverPlayer.inventoryMenu.containerId, incrementStateId(), OFF_HAND_SLOT, offHand.copy()));
                remoteOffHand = HashedStack.create(offHand, HASH_GENERATOR);
            }
            dirtyOffHand = false;
        }
        
        if (dirtyCarried) {
            if (remoteCarried == DIRTY_MARKER || !remoteCarried.matches(carried, HASH_GENERATOR)) {
                packets.add(new ClientboundSetCursorItemPacket(carried.copy()));
                remoteCarried = HashedStack.create(carried, HASH_GENERATOR);
            }
            dirtyCarried = false;
        }
        
        for (int dataSlot = 0; dataSlot < dataSlots.length; dataSlot++) {
            if (dataSlots[dataSlot] != remoteDataSlots[dataSlot]) {
                packets.add(new ClientboundContainerSetDataPacket(containerId, dataSlot, dataSlots[dataSlot]));
                remoteDataSlots[dataSlot] = dataSlots[dataSlot];
            }
        }
        
        if (pingId >= 0) {
            packets.add(createMaskedPingPacket(pingId));
        }
        
        PacketListener.getInstance().injectOutgoing(player, packets);
    }
    
    /**
     * Sends the cursor item stack to the remote client.
     */
    public void sendCarriedToRemote() {
        var content = new ClientboundSetCursorItemPacket(carried.copy());
        PacketListener.getInstance().injectOutgoing(player, content);
        remoteCarried = HashedStack.create(carried, HASH_GENERATOR);
        dirtyCarried = false;
    }
    
    /**
     * Sends all data to the remote client.
     *
     * @param pingId Optional ping id to also send a ping packet, or any negative number to not send a ping packet.
     */
    public void sendAllToRemote(int pingId) {
        PacketListener.getInstance().injectOutgoing(player, createContainerInitPacketList(pingId));
        markRemoteSynced();
    }
    
    /**
     * Sends screen open and initial content packets to the remote client.
     *
     * @param title The title of the inventory
     */
    public void sendOpenPacket(Component title) {
        this.title = title;
        
        var packets = createContainerInitPacketList(-1);
        packets.addFirst(new ClientboundOpenScreenPacket(containerId, menuType, PaperAdventure.asVanilla(title)));
        PacketListener.getInstance().injectOutgoing(player, packets);
        markRemoteSynced();
    }
    
    /**
     * Creates the list of packets needed for container initialization, excluding the open screen packet.
     *
     * @param pingId Optional ping id to also include a ping packet, or any negative number to not include a ping packet.
     * @return The list of packets
     */
    private List<Packet<? super ClientGamePacketListener>> createContainerInitPacketList(int pingId) {
        var packets = new ArrayList<Packet<? super ClientGamePacketListener>>();
        packets.add(new ClientboundContainerSetContentPacket(
            containerId,
            incrementStateId(),
            items.stream().map(ItemStack::copy).toList(),
            carried.copy()
        ));
        for (int i = 0; i < dataSlots.length; i++) {
            packets.add(new ClientboundContainerSetDataPacket(containerId, i, dataSlots[i]));
        }
        if (pingId >= 0) {
            packets.add(createMaskedPingPacket(pingId));
        }
        return packets;
    }
    
    /**
     * Marks the current state as synced with the remote client.
     */
    private void markRemoteSynced() {
        for (int i = 0; i < items.size(); i++) {
            remoteItems.set(i, HashedStack.create(items.get(i), HASH_GENERATOR));
        }
        remoteCarried = HashedStack.create(carried, HASH_GENERATOR);
        System.arraycopy(dataSlots, 0, remoteDataSlots, 0, dataSlots.length);
        
        dirtyItems.clear();
        dirtyCarried = false;
        dirtyOffHand = false;
    }
    //</editor-fold>
    
    /**
     * Opens this menu for the player.
     *
     * @param title The title of the inventory
     */
    public void open(Component title) {
        var pl = PacketListener.getInstance();
        pl.redirectIncoming(player, ServerboundContainerButtonClickPacket.class, incoming);
        pl.redirectIncoming(player, ServerboundContainerClickPacket.class, incoming);
        pl.redirectIncoming(player, ServerboundContainerClosePacket.class, incoming);
        pl.redirectIncoming(player, ServerboundSelectBundleItemPacket.class, incoming);
        pl.listenIncoming(player, ServerboundPongPacket.class, incoming);
        pl.discard(player, ClientboundOpenScreenPacket.class);
        pl.discard(player, ClientboundContainerSetContentPacket.class);
        pl.discard(player, ClientboundContainerSetDataPacket.class);
        pl.discard(player, ClientboundContainerSetSlotPacket.class);
        
        this.serverPlayer.containerMenu = proxy;
        sendOpenPacket(title);
    }
    
    /**
     * Runs cleanup logic after this menu has been closed.
     */
    public void handleClosed() {
        var pl = PacketListener.getInstance();
        pl.removeRedirect(player, ServerboundContainerButtonClickPacket.class);
        pl.removeRedirect(player, ServerboundContainerClickPacket.class);
        pl.removeRedirect(player, ServerboundContainerClosePacket.class);
        pl.removeRedirect(player, ServerboundSelectBundleItemPacket.class);
        pl.stopListening(player, ServerboundPongPacket.class);
        pl.stopDiscard(player, ClientboundOpenScreenPacket.class);
        pl.stopDiscard(player, ClientboundContainerSetContentPacket.class);
        pl.stopDiscard(player, ClientboundContainerSetDataPacket.class);
        pl.stopDiscard(player, ClientboundContainerSetSlotPacket.class);
        
        // transfer remote items state to inventory menu
        for (int i = 0; i < LOWER_INVENTORY_SIZE; i++) {
            var item = remoteItems.get((remoteItems.size() - LOWER_INVENTORY_SIZE) + i);
            // in the inventory menu, contents start at 9
            serverPlayer.inventoryMenu.setRemoteSlotUnsafe(9 + i, item);
        }
        serverPlayer.inventoryMenu.setRemoteSlotUnsafe(OFF_HAND_SLOT, remoteOffHand);
    }
    
    /**
     * Sends a ping packet with a random id mapping to the given window state id.
     *
     * @param id The window state id
     */
    private ClientboundPingPacket createMaskedPingPacket(int id) {
        // generate new ping id, remember mapping and timestamp
        int ping = MathUtils.RANDOM.nextInt();
        pendingPongs.put(ping, new PingData(id, System.currentTimeMillis()));
        
        // clear timed out pings
        long now = System.currentTimeMillis();
        pendingPongs.values().removeIf(data -> now - data.timestamp() > PING_TIMEOUT_MS);
        
        return new ClientboundPingPacket(ping);
    }
    
    //<editor-fold desc="action handlers">
    
    /**
     * Handles all queued interaction packets.
     *
     * @return The kind of update that is required after processing the incoming packets
     */
    public UpdateType processIncoming() {
        Packet<? super ServerGamePacketListener> packet;
        UpdateType updateType = UpdateType.NONE;
        while ((packet = incoming.poll()) != null) {
            updateType = updateType.or(processPacket(packet));
        }
        
        return updateType;
    }
    
    /**
     * Processes a single incoming packet.
     *
     * @param packet The packet to process
     * @return The kind of update that is required after processing this packet
     */
    protected UpdateType processPacket(Packet<? super ServerGamePacketListener> packet) {
        return switch (packet) {
            case ServerboundContainerButtonClickPacket p -> handleButtonClick(p.buttonId());
            case ServerboundContainerClickPacket p -> handleClick(p);
            case ServerboundSelectBundleItemPacket p -> handleBundleSelect(p);
            case ServerboundContainerClosePacket p -> {
                handleClose(p);
                yield UpdateType.NONE;
            }
            case ServerboundPongPacket p -> {
                handlePong(p);
                yield UpdateType.NONE;
            }
            default -> throw new UnsupportedOperationException("Unknown packet type: " + packet.getClass().getName());
        };
    }
    
    /**
     * Handles a pong packet from the client, which may or may not be related to a ping packet sent through this menu.
     *
     * @param packet The packet that was received
     */
    private void handlePong(ServerboundPongPacket packet) {
        var data = pendingPongs.remove(packet.getId());
        if (data != null) // ignore unknown pongs, unrelated to InvUI
            getWindowEvents().handlePong(data.id());
    }
    
    /**
     * Handles a client-initiated inventory close.
     *
     * @param packet The packet that was received
     */
    private void handleClose(ServerboundContainerClosePacket packet) {
        if (packet.getContainerId() != containerId)
            return;
        
        if (getWindow().isCloseable()) {
            getWindowEvents().handleClose(InventoryCloseEvent.Reason.PLAYER);
            serverPlayer.containerMenu = serverPlayer.inventoryMenu;
        } else {
            sendOpenPacket(title);
        }
    }
    
    /**
     * Handles a client-initiated inventory click.
     * See <a href="https://minecraft.wiki/w/Java_Edition_protocol#Click_Container">Click Container</a> for more information.
     *
     * @param packet The packet that was received
     * @return The kind of update that is required after processing this packet
     */
    protected UpdateType handleClick(ServerboundContainerClickPacket packet) {
        boolean requiresFullUpdate = packet.stateId() != stateId;
        
        // update remote slots
        for (Int2ObjectMap.Entry<HashedStack> entry : packet.changedSlots().int2ObjectEntrySet()) {
            int slot = entry.getIntKey();
            HashedStack stack = entry.getValue();
            if (slot < 0 || slot > remoteItems.size())
                continue;
            setRemoteItem(slot, stack);
        }
        setRemoteCarried(DIRTY_MARKER);
        
        if (packet.clickType() == net.minecraft.world.inventory.ClickType.QUICK_CRAFT) {
            if (!handleDragClick(packet))
                return requiresFullUpdate ? UpdateType.FULL : UpdateType.DIRTY;
        } else {
            handleNormalClick(packet);
        }
        
        return requiresFullUpdate ? UpdateType.FULL : UpdateType.DIRTY;
    }
    
    /**
     * Handles a non-drag click packet.
     *
     * @param packet The packet that was received
     */
    private void handleNormalClick(ServerboundContainerClickPacket packet) {
        int hotbarBtn = -1;
        ClickType clickType = switch (packet.clickType()) {
            case PICKUP -> switch (packet.buttonNum()) {
                case 0 -> ClickType.LEFT;
                case 1 -> ClickType.RIGHT;
                default -> ClickType.UNKNOWN;
            };
            
            case QUICK_MOVE -> switch (packet.buttonNum()) {
                case 0 -> ClickType.SHIFT_LEFT;
                case 1 -> ClickType.SHIFT_RIGHT;
                default -> ClickType.UNKNOWN;
            };
            
            case SWAP -> switch (packet.buttonNum()) {
                case 0, 1, 2, 3, 4, 5, 6, 7, 8 -> {
                    hotbarBtn = packet.buttonNum();
                    yield ClickType.NUMBER_KEY;
                }
                case 40 -> {
                    setRemoteOffHand(DIRTY_MARKER);
                    yield ClickType.SWAP_OFFHAND;
                }
                default -> ClickType.UNKNOWN;
            };
            
            case CLONE -> ClickType.MIDDLE;
            
            case THROW -> switch (packet.buttonNum()) {
                case 0 -> packet.slotNum() > 0 ? ClickType.DROP : ClickType.LEFT;
                case 1 -> packet.slotNum() > 0 ? ClickType.CONTROL_DROP : ClickType.RIGHT;
                default -> ClickType.UNKNOWN;
            };
            
            case PICKUP_ALL -> ClickType.DOUBLE_CLICK;
            
            case QUICK_CRAFT -> throw new AssertionError(); // should never be reached
        };
        
        // let window handle the click
        int finalHotbarBtn = hotbarBtn;
        runInInteractionContext(() -> {
            Click click = new Click(player, clickType, finalHotbarBtn);
            getWindowEvents().handleClick(packet.slotNum(), click);
        });
    }
    
    /**
     * Handles a drag click packet.
     *
     * @param packet The packet that was received
     * @return Whether the remote state should be updated
     */
    private boolean handleDragClick(ServerboundContainerClickPacket packet) {
        // item dragging is split into multiple packets:
        // - drag start
        // - place item (one packet per item)
        // - drag end
        switch (packet.buttonNum()) {
            // add slot for left, right, middle drag
            case 1, 5, 9 -> {
                var slot = packet.slotNum();
                if (slot >= 0 && slot < items.size()) {
                    dragSlots.add(packet.slotNum());
                }
                
                dragMode = switch (packet.buttonNum()) {
                    case 1 -> ClickType.LEFT;
                    case 5 -> ClickType.RIGHT;
                    case 9 -> ClickType.MIDDLE;
                    default -> throw new AssertionError();
                };
                
                return false; // no update required, further quick_craft packets incoming
            }
            
            // end left, right, middle drag
            case 2, 6, 10 -> {
                runInInteractionContext(() -> {
                    if (dragSlots.size() == 1) {
                        // handle one slot drags as simple clicks
                        int slot = dragSlots.iterator().nextInt();
                        getWindowEvents().handleClick(slot, new Click(player, dragMode, -1));
                    } else {
                        getWindowEvents().handleDrag(dragSlots, dragMode);
                    }
                });
                dragSlots.clear();
            }
            
            // drag start can be ignored
            default -> {
                return false; // no update required, further quick_craft packets incoming
            }
        }
        
        return true;
    }
    
    /**
     * Handles a bundle item selection packet.
     *
     * @param packet The packet that was received
     */
    private UpdateType handleBundleSelect(ServerboundSelectBundleItemPacket packet) {
        // verify legal slot
        int slot = packet.slotId();
        if (slot < 0 || slot >= items.size())
            return UpdateType.NONE;
        
        // verify bundle at slot
        var bundle = items.get(slot);
        var bundleContents = bundle.get(DataComponents.BUNDLE_CONTENTS);
        if (bundleContents == null)
            return UpdateType.NONE;
        
        // update remote item to expected selected item index
        var mutableBundleContents = new BundleContents.Mutable(bundleContents);
        mutableBundleContents.toggleSelectedItem(packet.selectedItemIndex());
        bundle.set(DataComponents.BUNDLE_CONTENTS, mutableBundleContents.toImmutable());
        
        // let window handle the selection
        runInInteractionContext(() -> getWindowEvents().handleBundleSelect(slot, packet.selectedItemIndex()));
        
        return UpdateType.DIRTY;
    }
    
    /**
     * Handles a client-initiated menu button click.
     *
     * @param buttonId The id of the button that was clicked
     */
    protected UpdateType handleButtonClick(int buttonId) {
        return UpdateType.NONE;
    }
    
    /**
     * Runs the given {@link Runnable} in a catching interaction context.
     *
     * @param run The {@link Runnable} to run in the interaction context
     */
    protected void runInInteractionContext(Runnable run) {
        try {
            run.run();
        } catch (Throwable t) {
            InvUI.getInstance().handleException("An exception occurred while handling a window interaction", t);
        }
    }
    //</editor-fold>
    
    /**
     * Increments and returns the current state id.
     *
     * @return The new state id
     */
    public int incrementStateId() {
        this.stateId = this.stateId + 1 & 32767;
        return this.stateId;
    }
    
    /**
     * Sets the {@link Window} that is associated with this menu.
     *
     * @param window The {@link Window}
     */
    public void setWindow(Window window) {
        this.window = window;
    }
    
    /**
     * Gets the {@link Window} that is associated with this menu.
     *
     * @return The {@link Window}
     * @throws IllegalStateException If the window is not {@link #setWindow(Window) set}
     */
    public Window getWindow() {
        if (window == null)
            throw new IllegalStateException("Window is not set");
        return window;
    }
    
    public WindowEventListener getWindowEvents() {
        if (window == null)
            throw new IllegalStateException("Window is not set");
        return (WindowEventListener) window;
    }
    
    /**
     * A proxy {@link AbstractContainerMenu} for intercepting carried item change and general bukkit interoperability.
     */
    private class ContainerMenuProxy extends AbstractContainerMenu {
        
        private final Inventory bukkitInventory = new CraftInventory(new SimpleContainer(0));
        
        public ContainerMenuProxy() {
            super(CustomContainerMenu.this.menuType, CustomContainerMenu.this.containerId);
        }
        
        @Override
        public ItemStack getCarried() {
            return carried;
        }
        
        @Override
        public void setCarried(ItemStack stack) {
            carried = stack;
        }
        
        @Override
        public void broadcastCarriedItem() {
            sendCarriedToRemote();
        }
        
        @Override
        public InventoryView getBukkitView() {
            return new FakeInventoryView(player, bukkitInventory);
        }
        
        @Override
        public ItemStack quickMoveStack(net.minecraft.world.entity.player.Player player, int i) {
            return ItemStack.EMPTY;
        }
        
        @Override
        public boolean stillValid(net.minecraft.world.entity.player.Player player) {
            return true;
        }
        
    }
    
}
