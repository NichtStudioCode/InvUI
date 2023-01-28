package de.studiocode.invui.gui.builder.guitype;

import de.studiocode.invui.gui.GUI;
import de.studiocode.invui.gui.ScrollGUI;
import de.studiocode.invui.gui.builder.GUIContext;
import de.studiocode.invui.gui.impl.ScrollNestedGUIImpl;

class ScrollGUIsGUIType implements GUIType<ScrollGUI<GUI>, GUI> {
    
    @Override
    public ScrollNestedGUIImpl createGUI(GUIContext<GUI> context) {
        ScrollNestedGUIImpl gui = new ScrollNestedGUIImpl(context.getContent(), context.getStructure());
        gui.setBackground(gui.getBackground());
        return gui;
    }
    
}
