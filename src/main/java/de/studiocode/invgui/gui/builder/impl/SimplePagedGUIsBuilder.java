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
        if (getBackFunction() == null || getForwardFunction() == null)
            throw new IllegalStateException("BackBuilder or ForwardBuilder haven't been set yet");
        if (guis == null)
            throw new IllegalStateException("GUIs haven't been set yet");
        
        SimplePagedGUIs gui = new SimplePagedGUIs(getWidth(), getHeight(), getBackItemIndex(), getBackFunction(),
            getForwardItemIndex(), getForwardFunction(), guis, getListSlots());
        setSlotElements(gui);
        
        return gui;
    }
    
    public void setGuis(List<GUI> guis) {
        this.guis = guis;
    }
    
}
