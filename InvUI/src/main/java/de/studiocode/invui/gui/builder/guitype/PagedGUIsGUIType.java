package de.studiocode.invui.gui.builder.guitype;

import de.studiocode.invui.gui.builder.GUIContext;
import de.studiocode.invui.gui.impl.SimplePagedNestedGUI;

class PagedGUIsGUIType implements GUIType<SimplePagedNestedGUI> {
    
    @Override
    public SimplePagedNestedGUI createGUI(GUIContext context) {
        SimplePagedNestedGUI gui = new SimplePagedNestedGUI(context.getGuis(), context.getStructure());
        gui.setBackground(context.getBackground());
        return gui;
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
