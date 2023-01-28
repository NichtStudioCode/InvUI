package xyz.xenondevs.invui.gui.builder.guitype;

import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.ScrollGui;
import xyz.xenondevs.invui.gui.builder.GuiContext;
import xyz.xenondevs.invui.gui.impl.ScrollNestedGuiImpl;

class ScrollGuisGuiType implements GuiType<ScrollGui<Gui>, Gui> {
    
    @Override
    public @NotNull ScrollNestedGuiImpl createGui(@NotNull GuiContext<Gui> context) {
        ScrollNestedGuiImpl gui = new ScrollNestedGuiImpl(context.getContent(), context.getStructure());
        gui.setBackground(gui.getBackground());
        return gui;
    }
    
}
