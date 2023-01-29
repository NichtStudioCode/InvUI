package xyz.xenondevs.invui.gui.builder;

import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.impl.NormalGuiImpl;

public final class NormalGuiBuilder extends AbstractGuiBuilder<Gui, NormalGuiBuilder> {
    
    NormalGuiBuilder() {
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public @NotNull Gui build() {
        if (structure == null)
            throw new IllegalStateException("Structure is not defined.");
        
        var gui = new NormalGuiImpl(structure);
        applyModifiers(gui);
        return gui;
    }
    
    @Override
    protected NormalGuiBuilder getThis() {
        return this;
    }
    
    @Override
    public @NotNull NormalGuiBuilder clone() {
        return (NormalGuiBuilder) super.clone();
    }
    
}
