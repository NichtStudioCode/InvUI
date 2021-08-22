package de.studiocode.invui.gui.builder.guitype;

import de.studiocode.invui.gui.builder.GUIContext;
import de.studiocode.invui.gui.impl.SimpleScrollGUI;

class ScrollGUIType implements GUIType<SimpleScrollGUI> {
    
    @Override
    public SimpleScrollGUI createGUI(GUIContext context) {
        return new SimpleScrollGUI(context.getWidth(), context.getHeight(), context.getItems(), context.getStructure());
    }
    
    @Override
    public boolean acceptsGUIs() {
        return false;
    }
    
    @Override
    public boolean acceptsItems() {
        return true;
    }
    
}