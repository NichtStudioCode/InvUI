package de.studiocode.invgui.gui.impl;

import de.studiocode.invgui.gui.SlotElement;
import de.studiocode.invgui.item.Item;
import de.studiocode.invgui.item.impl.tabgui.TabItem;

import java.util.Arrays;
import java.util.List;

public abstract class TabGUI extends BaseGUI {
    
    private final int tabAmount;
    private final int[] listSlots;
    private final Item[] tabItems;
    
    private int currentTab;
    
    public TabGUI(int width, int height, int tabAmount, int... listSlots) {
        super(width, height);
        this.tabAmount = tabAmount;
        this.listSlots = listSlots;
        this.tabItems = new Item[tabAmount];
    }
    
    public void showTab(int tab) {
        if (tab < 0 || tab >= tabAmount)
            throw new IllegalArgumentException("Tab out of bounds");
        
        currentTab = tab;
        update();
    }
    
    public void setTabItem(int tab, int index, TabItem tabItem) {
        tabItems[tab] = tabItem;
        setItem(index, tabItem);
    }
    
    protected void update() {
        Arrays.stream(tabItems).forEach(Item::notifyWindows);
        
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
