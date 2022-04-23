package de.studiocode.invui.gui.builder.guitype;

import de.studiocode.invui.gui.builder.GUIContext;
import de.studiocode.invui.gui.impl.SimpleTabGUI;

class TabGUIType implements GUIType<SimpleTabGUI> {
    
    @Override
    public SimpleTabGUI createGUI(GUIContext context) {
        SimpleTabGUI gui = new SimpleTabGUI(context.getGuis(), context.getStructure());
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
