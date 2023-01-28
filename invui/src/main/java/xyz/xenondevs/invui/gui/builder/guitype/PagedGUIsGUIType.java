package xyz.xenondevs.invui.gui.builder.guitype;

import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.PagedGui;
import xyz.xenondevs.invui.gui.builder.GuiContext;
import xyz.xenondevs.invui.gui.impl.PagedNestedGuiImpl;

class PagedGuisGuiType implements GuiType<PagedGui<Gui>, Gui> {
    
    @Override
    public @NotNull PagedNestedGuiImpl createGui(@NotNull GuiContext<Gui> context) {
        PagedNestedGuiImpl gui = new PagedNestedGuiImpl(context.getContent(), context.getStructure());
        gui.setBackground(context.getBackground());
        return gui;
    }
    
}
