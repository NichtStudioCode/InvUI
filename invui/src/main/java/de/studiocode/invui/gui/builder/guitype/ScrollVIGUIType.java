package de.studiocode.invui.gui.builder.guitype;

import de.studiocode.invui.gui.ScrollGUI;
import de.studiocode.invui.gui.builder.GUIContext;
import de.studiocode.invui.gui.impl.ScrollVIGUIImpl;
import de.studiocode.invui.virtualinventory.VirtualInventory;

class ScrollVIGUIType implements GUIType<ScrollGUI<VirtualInventory>, VirtualInventory> {
    
    @Override
    public ScrollVIGUIImpl createGUI(GUIContext<VirtualInventory> context) {
        ScrollVIGUIImpl gui = new ScrollVIGUIImpl(context.getContent(), context.getStructure());
        gui.setBackground(context.getBackground());
        return gui;
    }
    
}
