package xyz.xenondevs.invui.gui.builder.guitype;

import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.ScrollGui;
import xyz.xenondevs.invui.gui.builder.GuiContext;
import xyz.xenondevs.invui.gui.impl.ScrollNestedGuiImpl;

class ScrollGuisGuiType implements GuiType<ScrollGui<Gui>, Gui> {
    
    @Override
    public ScrollNestedGuiImpl createGui(GuiContext<Gui> context) {
        ScrollNestedGuiImpl gui = new ScrollNestedGuiImpl(context.getContent(), context.getStructure());
        gui.setBackground(gui.getBackground());
        return gui;
    }
    
}
