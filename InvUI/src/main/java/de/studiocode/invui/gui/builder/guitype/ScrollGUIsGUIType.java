package de.studiocode.invui.gui.builder.guitype;

import de.studiocode.invui.gui.builder.GUIContext;
import de.studiocode.invui.gui.impl.SimpleScrollNestedGUI;

class ScrollGUIsGUIType implements GUIType<SimpleScrollNestedGUI> {
    
    @Override
    public SimpleScrollNestedGUI createGUI(GUIContext context) {
        return new SimpleScrollNestedGUI(context.getWidth(), context.getHeight(), context.getGuis(), context.getStructure());
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
        return false;
    }
    
}
