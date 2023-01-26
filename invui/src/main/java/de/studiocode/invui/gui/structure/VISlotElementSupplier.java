package de.studiocode.invui.gui.structure;

import de.studiocode.invui.gui.SlotElement.VISlotElement;
import de.studiocode.invui.item.ItemProvider;
import de.studiocode.invui.virtualinventory.VirtualInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class VISlotElementSupplier implements Supplier<VISlotElement> {
    
    private final VirtualInventory inventory;
    private final ItemProvider background;
    private int slot = -1;
    
    public VISlotElementSupplier(@NotNull VirtualInventory inventory) {
        this.inventory = inventory;
        this.background = null;
    }
    
    public VISlotElementSupplier(@NotNull VirtualInventory inventory, @Nullable ItemProvider background) {
        this.inventory = inventory;
        this.background = background;
    }
    
    @NotNull
    @Override
    public VISlotElement get() {
        if (++slot == inventory.getSize()) slot = 0;
        return new VISlotElement(inventory, slot, background);
    }
    
}
