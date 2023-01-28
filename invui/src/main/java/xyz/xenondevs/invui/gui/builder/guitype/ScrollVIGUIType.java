package xyz.xenondevs.invui.gui.builder.guitype;

import xyz.xenondevs.invui.gui.ScrollGui;
import xyz.xenondevs.invui.gui.builder.GuiContext;
import xyz.xenondevs.invui.gui.impl.ScrollVIGuiImpl;
import xyz.xenondevs.invui.virtualinventory.VirtualInventory;

class ScrollVIGuiType implements GuiType<ScrollGui<VirtualInventory>, VirtualInventory> {
    
    @Override
    public ScrollVIGuiImpl createGui(GuiContext<VirtualInventory> context) {
        ScrollVIGuiImpl gui = new ScrollVIGuiImpl(context.getContent(), context.getStructure());
        gui.setBackground(context.getBackground());
        return gui;
    }
    
}
