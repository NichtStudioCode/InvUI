package de.studiocode.invui.gui.builder.guitype;

import de.studiocode.invui.gui.builder.GUIContext;
import de.studiocode.invui.gui.impl.SimpleScrollItemsGUI;

class ScrollItemsGUIType implements GUIType<SimpleScrollItemsGUI> {
    
    @Override
    public SimpleScrollItemsGUI createGUI(GUIContext context) {
        return new SimpleScrollItemsGUI(context.getWidth(), context.getHeight(), context.getItems(), context.getStructure());
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