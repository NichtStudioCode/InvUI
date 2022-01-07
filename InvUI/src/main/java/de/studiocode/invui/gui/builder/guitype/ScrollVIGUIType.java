package de.studiocode.invui.gui.builder.guitype;

import de.studiocode.invui.gui.builder.GUIContext;
import de.studiocode.invui.gui.impl.SimpleScrollVIGUI;

class ScrollVIGUIType implements GUIType<SimpleScrollVIGUI> {
    
    @Override
    public SimpleScrollVIGUI createGUI(GUIContext context) {
        return new SimpleScrollVIGUI(context.getWidth(), context.getHeight(), context.getInventory(), context.getStructure());
    }
    
    @Override
    public boolean acceptsGUIs() {
        return true;
    }
    
    @Override
    public boolean acceptsItems() {
        return false;
    }
    
    @Override
    public boolean acceptsInventory() {
        return true;
    }
    
}
