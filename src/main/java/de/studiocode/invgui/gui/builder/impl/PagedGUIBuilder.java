package de.studiocode.invgui.gui.builder.impl;

import de.studiocode.invgui.gui.impl.PagedGUI;
import de.studiocode.invgui.item.itembuilder.ItemBuilder;

import java.util.List;
import java.util.function.Function;

public abstract class PagedGUIBuilder extends BaseGUIBuilder {
    
    private Function<PagedGUI, ItemBuilder> backFunction;
    private Function<PagedGUI, ItemBuilder> forwardFunction;
    private int[] listSlots;
    
    public PagedGUIBuilder(int width, int height) {
        super(width, height);
    }
    
    protected int getBackItemIndex() {
        List<Integer> indices = findIndicesOf(0);
        if (indices.isEmpty())
            throw new IllegalStateException("BackItem index is not set");
        
        return indices.get(0);
    }
    
    protected int getForwardItemIndex() {
        List<Integer> indices = findIndicesOf(1);
        if (indices.isEmpty())
            throw new IllegalStateException("ForwardItem index is not set");
        
        return indices.get(0);
    }
    
    protected int[] getListSlots() {
        if (listSlots == null)
            return findIndicesOf(2).stream().mapToInt(Integer::intValue).toArray();
        else return listSlots;
    }
    
    public void setListSlots(int[] listSlots) {
        this.listSlots = listSlots;
    }
    
    public void setListSlotIngredient(char key) {
        setIngredient(key, 2);
    }
    
    public void setBackItem(char key, Function<PagedGUI, ItemBuilder> backFunction) {
        setIngredient(key, 0);
        this.backFunction = backFunction;
    }
    
    protected Function<PagedGUI, ItemBuilder> getBackFunction() {
        return backFunction;
    }
    
    public void setForwardItem(char key, Function<PagedGUI, ItemBuilder> forwardFunction) {
        setIngredient(key, 1);
        this.forwardFunction = forwardFunction;
    }
    
    protected Function<PagedGUI, ItemBuilder> getForwardFunction() {
        return forwardFunction;
    }
    
}
