package xyz.xenondevs.invui.inventory;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.Click;
import xyz.xenondevs.invui.InvUI;
import xyz.xenondevs.invui.Observer;
import xyz.xenondevs.invui.internal.util.FakeInventoryView;
import xyz.xenondevs.invui.util.ItemUtils;
import xyz.xenondevs.invui.util.TriConsumer;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A {@link xyz.xenondevs.invui.inventory.Inventory} which is backed by a bukkit {@link Inventory}.
 * <p>
 * Changes in this inventory are applied in the referenced inventory and changes in the bukkit inventory are visible
 * in this inventory.
 */
public sealed class ReferencingInventory extends xyz.xenondevs.invui.inventory.Inventory {
    
    private static final int MAX_STACK_SIZE = 64;
    
    protected final Inventory inventory;
    protected final Function<Inventory, @Nullable ItemStack[]> itemsGetter;
    protected final BiFunction<Inventory, Integer, @Nullable ItemStack> itemGetter;
    protected final TriConsumer<Inventory, Integer, @Nullable ItemStack> itemSetter;
    protected final int[] maxStackSizes;
    
    private @Nullable ScheduledTask updateTask;
    
    /**
     * Constructs a new {@link ReferencingInventory}.
     *
     * @param inventory   The {@link Inventory} to reference.
     * @param itemsGetter A {@link Function} which returns a copy of the {@link ItemStack ItemStacks} of the {@link Inventory}.
     * @param itemGetter  A {@link BiFunction} which returns a copy of the {@link ItemStack} on the specified slot.
     * @param itemSetter  A {@link TriConsumer} which copies and then sets the {@link ItemStack} on the specified slot.
     */
    private ReferencingInventory(
        Inventory inventory,
        Function<Inventory, @Nullable ItemStack[]> itemsGetter,
        BiFunction<Inventory, Integer, @Nullable ItemStack> itemGetter,
        TriConsumer<Inventory, Integer, @Nullable ItemStack> itemSetter
    ) {
        super(itemsGetter.apply(inventory).length);
        this.inventory = inventory;
        this.itemsGetter = itemsGetter;
        this.itemGetter = itemGetter;
        this.itemSetter = itemSetter;
        this.maxStackSizes = new int[size];
        Arrays.fill(maxStackSizes, MAX_STACK_SIZE);
    }
    
    /**
     * Creates a new {@link ReferencingInventory} which references only the {@link Inventory#getStorageContents() storage contents} of the specified {@link Inventory}.
     *
     * @param inventory The {@link Inventory} to reference.
     * @return The new {@link ReferencingInventory}.
     */
    public static ReferencingInventory fromStorageContents(Inventory inventory) {
        return new ReferencingInventory(inventory, Inventory::getStorageContents, Inventory::getItem, Inventory::setItem);
    }
    
    /**
     * Creates a new {@link ReferencingInventory} which references all {@link ItemStack ItemStacks} of the specified {@link Inventory}.
     *
     * @param inventory The {@link Inventory} to reference.
     * @return The new {@link ReferencingInventory}.
     */
    public static ReferencingInventory fromContents(Inventory inventory) {
        return new ReferencingInventory(inventory, Inventory::getContents, Inventory::getItem, Inventory::setItem);
    }
    
    /**
     * Creates a new {@link ReferencingInventory} with a view of the {@link PlayerInventory PlayerInventory's}
     * {@link Inventory#getStorageContents() storage contents}, where the hotbar slots are the last nine slots.
     *
     * @param inventory The {@link PlayerInventory} to reference.
     * @return The new {@link ReferencingInventory}.
     */
    public static ReferencingInventory.PlayerStorageContents fromPlayerStorageContents(PlayerInventory inventory) {
        return new PlayerStorageContents(inventory);
    }
    
    @Override
    public int[] getMaxStackSizes() {
        return maxStackSizes.clone();
    }
    
    @Override
    public int getMaxSlotStackSize(int slot) {
        return MAX_STACK_SIZE;
    }
    
    @Override
    public @Nullable ItemStack[] getItems() {
        return ItemUtils.clone(itemsGetter.apply(inventory));
    }
    
    @Override
    public @Nullable ItemStack[] getUnsafeItems() {
        return itemsGetter.apply(inventory);
    }
    
    @Override
    public @Nullable ItemStack getItem(int slot) {
        return ItemUtils.cloneUnlessEmpty(itemGetter.apply(inventory, slot));
    }
    
    @Override
    public @Nullable ItemStack getUnsafeItem(int slot) {
        return ItemUtils.takeUnlessEmpty(itemGetter.apply(inventory, slot));
    }
    
