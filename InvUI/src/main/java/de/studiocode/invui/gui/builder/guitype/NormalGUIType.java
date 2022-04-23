package de.studiocode.invui.gui.builder.guitype;

import de.studiocode.invui.gui.builder.GUIContext;
import de.studiocode.invui.gui.impl.SimpleGUI;

class NormalGUIType implements GUIType<SimpleGUI> {
    
    @Override
    public SimpleGUI createGUI(GUIContext context) {
        SimpleGUI gui = new SimpleGUI(context.getStructure());
        gui.setBackground(context.getBackground());
        return gui;
    }
    
    @Override
    public boolean acceptsGUIs() {
        return false;
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
