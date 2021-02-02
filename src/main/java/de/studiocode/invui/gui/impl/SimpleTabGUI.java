package de.studiocode.invui.gui.impl;

import de.studiocode.invui.gui.GUI;
import de.studiocode.invui.gui.SlotElement;
import de.studiocode.invui.item.impl.tabgui.TabItem;
import de.studiocode.invui.item.itembuilder.ItemBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Function;

public class SimpleTabGUI extends TabGUI {
    
    private final List<GUI> tabs;
    
    public SimpleTabGUI(int width, int height, LinkedHashMap<GUI, Function<TabGUI, ItemBuilder>> tabs,
                        int[] listSlots, int[] tabItemSlots) {
        
        super(width, height, tabs.size(), listSlots);
        this.tabs = new ArrayList<>(tabs.keySet());
    
        List<Function<TabGUI, ItemBuilder>> builderFunctions = new ArrayList<>(tabs.values());
        for (int i = 0; i < tabs.size(); i++) {
            TabItem tabItem = new TabItem(this, i, builderFunctions.get(i));
            setTabItem(i, tabItemSlots[i], tabItem);
        }
        
        update();
    }
    
    @Override
    public List<SlotElement> getSlotElements(int tab) {
        return Arrays.asList(tabs.get(tab).getSlotElements());
    }
    
}