    @Override
    protected void setCloneBackingItem(int slot, @Nullable ItemStack itemStack) {
        itemSetter.accept(inventory, slot, ItemUtils.cloneUnlessEmpty(itemStack));
    }
    
    @Override
    protected void setDirectBackingItem(int slot, @Nullable ItemStack itemStack) {
        itemSetter.accept(inventory, slot, ItemUtils.takeUnlessEmpty(itemStack));
    }
    
    @Override
    public void addObserver(Observer who, int what, int how) {
        super.addObserver(who, what, how);
        if (updateTask == null) {
            updateTask = Bukkit.getAsyncScheduler().runAtFixedRate(
                InvUI.getInstance().getPlugin(),
                x -> notifyWindows(),
                0,
                50,
                TimeUnit.MILLISECONDS
            );
        }
    }
    
    @Override
    public void removeObserver(Observer who, int what, int how) {
        super.removeObserver(who, what, how);
        if (updateTask != null
            && Arrays.stream(observers).allMatch(s -> s == null || s.isEmpty())
        ) {
            updateTask.cancel();
            updateTask = null;
        }
    }
    
    @SuppressWarnings("UnstableApiUsage")
    @Override
    protected boolean callClickEvent(int slot, Click click, InventoryAction action, boolean cancelled) {
        cancelled = super.callClickEvent(slot, click, action, cancelled);
        if (!InvUI.getInstance().isFireBukkitInventoryEvents())
            return cancelled;
        
        var player = click.player();
        InventoryClickEvent bukkitEvent;
        if (this instanceof PlayerStorageContents && inventory == player.getInventory()) {
            // for the player's own inventory, we use the bottom inventory of the view, regardless where the inventory is embedded,
            // as many plugins will assume the player inventory to be at the bottom
            var openView = player.getOpenInventory();
            int rawSlot = openView.getTopInventory().getSize() + slot;
            bukkitEvent = new InventoryClickEvent(
                openView,
                openView.getSlotType(rawSlot),
                rawSlot,
                click.clickType(),
                action,
                click.hotbarButton()
            );
        } else {
            // all other inventories are put at the top of the view, this requires a fake view
            // does not unwrap referencing inventories, since they may reorder slots
            bukkitEvent = new InventoryClickEvent(
                new FakeInventoryView(player, asBukkitInventory()),
                InventoryType.SlotType.CONTAINER,
                slot,
                click.clickType(),
                action,
                click.hotbarButton()
            );
        }
        
        bukkitEvent.setCancelled(cancelled);
        Bukkit.getPluginManager().callEvent(bukkitEvent);
        return bukkitEvent.isCancelled();
    }
    
    /**
     * Gets the referenced Bukkit {@link Inventory}.
     *
     * @return The referenced Bukkit {@link Inventory}.
     */
    public Inventory getReferencedInventory() {
        return inventory;
    }
    
    /**
     * A {@link ReferencingInventory} which references a {@link PlayerInventory}'s
     * {@link Inventory#getStorageContents() storage contents}, with hotbar slot reordering such that
     * the hotbar slots are the last nine slots.
     */
    public static final class PlayerStorageContents extends ReferencingInventory {
        
        public PlayerStorageContents(PlayerInventory inventory) {
            super(inventory, Inventory::getStorageContents, Inventory::getItem, Inventory::setItem);
        }
        
        private int convertSlot(int invUiSlot) {
            return (invUiSlot + 9) % 36;
        }
        
        @Override
        public @Nullable ItemStack getItem(int slot) {
            return super.getItem(convertSlot(slot));
        }
        
        @Override
        public @Nullable ItemStack getUnsafeItem(int slot) {
            return super.getUnsafeItem(convertSlot(slot));
        }
        
        @Override
        public @Nullable ItemStack[] getUnsafeItems() {
            @Nullable ItemStack[] items = itemsGetter.apply(inventory);
            @Nullable ItemStack[] reorderedItems = new ItemStack[items.length];
            
            System.arraycopy(items, 0, reorderedItems, 27, 9);
            System.arraycopy(items, 9, reorderedItems, 0, 27);
            
            return reorderedItems;
        }
        
        @Override
        public @Nullable ItemStack[] getItems() {
            return ItemUtils.clone(getUnsafeItems());
        }
        
        @Override
        protected void setCloneBackingItem(int slot, @Nullable ItemStack itemStack) {
            super.setCloneBackingItem(convertSlot(slot), itemStack);
        }
        
        @Override
        protected void setDirectBackingItem(int slot, @Nullable ItemStack itemStack) {
            super.setDirectBackingItem(convertSlot(slot), itemStack);
        }
        
        @Override
        public PlayerInventory getReferencedInventory() {
            return (PlayerInventory) super.getReferencedInventory();
        }
    }
    
}
