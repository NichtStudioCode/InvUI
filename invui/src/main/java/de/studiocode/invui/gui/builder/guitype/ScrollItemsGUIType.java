package de.studiocode.invui.gui.builder.guitype;

import de.studiocode.invui.gui.ScrollGUI;
import de.studiocode.invui.gui.builder.GUIContext;
import de.studiocode.invui.gui.impl.ScrollItemsGUIImpl;
import de.studiocode.invui.item.Item;

class ScrollItemsGUIType implements GUIType<ScrollGUI<Item>, Item> {
    
    @Override
    public ScrollItemsGUIImpl createGUI(GUIContext<Item> context) {
        ScrollItemsGUIImpl gui = new ScrollItemsGUIImpl(context.getContent(), context.getStructure());
        gui.setBackground(context.getBackground());
        return gui;
    }
    
}