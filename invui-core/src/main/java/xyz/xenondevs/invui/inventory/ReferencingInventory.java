package xyz.xenondevs.invui.inventory;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.util.TriConsumer;
import xyz.xenondevs.invui.window.Window;

import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A {@link xyz.xenondevs.invui.inventory.Inventory} which is backed by a bukkit {@link Inventory}.
 * <p>
 * Changes in this inventory are applied in the referenced inventory and changes in the bukkit inventory are visible
 * in this inventory.
 * <p>
 * Changes done using the methods provided by {@link xyz.xenondevs.invui.inventory.Inventory} will cause displaying
 * {@link Window Windows} to be {@link xyz.xenondevs.invui.inventory.Inventory#notifyWindows() notified}, but changes
 * done directly in the bukkit inventory will not. Therefore, if embedded in a {@link Gui}, it is necessary to call
 * {@link xyz.xenondevs.invui.inventory.Inventory#notifyWindows()} manually in order for changes to be displayed.
 */
public class ReferencingInventory extends xyz.xenondevs.invui.inventory.Inventory {
    
    private static final int MAX_STACK_SIZE = 64;
    
    protected final @NotNull Inventory inventory;
    protected final @NotNull Function<@NotNull Inventory, @Nullable ItemStack @NotNull []> itemsGetter;
    protected final @NotNull BiFunction<@NotNull Inventory, @NotNull Integer, @Nullable ItemStack> itemGetter;
    protected final @NotNull TriConsumer<@NotNull Inventory, @NotNull Integer, @Nullable ItemStack> itemSetter;
    protected final int size;
    protected final int @NotNull [] maxStackSizes;
    
    /**
     * Constructs a new {@link ReferencingInventory}.
     *
     * @param inventory   The {@link Inventory} to reference.
     * @param itemsGetter A {@link Function} which returns a copy of the {@link ItemStack ItemStacks} of the {@link Inventory}.
     * @param itemGetter  A {@link BiFunction} which returns a copy of the {@link ItemStack} on the specified slot.
     * @param itemSetter  A {@link TriConsumer} which copies and then sets the {@link ItemStack} on the specified slot.
     */
    public ReferencingInventory(
        @NotNull Inventory inventory,
        @NotNull Function<@NotNull Inventory, @Nullable ItemStack @NotNull []> itemsGetter,
        @NotNull BiFunction<@NotNull Inventory, @NotNull Integer, @Nullable ItemStack> itemGetter,
        @NotNull TriConsumer<@NotNull Inventory, @NotNull Integer, @Nullable ItemStack> itemSetter
    ) {
        this.inventory = inventory;
        this.itemsGetter = itemsGetter;
        this.itemGetter = itemGetter;
        this.itemSetter = itemSetter;
        this.size = itemsGetter.apply(inventory).length;
        this.maxStackSizes = new int[size];
        Arrays.fill(maxStackSizes, MAX_STACK_SIZE);
    }
    
    /**
     * Creates a new {@link ReferencingInventory} which references only the {@link Inventory#getStorageContents() storage contents} of the specified {@link Inventory}.
     *
     * @param inventory The {@link Inventory} to reference.
     * @return The new {@link ReferencingInventory}.
     */
    public static @NotNull ReferencingInventory fromStorageContents(@NotNull Inventory inventory) {
        return new ReferencingInventory(inventory, Inventory::getStorageContents, Inventory::getItem, Inventory::setItem);
    }
    
    /**
     * Creates a new {@link ReferencingInventory} which references all {@link ItemStack ItemStacks} of the specified {@link Inventory}.
     *
     * @param inventory The {@link Inventory} to reference.
     * @return The new {@link ReferencingInventory}.
     */
    public static @NotNull ReferencingInventory fromContents(@NotNull Inventory inventory) {
        return new ReferencingInventory(inventory, Inventory::getContents, Inventory::getItem, Inventory::setItem);
    }
    
    /**
     * Creates a new {@link ReferencingInventory} with a reversed view of the {@link PlayerInventory PlayerInventory's}
     * {@link Inventory#getStorageContents() storage contents}, where the last hotbar slot is the first slot and
     * the top left slot is the last slot.
     *
     * @param inventory The {@link PlayerInventory} to reference.
     * @return The new {@link ReferencingInventory}.
     */
    public static @NotNull ReferencingInventory fromReversedPlayerStorageContents(@NotNull PlayerInventory inventory) {
        return new ReversedPlayerContents(inventory);
    }
    
    @Override
    public int getSize() {
        return size;
    }
    
    @Override
    public int @NotNull [] getMaxStackSizes() {
        return maxStackSizes.clone();
    }
    
    @Override
    public int getMaxSlotStackSize(int slot) {
        return MAX_STACK_SIZE;
    }
    
    @Override
    public @Nullable ItemStack @NotNull [] getItems() {
        return itemsGetter.apply(inventory);
    }
    
    @Override
    public @Nullable ItemStack @NotNull [] getUnsafeItems() {
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
    
    private static class ReversedPlayerContents extends ReferencingInventory {
        
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
        public @Nullable ItemStack @NotNull [] getUnsafeItems() {
            return getItems();
        }
        
        @Override
        public @Nullable ItemStack @NotNull [] getItems() {
            ItemStack[] items = itemsGetter.apply(inventory);
            ItemStack[] reorderedItems = new ItemStack[items.length];
            
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
    
}
