package de.studiocode.invui.gui.builder.guitype;

import de.studiocode.invui.gui.builder.GUIContext;
import de.studiocode.invui.gui.impl.SimplePagedItemsGUI;

class PagedItemsGUIType implements GUIType<SimplePagedItemsGUI> {
    
    @Override
    public SimplePagedItemsGUI createGUI(GUIContext context) {
        return new SimplePagedItemsGUI(context.getWidth(), context.getHeight(), context.getItems(), context.getStructure());
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
