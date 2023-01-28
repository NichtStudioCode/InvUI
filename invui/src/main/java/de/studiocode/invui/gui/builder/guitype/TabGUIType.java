package de.studiocode.invui.gui.builder.guitype;

import de.studiocode.invui.gui.Gui;
import de.studiocode.invui.gui.TabGui;
import de.studiocode.invui.gui.builder.GuiContext;
import de.studiocode.invui.gui.impl.TabGuiImpl;

class TabGuiType implements GuiType<TabGui, Gui> {
    
    @Override
    public TabGuiImpl createGui(GuiContext<Gui> context) {
        TabGuiImpl gui = new TabGuiImpl(context.getContent(), context.getStructure());
        gui.setBackground(context.getBackground());
        return gui;
    }
    
}
