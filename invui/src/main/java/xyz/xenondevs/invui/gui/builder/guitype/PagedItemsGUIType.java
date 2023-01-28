package xyz.xenondevs.invui.gui.builder.guitype;

import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.PagedGui;
import xyz.xenondevs.invui.gui.builder.GuiContext;
import xyz.xenondevs.invui.gui.impl.PagedItemsGuiImpl;
import xyz.xenondevs.invui.item.Item;

class PagedItemsGuiType implements GuiType<PagedGui<Item>, Item> {
    
    @Override
    public @NotNull PagedItemsGuiImpl createGui(@NotNull GuiContext<Item> context) {
        PagedItemsGuiImpl gui = new PagedItemsGuiImpl(context.getContent(), context.getStructure());
        gui.setBackground(context.getBackground());
        return gui;
    }
    
}
