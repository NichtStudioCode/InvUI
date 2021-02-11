package de.studiocode.invui.gui.impl;

import de.studiocode.invui.gui.Controllable;
import de.studiocode.invui.gui.SlotElement;
import de.studiocode.invui.gui.structure.Marker;
import de.studiocode.invui.gui.structure.Structure;
import de.studiocode.invui.item.Item;
import de.studiocode.invui.item.impl.controlitem.ControlItem;
import de.studiocode.invui.item.impl.controlitem.TabItem;

import java.util.ArrayList;
import java.util.List;

public abstract class TabGUI extends BaseGUI implements Controllable {
    
    private final List<Item> controlItems = new ArrayList<>();
    private final int tabAmount;
    private final int[] listSlots;
    
    private int currentTab;
    
    public TabGUI(int width, int height, int tabAmount, int... listSlots) {
        super(width, height);
        this.tabAmount = tabAmount;
        this.listSlots = listSlots;
    }
    
    public TabGUI(int width, int height, int tabAmount, Structure structure) {
        this(width, height, tabAmount, structure.createIngredientList().findIndicesOfMarker(Marker.ITEM_LIST_SLOT));
        applyStructure(structure);
    }
    
    public void showTab(int tab) {
        if (tab < 0 || tab >= tabAmount)
            throw new IllegalArgumentException("Tab out of bounds");
        
        currentTab = tab;
        update();
    }
    
    @Override
    public void addControlItem(int index, ControlItem<?> controlItem) {
        if (!(controlItem instanceof TabItem))
            throw new IllegalArgumentException("controlItem is not an instance of TabItem");
        
        ((TabItem) controlItem).setGui(this);
        setItem(index, controlItem);
        controlItems.add(controlItem);
    }
    
    protected void update() {
        updateControlItems();
        updateContent();
    }
    
    private void updateControlItems() {
        controlItems.forEach(Item::notifyWindows);
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
