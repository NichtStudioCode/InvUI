package de.studiocode.invgui.gui.builder.impl;

import de.studiocode.invgui.gui.GUI;
import de.studiocode.invgui.gui.impl.SimpleTabGUI;

public class SimpleTabGUIBuilder extends TabGUIBuilder {
    
    public SimpleTabGUIBuilder(int width, int height) {
        super(width, height);
    }
    
    @Override
    public GUI build() {
        if (getTabItemSlots().length != getTabs().size())
            throw new IllegalStateException("TabItemSlots length has to be the same as tabs size");
        
        SimpleTabGUI tabGUI = new SimpleTabGUI(getWidth(), getHeight(), getTabs(), getListSlots(), getTabItemSlots());
        setSlotElements(tabGUI);
        
        return tabGUI;
    }
    
}
