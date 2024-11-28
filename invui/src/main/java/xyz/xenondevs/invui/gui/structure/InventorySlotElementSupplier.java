package xyz.xenondevs.invui.gui.structure;

import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.gui.SlotElement;
import xyz.xenondevs.invui.gui.SlotElement.InventorySlotElement;
import xyz.xenondevs.invui.inventory.Inventory;
import xyz.xenondevs.invui.item.ItemProvider;

import java.util.function.Supplier;

public class InventorySlotElementSupplier implements Supplier<InventorySlotElement> {
    
    private final Inventory inventory;
    private final ItemProvider background;
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
    public SlotElement.InventorySlotElement get() {
        if (++slot == inventory.getSize()) slot = 0;
        return new InventorySlotElement(inventory, slot, background);
    }
    
}
