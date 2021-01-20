package de.studiocode.invgui.gui.builder.impl;

import de.studiocode.invgui.gui.GUI;
import de.studiocode.invgui.gui.impl.SimplePagedGUIs;

import java.util.List;

public class SimplePagedGUIsBuilder extends PagedGUIBuilder {
    
    private List<GUI> guis;
    
    public SimplePagedGUIsBuilder(int width, int height) {
        super(width, height);
    }
    
    @Override
    public SimplePagedGUIs build() {
        if (getBackBuilder() == null || getForwardBuilder() == null)
            throw new IllegalStateException("BackBuilder or ForwardBuilder haven't been set yet");
        if (guis == null)
            throw new IllegalStateException("GUIs haven't been set yet");
        
        SimplePagedGUIs gui = new SimplePagedGUIs(getWidth(), getHeight(), getBackItemIndex(), getBackBuilder(),
            getForwardItemIndex(), getForwardBuilder(), guis, getListSlots());
        setItems(gui);
        
        return gui;
    }
    
    public void setGuis(List<GUI> guis) {
        this.guis = guis;
    }
    
}
