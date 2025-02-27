package xyz.xenondevs.invui.internal.menu;

import io.papermc.paper.adventure.PaperAdventure;
import it.unimi.dsi.fastutil.ints.IntLinkedOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.kyori.adventure.text.Component;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.BundleContents;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftInventory;
import org.bukkit.craftbukkit.inventory.CraftInventoryView;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.InvUI;
import xyz.xenondevs.invui.internal.network.PacketListener;
import xyz.xenondevs.invui.internal.util.InventoryUtils;
import xyz.xenondevs.invui.Click;
import xyz.xenondevs.invui.window.AbstractWindow;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

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
     * An item stack used for marking a remote slot dirty.
     */
    protected static final ItemStack DIRTY_MARKER = new ItemStack(
        BuiltInRegistries.ITEM.wrapAsHolder(Items.DIRT), 1,
        DataComponentPatch.builder()
            .set(DataComponents.ITEM_MODEL, ResourceLocation.fromNamespaceAndPath("invui", "dirty_marker"))
            .build()
    );
    
    /**
     * A thread local that stores whether the current thread is an interaction handling context (i.e. handling a click).
     */
    private static final ThreadLocal<Boolean> interactionContext = ThreadLocal.withInitial(() -> false);
    
    private final MenuType<?> menuType;
    protected final int containerId;
    protected final Player player;
    private final ServerPlayer serverPlayer;
    private @Nullable AbstractWindow<?> window;
    private final ContainerMenuProxy proxy;
    
    protected final NonNullList<ItemStack> items;
    protected final NonNullList<ItemStack> remoteItems;
    private ItemStack carried = ItemStack.EMPTY;
    private ItemStack remoteCarried = ItemStack.EMPTY;
    private ItemStack remoteOffHand;
    protected final int[] dataSlots;
    protected final int[] remoteDataSlots;
    private int stateId;
    private Component title = Component.empty();
    
    private final IntSet dragSlots = new IntLinkedOpenHashSet();
    private ClickType dragMode = ClickType.LEFT;
    
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
        this.remoteItems = NonNullList.withSize(size, ItemStack.EMPTY);
        this.remoteOffHand = serverPlayer.getInventory().offhand.getFirst().copy();
        
        int dataSize = InventoryUtils.getDataSlotCountOf(menuType);
        this.dataSlots = new int[dataSize];
        this.remoteDataSlots = new int[dataSize];
        
        this.proxy = new ContainerMenuProxy();
    }
    
    /**
     * Checks whether the current thread is an interaction handling context, i.e.
     * currently handling a click packet.
     *
     * @return Whether we are currently in an interaction handling context
     */
    public static boolean isInInteractionHandlingContext() {
        return interactionContext.get();
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
    }
    
    //<editor-fold desc="synchronization">
    
    /**
     * Synchronizes the menu with the remote client.
     *
     * @param all Whether to send all data or only changes
     */
    public void sendToRemote(boolean all) {
        if (all) {
            sendAllToRemote();
        } else {
            sendChangesToRemote();
        }
    }
    
    /**
     * Sends all changes to the remote client.
     */
    public void sendChangesToRemote() {
        var packets = new ArrayList<Packet<? super ClientGamePacketListener>>();
        
        for (int i = 0; i < items.size(); i++) {
            var item = items.get(i);
            if (!ItemStack.matches(item, remoteItems.get(i))) {
                packets.add(new ClientboundContainerSetSlotPacket(containerId, incrementStateId(), i, item));
                remoteItems.set(i, item.copy());
            }
        }
        
        var offHand = serverPlayer.getInventory().offhand.getFirst();
        if (!ItemStack.matches(offHand, remoteOffHand)) {
            packets.add(new ClientboundContainerSetSlotPacket(serverPlayer.inventoryMenu.containerId, incrementStateId(), OFF_HAND_SLOT, offHand));
            remoteOffHand = offHand.copy();
        }
        
        if (!ItemStack.matches(carried, remoteCarried)) {
            packets.add(new ClientboundSetCursorItemPacket(carried));
            remoteCarried = carried.copy();
        }
        
        for (int i = 0; i < dataSlots.length; i++) {
            if (dataSlots[i] != remoteDataSlots[i]) {
                packets.add(new ClientboundContainerSetDataPacket(containerId, i, dataSlots[i]));
                remoteDataSlots[i] = dataSlots[i];
            }
        }
        
        var bundle = new ClientboundBundlePacket(packets);
        PacketListener.getInstance().injectOutgoing(player, bundle);
    }
    
    /**
     * Sends the cursor item stack to the remote client.
     */
    public void sendCarriedToRemote() {
        var content = new ClientboundSetCursorItemPacket(carried);
        PacketListener.getInstance().injectOutgoing(player, content);
        remoteCarried = carried.copy();
    }
    
    /**
     * Sends all data to the remote client.
     */
    public void sendAllToRemote() {
        PacketListener.getInstance().injectOutgoing(player, createContainerInitPacketList());
        markRemoteSynced();
    }
    
    /**
     * Sends screen open and initial content packets to the remote client.
     *
     * @param title The title of the inventory
     */
    public void sendOpenPacket(Component title) {
        this.title = title;
        
        var packets = createContainerInitPacketList();
        packets.addFirst(new ClientboundOpenScreenPacket(containerId, menuType, PaperAdventure.asVanilla(title)));
        PacketListener.getInstance().injectOutgoing(player, packets);
        markRemoteSynced();
    }
    
    /**
     * Creates the list of packets needed for container initialization, excluding the open screen packet.
     *
     * @return The list of packets
     */
    private List<Packet<? super ClientGamePacketListener>> createContainerInitPacketList() {
        var packets = new ArrayList<Packet<? super ClientGamePacketListener>>();
        packets.add(new ClientboundContainerSetContentPacket(containerId, incrementStateId(), items, carried));
        for (int i = 0; i < dataSlots.length; i++) {
            packets.add(new ClientboundContainerSetDataPacket(containerId, i, dataSlots[i]));
        }
        return packets;
    }
    
    /**
     * Marks the current state as synced with the remote client.
     */
    private void markRemoteSynced() {
        for (int i = 0; i < items.size(); i++) {
            remoteItems.set(i, items.get(i).copy());
        }
        remoteCarried = carried.copy();
        System.arraycopy(dataSlots, 0, remoteDataSlots, 0, dataSlots.length);
    }
    //</editor-fold>
    
    /**
     * Opens this menu for the player.
     *
     * @param title The title of the inventory
     */
    public void open(Component title) {
        var pl = PacketListener.getInstance();
        pl.redirectIncoming(player, ServerboundContainerButtonClickPacket.class, p -> handleButtonClick(p.buttonId()));
        pl.redirectIncoming(player, ServerboundContainerClickPacket.class, this::handleClick);
        pl.redirectIncoming(player, ServerboundContainerClosePacket.class, this::handleClose);
        pl.redirectIncoming(player, ServerboundSelectBundleItemPacket.class, this::handleBundleSelect);
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
        pl.stopDiscard(player, ClientboundOpenScreenPacket.class);
        pl.stopDiscard(player, ClientboundContainerSetContentPacket.class);
        pl.stopDiscard(player, ClientboundContainerSetDataPacket.class);
        pl.stopDiscard(player, ClientboundContainerSetSlotPacket.class);
        
        // TODO: handle cursor item (drop?)
        
        // transfer remote items state to inventory menu
        for (int i = 0; i < LOWER_INVENTORY_SIZE; i++) {
            var item = remoteItems.get((remoteItems.size() - LOWER_INVENTORY_SIZE) + i);
            // in the inventory menu, contents start at 9
            serverPlayer.inventoryMenu.setRemoteSlot(9 + i, item);
        }
        serverPlayer.inventoryMenu.setRemoteSlot(OFF_HAND_SLOT, remoteOffHand);
        
        serverPlayer.containerMenu = serverPlayer.inventoryMenu;
    }
    
    //<editor-fold desc="action handlers">
    
    /**
     * Handles a client-initiated inventory close.
     *
     * @param packet The packet that was received
     */
    private void handleClose(ServerboundContainerClosePacket packet) {
        if (packet.getContainerId() != containerId)
            return;
        
        var window = getWindow();
        if (window.isCloseable()) {
            window.handleClose();
        } else {
            sendOpenPacket(title);
        }
    }
    
    /**
     * Handles a client-initiated inventory click.
     * See <a href="https://minecraft.wiki/w/Java_Edition_protocol#Click_Container">Click Container</a> for more information.
     *
     * @param packet The packet that was received
     */
    private void handleClick(ServerboundContainerClickPacket packet) {
        boolean requiresFullUpdate = packet.getStateId() != stateId;
        
        // update remote slots
        for (var entry : packet.getChangedSlots().int2ObjectEntrySet()) {
            int slot = entry.getIntKey();
            if (slot < 0 || slot > remoteItems.size())
                continue;
            remoteItems.set(entry.getIntKey(), entry.getValue());
        }
        remoteCarried = DIRTY_MARKER;
        
        if (packet.getClickType() == net.minecraft.world.inventory.ClickType.QUICK_CRAFT) {
            // item dragging is split into multiple packets:
            // - drag start
            // - place item (one packet per item)
            // - drag end
            switch (packet.getButtonNum()) {
                // add slot for left, right, middle drag
                case 1, 5, 9 -> {
                    var slot = packet.getSlotNum();
                    if (slot >= 0 && slot < items.size()) {
                        dragSlots.add(packet.getSlotNum());
                    }
                    
                    dragMode = switch (packet.getButtonNum()) {
                        case 1 -> ClickType.LEFT;
                        case 5 -> ClickType.RIGHT;
                        case 9 -> ClickType.MIDDLE;
                        default -> throw new AssertionError();
                    };
                    
                    return; // no update required, further quick_craft packets incoming
                }
                
                // end left, right, middle drag
                case 2, 6, 10 -> {
                    runInInteractionContext(() -> {
                        if (dragSlots.size() == 1) {
                            // handle one slot drags as simple clicks
                            int slot = dragSlots.iterator().nextInt();
                            getWindow().handleClick(slot, new Click(player, dragMode, -1));
                        } else {
                            getWindow().handleDrag(dragSlots, dragMode);
                            
                        }
                    });
                    dragSlots.clear();
                }
                
                // drag start can be ignored
                default -> {
                    return; // no update required, further quick_craft packets incoming
                }
            }
        } else {
            int hotbarBtn = -1;
            ClickType clickType = switch (packet.getClickType()) {
                case PICKUP -> switch (packet.getButtonNum()) {
                    case 0 -> ClickType.LEFT;
                    case 1 -> ClickType.RIGHT;
                    default -> ClickType.UNKNOWN;
                };
                
                case QUICK_MOVE -> switch (packet.getButtonNum()) {
                    case 0 -> ClickType.SHIFT_LEFT;
                    case 1 -> ClickType.SHIFT_RIGHT;
                    default -> ClickType.UNKNOWN;
                };
                
                case SWAP -> switch (packet.getButtonNum()) {
                    case 0, 1, 2, 3, 4, 5, 6, 7, 8 -> {
                        hotbarBtn = packet.getButtonNum();
                        yield ClickType.NUMBER_KEY;
                    }
                    case 40 -> {
                        remoteOffHand = DIRTY_MARKER;
                        yield ClickType.SWAP_OFFHAND;
                    }
                    default -> ClickType.UNKNOWN;
                };
                
                case CLONE -> ClickType.MIDDLE;
                
                case THROW -> switch (packet.getButtonNum()) {
                    case 0 -> ClickType.DROP;
                    case 1 -> ClickType.CONTROL_DROP;
                    default -> ClickType.UNKNOWN;
                };
                
                case PICKUP_ALL -> ClickType.DOUBLE_CLICK;
                
                case QUICK_CRAFT -> throw new AssertionError(); // should never be reached
            };
            
            // let window handle the click
            int finalHotbarBtn = hotbarBtn;
            runInInteractionContext(() -> {
                Click click = new Click(player, clickType, finalHotbarBtn);
                getWindow().handleClick(packet.getSlotNum(), click);
            });
        }
        
        // syncs server and client state, if necessary
        sendToRemote(requiresFullUpdate);
    }
    
    private void handleBundleSelect(ServerboundSelectBundleItemPacket packet) {
        // verify legal slot
        int slot = packet.slotId();
        if (slot < 0 || slot >= items.size())
            return;
        
        // verify bundle at slot
        var bundle = remoteItems.get(slot);
        var bundleContents = bundle.get(DataComponents.BUNDLE_CONTENTS);
        if (bundleContents == null)
            return;
        
        // update remote item to expected selected item index
        var mutableBundleContents = new BundleContents.Mutable(bundleContents);
        mutableBundleContents.toggleSelectedItem(packet.selectedItemIndex());
        bundle.set(DataComponents.BUNDLE_CONTENTS, mutableBundleContents.toImmutable());
        
        // let window handle the selection
        runInInteractionContext(() -> getWindow().handleBundleSelect(slot, packet.selectedItemIndex()));
        
        // syncs server and client state
        sendToRemote(false);
    }
    
    /**
     * Handles a client-initiated menu button click.
     *
     * @param buttonId The id of the button that was clicked
     */
    protected void handleButtonClick(int buttonId) {
    }
    
    private void runInInteractionContext(Runnable run) {
        try {
            interactionContext.set(true);
            run.run();
        } catch (Throwable t) {
            InvUI.getInstance().getLogger().log(Level.SEVERE, "An exception occurred while handling a window interaction", t);
        } finally {
            interactionContext.set(false);
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
     * Sets the {@link AbstractWindow} that is associated with this menu.
     *
     * @param window The {@link AbstractWindow}
     */
    public void setWindow(AbstractWindow<?> window) {
        this.window = window;
    }
    
    /**
     * Gets the {@link AbstractWindow} that is associated with this menu.
     *
     * @return The {@link AbstractWindow}
     * @throws IllegalStateException If the window is not {@link #setWindow(AbstractWindow) set}
     */
    public AbstractWindow<?> getWindow() {
        if (window == null)
            throw new IllegalStateException("Window is not set");
        return window;
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
            return new CraftInventoryView<>(player, bukkitInventory, this);
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
