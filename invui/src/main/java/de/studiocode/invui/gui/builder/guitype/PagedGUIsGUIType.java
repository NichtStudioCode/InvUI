package de.studiocode.invui.gui.builder.guitype;

import de.studiocode.invui.gui.GUI;
import de.studiocode.invui.gui.PagedGUI;
import de.studiocode.invui.gui.builder.GUIContext;
import de.studiocode.invui.gui.impl.PageNestedGUIImpl;

class PagedGUIsGUIType implements GUIType<PagedGUI<GUI>, GUI> {
    
    @Override
    public PageNestedGUIImpl createGUI(GUIContext<GUI> context) {
        PageNestedGUIImpl gui = new PageNestedGUIImpl(context.getContent(), context.getStructure());
        gui.setBackground(context.getBackground());
        return gui;
    }
    
}
