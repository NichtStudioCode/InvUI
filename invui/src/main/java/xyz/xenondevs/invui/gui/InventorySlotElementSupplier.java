package xyz.xenondevs.invui.gui;

import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.inventory.Inventory;
import xyz.xenondevs.invui.item.ItemProvider;

class InventorySlotElementSupplier implements ResettableSlotElementSupplier<SlotElement.InventoryLink> {
    
    private final Inventory inventory;
    private final @Nullable ItemProvider background;
    private final int offset;
    private int slot;
    
    public InventorySlotElementSupplier(Inventory inventory, @Nullable ItemProvider background, int offset) {
        if (inventory.getSize() <= 0)
            throw new IllegalArgumentException("Illegal inventory size: " + inventory.getSize());
        if (offset >= inventory.getSize())
            throw new IllegalArgumentException("Offset must be less than inventory size, but was " + offset + " for inventory of size " + inventory.getSize());
        
        this.inventory = inventory;
        this.background = background;
        this.offset = offset;
        this.slot = offset;
    }
    
    @Override
    public SlotElement.InventoryLink get() {
        if (slot >= inventory.getSize())
            throw new IllegalStateException("No more slots available");
        return new SlotElement.InventoryLink(inventory, slot++, background);
    }
    
    @Override
    public void reset() {
        slot = offset;
    }
    
}
