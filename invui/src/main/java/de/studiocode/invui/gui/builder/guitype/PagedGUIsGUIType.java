package de.studiocode.invui.gui.builder.guitype;

import de.studiocode.invui.gui.Gui;
import de.studiocode.invui.gui.PagedGui;
import de.studiocode.invui.gui.builder.GuiContext;
import de.studiocode.invui.gui.impl.PagedNestedGuiImpl;

class PagedGuisGuiType implements GuiType<PagedGui<Gui>, Gui> {
    
    @Override
    public PagedNestedGuiImpl createGui(GuiContext<Gui> context) {
        PagedNestedGuiImpl gui = new PagedNestedGuiImpl(context.getContent(), context.getStructure());
        gui.setBackground(context.getBackground());
        return gui;
    }
    
}
