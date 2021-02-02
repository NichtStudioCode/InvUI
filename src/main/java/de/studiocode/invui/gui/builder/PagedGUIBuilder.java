package de.studiocode.invui.gui.builder;

import de.studiocode.invui.gui.GUI;
import de.studiocode.invui.gui.SlotElement;
import de.studiocode.invui.gui.impl.PagedGUI;
import de.studiocode.invui.gui.impl.SimplePagedGUIs;
import de.studiocode.invui.gui.impl.SimplePagedItemsGUI;
import de.studiocode.invui.item.Item;
import de.studiocode.invui.item.itembuilder.ItemBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Function;

public class PagedGUIBuilder extends GUIBuilder {
    
    private final ContentType contentType;
    
    private Function<PagedGUI, ItemBuilder> backFunction;
    private Function<PagedGUI, ItemBuilder> forwardFunction;
    private int[] listSlots;
    private List<?> content;
    
    public PagedGUIBuilder(ContentType contentType, int width, int height) {
        super(width, height);
        this.contentType = contentType;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public PagedGUI build() {
        int[] listSlots = this.listSlots != null ? this.listSlots : findIndicesOf(2);
        int[] backSlots = findIndicesOf(0);
        int[] forwardSlots = findIndicesOf(1);
        
        if (listSlots.length == 0) throw new IllegalStateException("No list slots have been set.");
        if (backSlots.length == 0) throw new IllegalStateException("Not back slot has been set.");
        if (forwardSlots.length == 0) throw new IllegalStateException("No forward slot has been set.");
        if (content == null) throw new IllegalStateException("Content has not been set.");
        if (forwardFunction == null) throw new IllegalStateException("Forward function has not been set.");
        if (backFunction == null) throw new IllegalStateException("Back function has not been set");
        
        int backSlot = backSlots[0];
        int forwardSlot = forwardSlots[0];
        
        PagedGUI gui;
        if (contentType == ContentType.GUI) {
            gui = new SimplePagedGUIs(width, height, backSlot, backFunction,
                forwardSlot, forwardFunction, (List<GUI>) content, listSlots);
        } else {
            gui = new SimplePagedItemsGUI(width, height, backSlot, backFunction,
                forwardSlot, forwardFunction, (List<Item>) content, listSlots);
        }
        
        setSlotElements(gui);
        return gui;
    }
    
    public PagedGUIBuilder setGUIs(List<GUI> guis) {
        if (contentType != ContentType.GUI)
            throw new IllegalStateException("Can't set guis if ContentType is not GUI");
        content = guis;
        
        return this;
    }
    
    public PagedGUIBuilder setItems(List<Item> items) {
        if (contentType != ContentType.SINGLE_ITEMS)
            throw new IllegalStateException("Can't set items if ContentType is not SINGLE_ITEMS");
        content = items;
        
        return this;
    }
    
    public PagedGUIBuilder setListSlots(int[] listSlots) {
        this.listSlots = listSlots;
        return this;
    }
    
    public PagedGUIBuilder setListSlotIngredient(char key) {
        setIngredient(key, 2);
        return this;
    }
    
    public PagedGUIBuilder setBackItem(char key, Function<PagedGUI, ItemBuilder> backFunction) {
        setIngredient(key, 0);
        this.backFunction = backFunction;
        return this;
    }
    
    public PagedGUIBuilder setForwardItem(char key, Function<PagedGUI, ItemBuilder> forwardFunction) {
        setIngredient(key, 1);
        this.forwardFunction = forwardFunction;
        return this;
    }
    
    @Override
    public PagedGUIBuilder setStructure(@NotNull String structure) {
        return (PagedGUIBuilder) super.setStructure(structure);
    }
    
    @Override
    public PagedGUIBuilder setIngredient(char key, @NotNull Item item) {
        return (PagedGUIBuilder) super.setIngredient(key, item);
    }
    
    @Override
    public PagedGUIBuilder setIngredient(char key, @NotNull SlotElement slotElement) {
        return (PagedGUIBuilder) super.setIngredient(key, slotElement);
    }
    
    public enum ContentType {
        
        SINGLE_ITEMS,
        GUI
        
    }
    
}
