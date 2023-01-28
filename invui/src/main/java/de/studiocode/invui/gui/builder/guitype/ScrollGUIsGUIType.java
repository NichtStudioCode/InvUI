package de.studiocode.invui.gui.builder.guitype;

import de.studiocode.invui.gui.Gui;
import de.studiocode.invui.gui.ScrollGui;
import de.studiocode.invui.gui.builder.GuiContext;
import de.studiocode.invui.gui.impl.ScrollNestedGuiImpl;

class ScrollGuisGuiType implements GuiType<ScrollGui<Gui>, Gui> {
    
    @Override
    public ScrollNestedGuiImpl createGui(GuiContext<Gui> context) {
        ScrollNestedGuiImpl gui = new ScrollNestedGuiImpl(context.getContent(), context.getStructure());
        gui.setBackground(gui.getBackground());
        return gui;
    }
    
}
