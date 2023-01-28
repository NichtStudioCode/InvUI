package de.studiocode.invui.gui.builder.guitype;

import de.studiocode.invui.gui.Gui;
import de.studiocode.invui.gui.builder.GuiContext;
import de.studiocode.invui.gui.impl.NormalGuiImpl;

class NormalGuiType implements GuiType<Gui, Void> {
    
    @Override
    public NormalGuiImpl createGui(GuiContext<Void> context) {
        NormalGuiImpl gui = new NormalGuiImpl(context.getStructure());
        gui.setBackground(context.getBackground());
        return gui;
    }
    
}
