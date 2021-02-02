package de.studiocode.invui.gui.builder;

import de.studiocode.invui.gui.GUI;
import de.studiocode.invui.gui.SlotElement;
import de.studiocode.invui.gui.impl.SimpleTabGUI;
import de.studiocode.invui.gui.impl.TabGUI;
import de.studiocode.invui.item.Item;
import de.studiocode.invui.item.itembuilder.ItemBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.function.Function;

public class TabGUIBuilder extends GUIBuilder {
    
    private final LinkedHashMap<GUI, Function<TabGUI, ItemBuilder>> tabs = new LinkedHashMap<>();
    
    private int[] listSlots;
    private int[] tabItemSlots;
    
    public TabGUIBuilder(int width, int height) {
        super(width, height);
    }
    
    @Override
    public TabGUI build() {
        int[] listSlots = this.listSlots != null ? this.listSlots : findIndicesOf(0);
        int[] tabItemSlots = this.tabItemSlots != null ? this.tabItemSlots : findIndicesOf(1);
        
        if (listSlots.length == 0) throw new IllegalStateException("No list slots have been set.");
        if (tabItemSlots.length != tabs.size()) throw new IllegalStateException("TabItemSlots length has to be the same as tabs size");
        
        SimpleTabGUI tabGUI = new SimpleTabGUI(width, height, tabs, listSlots, tabItemSlots);
        setSlotElements(tabGUI);
        
        return tabGUI;
    }
    
    public TabGUIBuilder addTab(GUI gui, Function<TabGUI, ItemBuilder> builderFunction) {
        tabs.put(gui, builderFunction);
        return this;
    }
    
    public TabGUIBuilder setListSlotIngredient(char key) {
        setIngredient(key, 0);
        return this;
    }
    
    public TabGUIBuilder setTabSlotIngredient(char key) {
        setIngredient(key, 1);
        return this;
    }
    
    public TabGUIBuilder setListSlots(int[] listSlots) {
        this.listSlots = listSlots;
        return this;
    }
    
    public TabGUIBuilder setTabItemSlots(int[] tabItemSlots) {
        this.tabItemSlots = tabItemSlots;
        return this;
    }
    
    @Override
    public TabGUIBuilder setStructure(@NotNull String structure) {
        return (TabGUIBuilder) super.setStructure(structure);
    }
    
    @Override
    public TabGUIBuilder setIngredient(char key, @NotNull Item item) {
        return (TabGUIBuilder) super.setIngredient(key, item);
    }
    
    @Override
    public TabGUIBuilder setIngredient(char key, @NotNull SlotElement slotElement) {
        return (TabGUIBuilder) super.setIngredient(key, slotElement);
    }
    
}
