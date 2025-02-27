package xyz.xenondevs.invui.inventory;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitTask;
import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.InvUI;
import xyz.xenondevs.invui.util.TriConsumer;
import xyz.xenondevs.invui.window.AbstractWindow;

import java.util.Arrays;
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
    
    private @Nullable BukkitTask updateTask;
    
    /**
     * Constructs a new {@link ReferencingInventory}.
     *
     * @param inventory   The {@link Inventory} to reference.
     * @param itemsGetter A {@link Function} which returns a copy of the {@link ItemStack ItemStacks} of the {@link Inventory}.
     * @param itemGetter  A {@link BiFunction} which returns a copy of the {@link ItemStack} on the specified slot.
     * @param itemSetter  A {@link TriConsumer} which copies and then sets the {@link ItemStack} on the specified slot.
     */
    public ReferencingInventory(
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
    public static ReferencingInventory fromPlayerStorageContents(PlayerInventory inventory) {
        return new PlayerStorageContents(inventory);
    }
    
    /**
     * Creates a new {@link ReferencingInventory} with a reversed view of the {@link PlayerInventory PlayerInventory's}
     * {@link Inventory#getStorageContents() storage contents}, where the last hotbar slot is the first slot and
     * the top left slot is the last slot.
     *
     * @param inventory The {@link PlayerInventory} to reference.
     * @return The new {@link ReferencingInventory}.
     */
    public static ReferencingInventory fromReversedPlayerStorageContents(PlayerInventory inventory) {
        return new ReversedPlayerContents(inventory);
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
        return itemsGetter.apply(inventory);
    }
    
    @Override
    public @Nullable ItemStack[] getUnsafeItems() {
        return itemsGetter.apply(inventory);
    }
    
    @Override
    public @Nullable ItemStack getItem(int slot) {
        return itemGetter.apply(inventory, slot);
    }
    
    @Override
    public @Nullable ItemStack getUnsafeItem(int slot) {
        return itemGetter.apply(inventory, slot);
    }
    
    @Override
    protected void setCloneBackingItem(int slot, @Nullable ItemStack itemStack) {
        itemSetter.accept(inventory, slot, itemStack);
    }
    
    @Override
    protected void setDirectBackingItem(int slot, @Nullable ItemStack itemStack) {
        itemSetter.accept(inventory, slot, itemStack);
    }
    
    // TODO: make this work with delegating inventories
    @Override
    public void addViewer(AbstractWindow<?> viewer, int what, int how) {
        super.addViewer(viewer, what, how);
        if (updateTask == null) {
            updateTask = Bukkit.getScheduler().runTaskTimer(
                InvUI.getInstance().getPlugin(),
                () -> notifyWindows(),
                0, 1
            );
        }
    }
    
    // TODO: make this work with delegating inventories
    @Override
    public void removeViewer(AbstractWindow<?> viewer, int what, int how) {
        super.removeViewer(viewer, what, how);
        if (updateTask != null
            && Arrays.stream(viewers).allMatch(s -> s == null || s.isEmpty())
        ) {
            updateTask.cancel();
            updateTask = null;
        }
    }
    
    private static final class ReversedPlayerContents extends ReferencingInventory {
        
        public ReversedPlayerContents(PlayerInventory inventory) {
            super(inventory, Inventory::getStorageContents, Inventory::getItem, Inventory::setItem);
        }
        
        private int convertSlot(int invUiSlot) {
            if (invUiSlot < 9) return 8 - invUiSlot;
            else return 44 - invUiSlot;
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
            return getItems();
        }
        
        @Override
        public @Nullable ItemStack[] getItems() {
            @Nullable ItemStack[] items = itemsGetter.apply(inventory);
            @Nullable ItemStack[] reorderedItems = new ItemStack[items.length];
            
            for (int i = 0; i < 9; i++) {
                reorderedItems[8 - i] = items[i];
            }
            
            for (int i = 9; i < 36; i++) {
                reorderedItems[44 - i] = items[i];
            }
            
            return reorderedItems;
        }
        
        @Override
        protected void setCloneBackingItem(int slot, @Nullable ItemStack itemStack) {
            super.setCloneBackingItem(convertSlot(slot), itemStack);
        }
        
        @Override
        protected void setDirectBackingItem(int slot, @Nullable ItemStack itemStack) {
            super.setDirectBackingItem(convertSlot(slot), itemStack);
        }
        
    }
    
    private static final class PlayerStorageContents extends ReferencingInventory {
        
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
            return getItems();
        }
        
        @Override
        public @Nullable ItemStack[] getItems() {
            @Nullable ItemStack[] items = itemsGetter.apply(inventory);
            @Nullable ItemStack[] reorderedItems = new ItemStack[items.length];
            
            System.arraycopy(items, 0, reorderedItems, 27, 9);
            System.arraycopy(items, 9, reorderedItems, 0, 27);
            
            return reorderedItems;
        }
        
        @Override
        protected void setCloneBackingItem(int slot, @Nullable ItemStack itemStack) {
            super.setCloneBackingItem(convertSlot(slot), itemStack);
        }
        
        @Override
        protected void setDirectBackingItem(int slot, @Nullable ItemStack itemStack) {
            super.setDirectBackingItem(convertSlot(slot), itemStack);
        }
        
    }
    
}
