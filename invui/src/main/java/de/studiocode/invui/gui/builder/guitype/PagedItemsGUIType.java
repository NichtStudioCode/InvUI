package de.studiocode.invui.gui.builder.guitype;

import de.studiocode.invui.gui.PagedGui;
import de.studiocode.invui.gui.builder.GuiContext;
import de.studiocode.invui.gui.impl.PagedItemsGuiImpl;
import de.studiocode.invui.item.Item;

class PagedItemsGuiType implements GuiType<PagedGui<Item>, Item> {
    
    @Override
    public PagedItemsGuiImpl createGui(GuiContext<Item> context) {
        PagedItemsGuiImpl gui = new PagedItemsGuiImpl(context.getContent(), context.getStructure());
        gui.setBackground(context.getBackground());
        return gui;
    }
    
}
