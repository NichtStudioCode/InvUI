package de.studiocode.invui.gui.impl;

import de.studiocode.invui.gui.SlotElement;
import de.studiocode.invui.gui.structure.Structure;
import de.studiocode.invui.virtualinventory.VirtualInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SimpleScrollVIGUI extends ScrollGUI {
    
    private VirtualInventory inventory;
    
    public SimpleScrollVIGUI(int width, int height, @Nullable VirtualInventory inventory, int... itemListSlots) {
        super(width, height, false, itemListSlots);
        this.inventory = inventory;
        
        update();
    }
    
    public SimpleScrollVIGUI(@Nullable VirtualInventory inventory, @NotNull Structure structure) {
        super(structure.getWidth(), structure.getHeight(), false, structure);
        this.inventory = inventory;
        
        update();
    }
    
    public void setInventory(VirtualInventory inventory) {
        this.inventory = inventory;
        update();
    }
    
    @Override
    protected List<SlotElement.VISlotElement> getElements(int from, int to) {
        ArrayList<SlotElement.VISlotElement> elements = new ArrayList<>();
        if (inventory != null) {
            for (int i = from; i < to && i < inventory.getSize(); i++) {
                elements.add(new SlotElement.VISlotElement(inventory, i));
            }
        }
        
        return elements;
    }
    
    @Override
    protected int getMaxLineIndex() {
        if (inventory == null) return 0;
        return (int) Math.ceil((double) inventory.getSize() / (double) getLineLength()) - 1;
    }
    
}
