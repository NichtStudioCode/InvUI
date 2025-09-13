package xyz.xenondevs.invui.gui;

import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.inventory.Inventory;
import xyz.xenondevs.invui.item.ItemProvider;

import java.util.ArrayList;
import java.util.List;

class InventorySlotElementSupplier implements SlotElementSupplier {
    
    private final Inventory inventory;
    private final @Nullable ItemProvider background;
    private final int offset;
    
    public InventorySlotElementSupplier(Inventory inventory, @Nullable ItemProvider background, int offset) {
        if (inventory.getSize() <= 0)
            throw new IllegalArgumentException("Illegal inventory size: " + inventory.getSize());
        if (offset >= inventory.getSize())
            throw new IllegalArgumentException("Offset must be less than inventory size, but was " + offset + " for inventory of size " + inventory.getSize());
        
        this.inventory = inventory;
        this.background = background;
        this.offset = offset;
    }
    
    @Override
    public List<? extends SlotElement> generateSlotElements(List<? extends Slot> slots) {
        if (slots.size() > inventory.getSize())
            throw new IndexOutOfBoundsException("Structure requests " + slots.size() + " slots, but inventory only has " + inventory.getSize() + " slots");
        
        var elements = new ArrayList<SlotElement.InventoryLink>();
        for (int i = 0; i < slots.size(); i++) {
            elements.add(new SlotElement.InventoryLink(inventory, offset + i, background));
        }
        
        return elements;
    }
    
}
