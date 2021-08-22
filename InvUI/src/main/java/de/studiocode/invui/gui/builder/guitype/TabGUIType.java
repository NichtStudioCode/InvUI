package de.studiocode.invui.gui.builder.guitype;

import de.studiocode.invui.gui.builder.GUIContext;
import de.studiocode.invui.gui.impl.SimpleTabGUI;

class TabGUIType implements GUIType<SimpleTabGUI> {
    
    @Override
    public SimpleTabGUI createGUI(GUIContext context) {
        return new SimpleTabGUI(context.getWidth(), context.getHeight(), context.getGuis(), context.getStructure());
    }
    
    @Override
    public boolean acceptsGUIs() {
        return true;
    }
    
    @Override
    public boolean acceptsItems() {
        return false;
    }
    
}
