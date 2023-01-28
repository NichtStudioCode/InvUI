package xyz.xenondevs.invui.gui.builder.guitype;

import xyz.xenondevs.invui.gui.PagedGui;
import xyz.xenondevs.invui.gui.builder.GuiContext;
import xyz.xenondevs.invui.gui.impl.PagedItemsGuiImpl;
import xyz.xenondevs.invui.item.Item;

class PagedItemsGuiType implements GuiType<PagedGui<Item>, Item> {
    
    @Override
    public PagedItemsGuiImpl createGui(GuiContext<Item> context) {
        PagedItemsGuiImpl gui = new PagedItemsGuiImpl(context.getContent(), context.getStructure());
        gui.setBackground(context.getBackground());
        return gui;
    }
    
}
