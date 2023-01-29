package xyz.xenondevs.invui.gui.builder;

import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.ScrollGui;
import xyz.xenondevs.invui.gui.impl.ScrollNestedGuiImpl;

public final class ScrollNestedGuiBuilder extends AbstractScrollGuiBuilder<Gui, ScrollNestedGuiBuilder>{
    
    ScrollNestedGuiBuilder() {
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public @NotNull ScrollGui<Gui> build() {
        if (structure == null)
            throw new IllegalStateException("Structure is not defined.");
        
        var gui = new ScrollNestedGuiImpl(content, structure);
        applyModifiers(gui);
        return gui;
    }
    
    @Override
    protected ScrollNestedGuiBuilder getThis() {
        return this;
    }
    
    @Override
    public @NotNull ScrollNestedGuiBuilder clone() {
        return (ScrollNestedGuiBuilder) super.clone();
    }
    
}
