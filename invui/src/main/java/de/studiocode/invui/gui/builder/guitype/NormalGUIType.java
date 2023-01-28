package de.studiocode.invui.gui.builder.guitype;

import de.studiocode.invui.gui.GUI;
import de.studiocode.invui.gui.builder.GUIContext;
import de.studiocode.invui.gui.impl.NormalGUIImpl;

class NormalGUIType implements GUIType<GUI, Void> {
    
    @Override
    public NormalGUIImpl createGUI(GUIContext<Void> context) {
        NormalGUIImpl gui = new NormalGUIImpl(context.getStructure());
        gui.setBackground(context.getBackground());
        return gui;
    }
    
}
