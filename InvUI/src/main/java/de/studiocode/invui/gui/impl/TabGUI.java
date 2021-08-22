package de.studiocode.invui.gui.impl;

import de.studiocode.invui.gui.Controllable;
import de.studiocode.invui.gui.SlotElement;
import de.studiocode.invui.gui.structure.Markers;
import de.studiocode.invui.gui.structure.Structure;

import java.util.List;

public abstract class TabGUI extends BaseGUI implements Controllable {
    
    private final int tabAmount;
    private final int[] listSlots;
    
    private int currentTab;
    
    public TabGUI(int width, int height, int tabAmount, int... listSlots) {
        super(width, height);
        this.tabAmount = tabAmount;
        this.listSlots = listSlots;
    }
    
    public TabGUI(int width, int height, int tabAmount, Structure structure) {
        this(width, height, tabAmount, structure.getIngredientList().findIndicesOfMarker(Markers.ITEM_LIST_SLOT));
        applyStructure(structure);
    }
    
    public void showTab(int tab) {
        if (tab < 0 || tab >= tabAmount)
            throw new IllegalArgumentException("Tab out of bounds");
        
        currentTab = tab;
        update();
    }
    
    protected void update() {
        updateControlItems();
        updateContent();
    }
    
    private void updateContent() {
        List<SlotElement> slotElements = getSlotElements(currentTab);
        for (int i = 0; i < listSlots.length; i++) {
            int slot = listSlots[i];
            if (slotElements.size() > i) setSlotElement(listSlots[i], slotElements.get(i));
            else remove(slot);
        }
    }
    
    public int getCurrentTab() {
        return currentTab;
    }
    
    public abstract List<SlotElement> getSlotElements(int tab);
    
}
