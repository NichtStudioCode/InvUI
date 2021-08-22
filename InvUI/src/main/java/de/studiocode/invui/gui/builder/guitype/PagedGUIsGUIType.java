package de.studiocode.invui.gui.builder.guitype;

import de.studiocode.invui.gui.builder.GUIContext;
import de.studiocode.invui.gui.impl.SimplePagedNestedGUI;

class PagedGUIsGUIType implements GUIType<SimplePagedNestedGUI> {
    
    @Override
    public SimplePagedNestedGUI createGUI(GUIContext context) {
        return new SimplePagedNestedGUI(context.getWidth(), context.getHeight(), context.getGuis(), context.getStructure());
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
