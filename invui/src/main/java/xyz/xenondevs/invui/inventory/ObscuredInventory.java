package xyz.xenondevs.invui.inventory;

import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.Click;
import xyz.xenondevs.invui.InvUI;
import xyz.xenondevs.invui.Observer;
import xyz.xenondevs.invui.internal.util.ArrayUtils;
import xyz.xenondevs.invui.inventory.event.ItemPreUpdateEvent;
import xyz.xenondevs.invui.inventory.event.UpdateReason;

import java.util.EnumSet;
import java.util.Set;
import java.util.function.IntPredicate;
import java.util.stream.IntStream;

/**
 * An {@link Inventory} that delegates to another {@link Inventory} while hiding certain slots.
 */
public final class ObscuredInventory extends Inventory {
    
    private final Inventory inventory;
    private final int[] slots; // this slot -> delegate slot
    private final int[] inverseSlots; // delegate slot -> this slot
    private final Set<OperationCategory> guiPriorityOverrides = EnumSet.noneOf(OperationCategory.class);
    
    /**
     * Constructs a new {@link ObscuredInventory}.
     *
     * @param inventory  The {@link Inventory} to delegate to.
     * @param isObscured A {@link IntPredicate} that returns true for slots that should be hidden.
     */
    public ObscuredInventory(Inventory inventory, IntPredicate isObscured) {
        super(calculateSize(inventory, isObscured));
        this.inventory = inventory;
        this.slots = IntStream.range(0, inventory.getSize())
            .filter(slot -> !isObscured.test(slot))
            .toArray();
        this.inverseSlots = ArrayUtils.newIntArray(inventory.getSize(), -1);
        for (int i = 0; i < this.slots.length; i++) {
            inverseSlots[this.slots[i]] = i;
        }
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
    public int[] getIterationOrder(OperationCategory category) {
        int[] obscuredIterationOrder = super.getIterationOrder(category);
        int[] iterationOrder = new int[slots.length];
        int i = 0;
        for (int slot : inventory.getIterationOrder(category)) {
            if (slot < inverseSlots.length && inverseSlots[slot] != -1) {
                iterationOrder[obscuredIterationOrder[i++]] = inverseSlots[slot];
            }
        }
        return iterationOrder;
    }
    
    @Override
    public void setGuiPriority(OperationCategory category, int priority) {
        guiPriorityOverrides.add(category);
        super.setGuiPriority(category, priority);
    }
    
    @Override
    public int getGuiPriority(OperationCategory category) {
        if (guiPriorityOverrides.contains(category))
            return super.getGuiPriority(category);
        return inventory.getGuiPriority(category);
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
    public void addObserver(Observer who, int what, int how) {
        inventory.addObserver(who, what, how);
    }
    
    @Override
    public void removeObserver(Observer who, int what, int how) {
        inventory.removeObserver(who, what, how);
    }
    
    @Override
    protected boolean callClickEvent(int slot, Click click, InventoryAction action, boolean cancelled) {
        cancelled = inventory.callClickEvent(slots[slot], click, action, cancelled);
        return super.callClickEvent(slot, click, action, cancelled);
    }
    
    @Override
    public ItemPreUpdateEvent callPreUpdateEvent(@Nullable UpdateReason updateReason, int slot, @Nullable ItemStack previousItemStack, @Nullable ItemStack newItemStack) {
        if (updateReason == UpdateReason.SUPPRESSED)
            throw new IllegalArgumentException("Cannot call ItemPreUpdateEvent with UpdateReason.SUPPRESSED");
        
        var delegatedEvent = inventory.callPreUpdateEvent(updateReason, slots[slot], previousItemStack, newItemStack);
        var event = new ItemPreUpdateEvent(this, slot, updateReason, delegatedEvent.getPreviousItem(), delegatedEvent.getNewItem());
        event.setCancelled(delegatedEvent.isCancelled());
        
        for (var handler : getPreUpdateHandlers()) {
            try {
                handler.accept(event);
            } catch (Throwable t) {
                InvUI.getInstance().handleException("An exception occurred while handling an inventory event", t);
            }
        }
        
        return event;
    }
    
    @Override
    public void callPostUpdateEvent(@Nullable UpdateReason updateReason, int slot, @Nullable ItemStack previousItemStack, @Nullable ItemStack newItemStack) {
        if (updateReason == UpdateReason.SUPPRESSED)
            throw new IllegalArgumentException("Cannot call ItemPostUpdateEvent with UpdateReason.SUPPRESSED");
        
        inventory.callPostUpdateEvent(updateReason, slots[slot], previousItemStack, newItemStack);
        super.callPostUpdateEvent(updateReason, slot, previousItemStack, newItemStack);
    }
    
    @Override
    public boolean hasEventHandlers() {
        return super.hasEventHandlers() || inventory.hasEventHandlers();
    }
    
    @Override
    public InventorySlot getBackingSlot(int slot) {
        return inventory.getBackingSlot(slots[slot]);
    }
    
    @Override
    public int getUpdatePeriod(int what) {
        return inventory.getUpdatePeriod(slots[what]);
    }
    
}
