package xyz.xenondevs.invui.inventory;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.inventory.event.ItemPostUpdateEvent;
import xyz.xenondevs.invui.inventory.event.ItemPreUpdateEvent;
import xyz.xenondevs.invui.inventory.event.UpdateReason;

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.IntPredicate;

/**
 * An {@link Inventory} that delegates to another {@link Inventory} while hiding certain slots.
 */
public class ObscuredInventory extends Inventory {
    
    private final @NotNull Inventory inventory;
    private final int @NotNull [] slots;
    
    /**
     * Constructs a new {@link ObscuredInventory}.
     *
     * @param inventory  The {@link Inventory} to delegate to.
     * @param isObscured A {@link IntPredicate} that returns true for slots that should be hidden.
     */
    public ObscuredInventory(@NotNull Inventory inventory, @NotNull IntPredicate isObscured) {
        this.inventory = inventory;
        
        ArrayList<Integer> slots = new ArrayList<>();
        for (int slot = 0; slot < inventory.getSize(); slot++) {
            if (isObscured.test(slot))
                continue;
            
            slots.add(slot);
        }
        
        this.slots = slots.stream().mapToInt(Integer::intValue).toArray();
    }
    
    @Override
    public int getSize() {
        return slots.length;
    }
    
    @Override
    public int @NotNull [] getMaxStackSizes() {
        int[] maxStackSizes = new int[slots.length];
        for (int i = 0; i < slots.length; i++) {
            maxStackSizes[i] = inventory.getMaxSlotStackSize(slots[i]);
        }
        return maxStackSizes;
    }
    
    @Override
    public int getMaxSlotStackSize(int slot) {
        return inventory.getMaxSlotStackSize(slots[slot]);
    }
    
    @Override
    public @Nullable ItemStack @NotNull [] getItems() {
        ItemStack[] items = new ItemStack[slots.length];
        for (int i = 0; i < slots.length; i++) {
            items[i] = inventory.getItem(slots[i]);
        }
        return items;
    }
    
    @Override
    public @Nullable ItemStack @NotNull [] getUnsafeItems() {
        ItemStack[] items = new ItemStack[slots.length];
        for (int i = 0; i < slots.length; i++) {
            items[i] = inventory.getUnsafeItem(slots[i]);
        }
        return items;
    }
    
    @Override
    public @Nullable ItemStack getItem(int slot) {
        return inventory.getItem(slots[slot]);
    }
    
    @Override
    public @Nullable ItemStack getUnsafeItem(int slot) {
        return inventory.getUnsafeItem(slots[slot]);
    }
    
    @Override
    protected void setCloneBackingItem(int slot, @Nullable ItemStack itemStack) {
        inventory.setCloneBackingItem(slots[slot], itemStack);
    }
    
    @Override
    protected void setDirectBackingItem(int slot, @Nullable ItemStack itemStack) {
        inventory.setDirectBackingItem(slots[slot], itemStack);
    }
    
    @Override
    public void notifyWindows() {
        super.notifyWindows();
        inventory.notifyWindows();
    }
    
    @Override
    public ItemPreUpdateEvent callPreUpdateEvent(@Nullable UpdateReason updateReason, int slot, @Nullable ItemStack previousItemStack, @Nullable ItemStack newItemStack) {
        return inventory.callPreUpdateEvent(updateReason, slots[slot], previousItemStack, newItemStack);
    }
    
    @Override
    public void callPostUpdateEvent(@Nullable UpdateReason updateReason, int slot, @Nullable ItemStack previousItemStack, @Nullable ItemStack newItemStack) {
        inventory.callPostUpdateEvent(updateReason, slots[slot], previousItemStack, newItemStack);
    }
    
    @Override
    public boolean hasEventHandlers() {
        return inventory.hasEventHandlers();
    }
    
    @Override
    public void setPostUpdateHandler(@Nullable Consumer<@NotNull ItemPostUpdateEvent> inventoryUpdatedHandler) {
        throw new UnsupportedOperationException("Update handlers need to be set in the backing inventory");
    }
    
    @Override
    public void setPreUpdateHandler(@Nullable Consumer<@NotNull ItemPreUpdateEvent> preUpdateHandler) {
        throw new UnsupportedOperationException("Update handlers need to be set in the backing inventory");
    }
    
    @Override
    public int getGuiPriority() {
        return inventory.getGuiPriority();
    }
    
    @Override
    public void setGuiPriority(int guiPriority) {
        throw new UnsupportedOperationException("Gui priority needs to be set in the backing inventory");
    }
    
}
