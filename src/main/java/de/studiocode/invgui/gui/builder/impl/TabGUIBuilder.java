package de.studiocode.invgui.gui.builder.impl;

import de.studiocode.invgui.gui.GUI;
import de.studiocode.invgui.gui.impl.TabGUI;
import de.studiocode.invgui.item.itembuilder.ItemBuilder;

import java.util.LinkedHashMap;
import java.util.function.Function;

public abstract class TabGUIBuilder extends BaseGUIBuilder {
    
    private final LinkedHashMap<GUI, Function<TabGUI, ItemBuilder>> tabs = new LinkedHashMap<>();
    
    private int[] listSlots;
    private int[] tabItemSlots;
    
    public TabGUIBuilder(int width, int height) {
        super(width, height);
    }
    
    public void addTab(GUI gui, Function<TabGUI, ItemBuilder> builderFunction) {
        tabs.put(gui, builderFunction);
    }
    
    public void setListSlotIngredient(char key) {
        setIngredient(key, 0);
    }
    
    public void setTabSlotIngredient(char key) {
        setIngredient(key, 1);
    }
    
    public void setListSlots(int[] listSlots) {
        this.listSlots = listSlots;
    }
    
    public void setTabItemSlots(int[] tabItemSlots) {
        this.tabItemSlots = tabItemSlots;
    }
    
    protected int[] getListSlots() {
        if (listSlots == null) {
            return findIndicesOf(0).stream().mapToInt(Integer::intValue).toArray();
        } else return listSlots;
    }
    
    protected int[] getTabItemSlots() {
        if (tabItemSlots == null) {
            return findIndicesOf(1).stream().mapToInt(Integer::intValue).toArray();
        } else return tabItemSlots;
    }
    
    protected LinkedHashMap<GUI, Function<TabGUI, ItemBuilder>> getTabs() {
        return tabs;
    }
    
}
