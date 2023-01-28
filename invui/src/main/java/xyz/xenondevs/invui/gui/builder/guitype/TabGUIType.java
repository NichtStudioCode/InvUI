package xyz.xenondevs.invui.gui.builder.guitype;

import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.TabGui;
import xyz.xenondevs.invui.gui.builder.GuiContext;
import xyz.xenondevs.invui.gui.impl.TabGuiImpl;

class TabGuiType implements GuiType<TabGui, Gui> {
    
    @Override
    public TabGuiImpl createGui(GuiContext<Gui> context) {
        TabGuiImpl gui = new TabGuiImpl(context.getContent(), context.getStructure());
        gui.setBackground(context.getBackground());
        return gui;
    }
    
}
