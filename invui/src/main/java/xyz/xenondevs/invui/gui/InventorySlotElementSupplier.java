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
        this.inventory = inventory;
        this.background = null;
    }
    
    public InventorySlotElementSupplier(Inventory inventory, @Nullable ItemProvider background) {
        this.inventory = inventory;
        this.background = background;
    }
    
    @Override
    public SlotElement.InventoryLink get() {
        if (slot >= inventory.getSize())
            throw new IllegalStateException("No more slots available");
        
        return new SlotElement.InventoryLink(inventory, slot++, background);
    }
    
}
