package de.studiocode.invui.gui.structure;

import de.studiocode.invui.gui.SlotElement.VISlotElement;
import de.studiocode.invui.item.ItemProvider;
import de.studiocode.invui.virtualinventory.VirtualInventory;

import java.util.function.Supplier;

public class VISlotElementSupplier implements Supplier<VISlotElement> {
    
    private final VirtualInventory inventory;
    private final ItemProvider background;
    private int slot = -1;
    
    public VISlotElementSupplier(VirtualInventory inventory) {
        this.inventory = inventory;
        this.background = null;
    }
    
    public VISlotElementSupplier(VirtualInventory inventory, ItemProvider background) {
        this.inventory = inventory;
        this.background = background;
    }
    
    @Override
    public VISlotElement get() {
        if (++slot == inventory.getSize()) slot = 0;
        return new VISlotElement(inventory, slot, background);
    }
    
}
