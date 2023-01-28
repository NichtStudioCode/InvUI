package de.studiocode.invui.gui.builder.guitype;

import de.studiocode.invui.gui.PagedGUI;
import de.studiocode.invui.gui.builder.GUIContext;
import de.studiocode.invui.gui.impl.PagedItemsGUIImpl;
import de.studiocode.invui.item.Item;

class PagedItemsGUIType implements GUIType<PagedGUI<Item>, Item> {
    
    @Override
    public PagedItemsGUIImpl createGUI(GUIContext<Item> context) {
        PagedItemsGUIImpl gui = new PagedItemsGUIImpl(context.getContent(), context.getStructure());
        gui.setBackground(context.getBackground());
        return gui;
    }
    
}
