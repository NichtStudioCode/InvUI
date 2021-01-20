package de.studiocode.invgui.gui.builder.impl;

import de.studiocode.invgui.item.itembuilder.ItemBuilder;

import java.util.List;

public abstract class PagedGUIBuilder extends BaseGUIBuilder {
    
    private ItemBuilder backBuilder;
    private ItemBuilder forwardBuilder;
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
    
    public void setBackItemIngredient(char c) {
        setIngredient(c, 0);
    }
    
    public void setForwardItemIngredient(char c) {
        setIngredient(c, 1);
    }
    
    public void setListSlotIngredient(char c) {
        setIngredient(c, 2);
    }
    
    protected ItemBuilder getBackBuilder() {
        return backBuilder;
    }
    
    public void setBackBuilder(ItemBuilder backBuilder) {
        this.backBuilder = backBuilder;
    }
    
    protected ItemBuilder getForwardBuilder() {
        return forwardBuilder;
    }
    
    public void setForwardBuilder(ItemBuilder forwardBuilder) {
        this.forwardBuilder = forwardBuilder;
    }
    
}
