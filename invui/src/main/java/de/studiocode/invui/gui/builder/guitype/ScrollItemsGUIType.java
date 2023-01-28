package de.studiocode.invui.gui.builder.guitype;

import de.studiocode.invui.gui.ScrollGui;
import de.studiocode.invui.gui.builder.GuiContext;
import de.studiocode.invui.gui.impl.ScrollItemsGuiImpl;
import de.studiocode.invui.item.Item;

class ScrollItemsGuiType implements GuiType<ScrollGui<Item>, Item> {
    
    @Override
    public ScrollItemsGuiImpl createGui(GuiContext<Item> context) {
        ScrollItemsGuiImpl gui = new ScrollItemsGuiImpl(context.getContent(), context.getStructure());
        gui.setBackground(context.getBackground());
        return gui;
    }
    
}