package de.studiocode.invui.gui.builder.guitype;

import de.studiocode.invui.gui.builder.GUIContext;
import de.studiocode.invui.gui.impl.SimplePagedItemsGUI;

class PagedItemsGUIType implements GUIType<SimplePagedItemsGUI> {
    
    @Override
    public SimplePagedItemsGUI createGUI(GUIContext context) {
        SimplePagedItemsGUI gui = new SimplePagedItemsGUI(context.getItems(), context.getStructure());
        gui.setBackground(context.getBackground());
        return gui;
    }
    
    @Override
    public boolean acceptsGUIs() {
        return false;
    }
    
    @Override
    public boolean acceptsItems() {
        return true;
    }
    
    @Override
    public boolean acceptsInventory() {
        return false;
    }
    
}
