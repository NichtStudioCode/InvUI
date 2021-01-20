package de.studiocode.invgui.gui.builder.impl;

import de.studiocode.invgui.gui.impl.SimpleGUI;

public class SimpleGUIBuilder extends BaseGUIBuilder {
    
    public SimpleGUIBuilder(int width, int height) {
        super(width, height);
    }
    
    @Override
    public SimpleGUI build() {
        SimpleGUI gui = new SimpleGUI(getWidth(), getHeight());
        setItems(gui);
        return gui;
    }
    
}
