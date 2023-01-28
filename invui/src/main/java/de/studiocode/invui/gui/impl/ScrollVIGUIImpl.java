package de.studiocode.invui.gui.impl;

import de.studiocode.invui.gui.AbstractScrollGUI;
import de.studiocode.invui.gui.SlotElement;
import de.studiocode.invui.gui.structure.Structure;
import de.studiocode.invui.virtualinventory.VirtualInventory;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class ScrollVIGUIImpl extends AbstractScrollGUI<VirtualInventory> {
    
    private List<VirtualInventory> inventories;
    private List<SlotElement.VISlotElement> elements;
    
    public ScrollVIGUIImpl(int width, int height, @NotNull List<VirtualInventory> inventories, int... contentListSlots) {
        super(width, height, false, contentListSlots);
        setContent(inventories);
    }
    
    public ScrollVIGUIImpl(@NotNull List<VirtualInventory> inventories, @NotNull Structure structure) {
        super(structure.getWidth(), structure.getHeight(), false, structure);
        setContent(inventories);
    }
    
    @Override
    public void setContent(List<VirtualInventory> inventory) {
        this.inventories = inventory;
        updateElements();
        update();
    }
    
    private void updateElements() {
        elements = new ArrayList<>();
        for (VirtualInventory inventory : inventories) {
            for (int i = 0; i < inventory.getSize(); i++) {
                elements.add(new SlotElement.VISlotElement(inventory, i));
            }
        }
    }
    
    @Override
    protected List<SlotElement.VISlotElement> getElements(int from, int to) {
        return elements.subList(from, Math.min(elements.size(), to));
    }
    
    @Override
    public int getMaxLine() {
        if (elements == null) return 0;
        return (int) Math.ceil((double) elements.size() / (double) getLineLength()) - 1;
    }
    
}
