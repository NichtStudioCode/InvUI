package de.studiocode.invui.gui.builder.guitype;

import de.studiocode.invui.gui.GUI;
import de.studiocode.invui.gui.TabGUI;
import de.studiocode.invui.gui.builder.GUIContext;
import de.studiocode.invui.gui.impl.TabGUIImpl;

class TabGUIType implements GUIType<TabGUI, GUI> {
    
    @Override
    public TabGUIImpl createGUI(GUIContext<GUI> context) {
        TabGUIImpl gui = new TabGUIImpl(context.getContent(), context.getStructure());
        gui.setBackground(context.getBackground());
        return gui;
    }
    
}
