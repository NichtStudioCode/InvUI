package xyz.xenondevs.invui.gui;

import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.inventory.Inventory;
import xyz.xenondevs.invui.item.ItemProvider;

import java.util.function.Supplier;

class InventorySlotElementSupplier implements Supplier<SlotElement.InventoryLink> {
    
    private final Inventory inventory;
    private final @Nullable ItemProvider background;
    private int slot = 0;
    
    public InventorySlotElementSupplier(Inventory inventory) {
        this(inventory, null);
    }
    
    public InventorySlotElementSupplier(Inventory inventory, @Nullable ItemProvider background) {
        if (inventory.getSize() <= 0)
            throw new IllegalArgumentException("Illegal inventory size: " + inventory.getSize());
        
        this.inventory = inventory;
        this.background = background;
    }
    
    @Override
    public SlotElement.InventoryLink get() {
        var element = new SlotElement.InventoryLink(inventory, slot, background);
        slot = (slot + 1) % inventory.getSize();
        return element;
    }
    
}
