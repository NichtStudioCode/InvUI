package de.studiocode.invui.gui.builder.guitype;

import de.studiocode.invui.gui.builder.GUIContext;
import de.studiocode.invui.gui.impl.SimpleGUI;

class NormalGUIType implements GUIType<SimpleGUI> {
    
    @Override
    public SimpleGUI createGUI(GUIContext context) {
        return new SimpleGUI(context.getWidth(), context.getHeight(), context.getStructure());
    }
    
    @Override
    public boolean acceptsGUIs() {
        return false;
    }
    
    @Override
    public boolean acceptsItems() {
        return false;
    }
    
}
