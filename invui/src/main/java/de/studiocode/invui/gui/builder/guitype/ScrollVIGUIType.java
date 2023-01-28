package de.studiocode.invui.gui.builder.guitype;

import de.studiocode.invui.gui.ScrollGui;
import de.studiocode.invui.gui.builder.GuiContext;
import de.studiocode.invui.gui.impl.ScrollVIGuiImpl;
import de.studiocode.invui.virtualinventory.VirtualInventory;

class ScrollVIGuiType implements GuiType<ScrollGui<VirtualInventory>, VirtualInventory> {
    
    @Override
    public ScrollVIGuiImpl createGui(GuiContext<VirtualInventory> context) {
        ScrollVIGuiImpl gui = new ScrollVIGuiImpl(context.getContent(), context.getStructure());
        gui.setBackground(context.getBackground());
        return gui;
    }
    
}
