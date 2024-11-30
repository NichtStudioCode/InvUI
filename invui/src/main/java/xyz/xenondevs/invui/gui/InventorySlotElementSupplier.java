package xyz.xenondevs.invui.gui;

import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.gui.SlotElement.InventoryLink;
import xyz.xenondevs.invui.inventory.Inventory;
import xyz.xenondevs.invui.item.ItemProvider;

import java.util.function.Supplier;

class InventorySlotElementSupplier implements Supplier<InventoryLink> {
    
    private final Inventory inventory;
    private final @Nullable ItemProvider background;
    private int slot = -1;
    
    public InventorySlotElementSupplier(Inventory inventory) {
        this.inventory = inventory;
        this.background = null;
    }
    
    public InventorySlotElementSupplier(Inventory inventory, @Nullable ItemProvider background) {
        this.inventory = inventory;
        this.background = background;
    }
    
    @Override
    public InventoryLink get() {
        if (++slot == inventory.getSize())
            slot = 0;
        return new InventoryLink(inventory, slot, background);
    }
    
}
