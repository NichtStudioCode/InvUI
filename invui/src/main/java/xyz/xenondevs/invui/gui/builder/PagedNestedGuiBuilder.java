package xyz.xenondevs.invui.gui.builder;

import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.PagedGui;
import xyz.xenondevs.invui.gui.impl.PagedNestedGuiImpl;

public final class PagedNestedGuiBuilder extends AbstractPagedGuiBuilder<Gui, PagedNestedGuiBuilder> {
    
    PagedNestedGuiBuilder() {
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public @NotNull PagedGui<Gui> build() {
        if (structure == null)
            throw new IllegalStateException("Structure is not defined.");
        
        var gui = new PagedNestedGuiImpl(content, structure);
        applyModifiers(gui);
        return gui;
    }
    
    @Override
    protected PagedNestedGuiBuilder getThis() {
        return this;
    }
    
    @Override
    public @NotNull PagedNestedGuiBuilder clone() {
        return (PagedNestedGuiBuilder) super.clone();
    }
    
}
