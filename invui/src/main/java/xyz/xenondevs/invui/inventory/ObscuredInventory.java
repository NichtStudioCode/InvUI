package xyz.xenondevs.invui.inventory;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.inventory.event.ItemPostUpdateEvent;
import xyz.xenondevs.invui.inventory.event.ItemPreUpdateEvent;
import xyz.xenondevs.invui.inventory.event.UpdateReason;
import xyz.xenondevs.invui.window.AbstractWindow;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.IntPredicate;

/**
 * An {@link Inventory} that delegates to another {@link Inventory} while hiding certain slots.
 */
public final class ObscuredInventory extends Inventory {
    
    private final Inventory inventory;
    private final int[] slots;
    
    /**
     * Constructs a new {@link ObscuredInventory}.
     *
     * @param inventory  The {@link Inventory} to delegate to.
     * @param isObscured A {@link IntPredicate} that returns true for slots that should be hidden.
     */
    public ObscuredInventory(Inventory inventory, IntPredicate isObscured) {
        super(calculateSize(inventory, isObscured));
        this.inventory = inventory;
        
        ArrayList<Integer> slots = new ArrayList<>();
        for (int slot = 0; slot < inventory.getSize(); slot++) {
            if (isObscured.test(slot))
                continue;
            
            slots.add(slot);
        }
        
        this.slots = slots.stream().mapToInt(Integer::intValue).toArray();
    }
    
    private static int calculateSize(Inventory inventory, IntPredicate isObscured) {
        int size = 0;
        for (int slot = 0; slot < inventory.getSize(); slot++) {
            if (!isObscured.test(slot))
                size++;
        }
        return size;
    }
    
    @Override
    public int[] getMaxStackSizes() {
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
    public @Nullable ItemStack[] getItems() {
        @Nullable ItemStack[] items = new ItemStack[slots.length];
        for (int i = 0; i < slots.length; i++) {
            items[i] = inventory.getItem(slots[i]);
        }
        return items;
    }
    
    @Override
    public @Nullable ItemStack[] getUnsafeItems() {
        @Nullable ItemStack[] items = new ItemStack[slots.length];
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
        inventory.notifyWindows();
    }
    
    @Override
    public void notifyWindows(int slot) {
        inventory.notifyWindows(slots[slot]);
    }
    
    @Override
    public void addViewer(AbstractWindow viewer, int what, int how) {
        inventory.addViewer(viewer, what, how);
    }
    
    @Override
    public void removeViewer(AbstractWindow viewer, int what, int how) {
        inventory.removeViewer(viewer, what, how);
    }
    
    @Override
    public void callClickEvent(int slot, InventoryClickEvent event) {
        inventory.callClickEvent(slots[slot], event);
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
    public int getGuiPriority() {
        return inventory.getGuiPriority();
    }
    
    @Override
    public void setGuiPriority(int guiPriority) {
        throw new UnsupportedOperationException("Gui priority needs to be set in the backing inventory");
    }
    
    @Override
    public List<BiConsumer<Integer, InventoryClickEvent>> getClickHandlers() {
        throw new UnsupportedOperationException("Click handlers need to be set in the backing inventory");
    }
    
    @Override
    public void setClickHandlers(List<BiConsumer<Integer, InventoryClickEvent>> clickHandlers) {
        throw new UnsupportedOperationException("Click handlers need to be set in the backing inventory");
    }
    
    @Override
    public void addClickHandler(BiConsumer<Integer, InventoryClickEvent> clickHandler) {
        throw new UnsupportedOperationException("Click handlers need to be set in the backing inventory");
    }
    
    @Override
    public void removeClickHandler(BiConsumer<Integer, InventoryClickEvent> clickHandler) {
        throw new UnsupportedOperationException("Click handlers need to be set in the backing inventory");
    }
    
    @Override
    public List<Consumer<ItemPreUpdateEvent>> getPreUpdateHandlers() {
        throw new UnsupportedOperationException("Update handlers need to be set in the backing inventory");
    }
    
    @Override
    public void setPreUpdateHandlers(List<Consumer<ItemPreUpdateEvent>> preUpdateHandlers) {
        throw new UnsupportedOperationException("Update handlers need to be set in the backing inventory");
    }
    
    @Override
    public void addPreUpdateHandler(Consumer<ItemPreUpdateEvent> preUpdateHandler) {
        throw new UnsupportedOperationException("Update handlers need to be set in the backing inventory");
    }
    
    @Override
    public void removePreUpdateHandler(Consumer<ItemPreUpdateEvent> preUpdateHandler) {
        throw new UnsupportedOperationException("Update handlers need to be set in the backing inventory");
    }
    
    @Override
    public List<Consumer<ItemPostUpdateEvent>> getPostUpdateHandlers() {
        throw new UnsupportedOperationException("Update handlers need to be set in the backing inventory");
    }
    
    @Override
    public void setPostUpdateHandlers(List<Consumer<ItemPostUpdateEvent>> postUpdateHandlers) {
        throw new UnsupportedOperationException("Update handlers need to be set in the backing inventory");
    }
    
    @Override
    public void addPostUpdateHandler(Consumer<ItemPostUpdateEvent> postUpdateHandler) {
        throw new UnsupportedOperationException("Update handlers need to be set in the backing inventory");
    }
    
    @Override
    public void removePostUpdateHandler(Consumer<ItemPostUpdateEvent> postUpdateHandler) {
        throw new UnsupportedOperationException("Update handlers need to be set in the backing inventory");
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public void setPostUpdateHandler(@Nullable Consumer<ItemPostUpdateEvent> inventoryUpdatedHandler) {
        throw new UnsupportedOperationException("Update handlers need to be set in the backing inventory");
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public void setPreUpdateHandler(@Nullable Consumer<ItemPreUpdateEvent> preUpdateHandler) {
        throw new UnsupportedOperationException("Update handlers need to be set in the backing inventory");
    }
    
}
