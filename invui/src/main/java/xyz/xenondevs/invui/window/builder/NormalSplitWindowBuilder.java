package xyz.xenondevs.invui.window.builder;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.AbstractGui;
import xyz.xenondevs.invui.window.Window;
import xyz.xenondevs.invui.window.impl.NormalSplitWindowImpl;

public final class NormalSplitWindowBuilder extends AbstractSplitWindowBuilder<Window, Player, NormalSplitWindowBuilder> {
    
    NormalSplitWindowBuilder() {
    }
    
    @Override
    public @NotNull Window build() {
        if (viewer == null)
            throw new IllegalStateException("Viewer is not defined.");
        if (upperGuiSupplier == null)
            throw new IllegalStateException("Upper Gui is not defined.");
        if (lowerGuiSupplier == null)
            throw new IllegalStateException("Lower Gui is not defined.");
        
        var window = new NormalSplitWindowImpl(
            viewer,
            title,
            (AbstractGui) upperGuiSupplier.get(),
            (AbstractGui) lowerGuiSupplier.get(),
            closeable,
            retain
        );
        
        applyModifiers(window);
        
        return window;
    }
    
    @Override
    protected NormalSplitWindowBuilder getThis() {
        return this;
    }
    
    @Override
    public @NotNull NormalSplitWindowBuilder clone() {
        return (NormalSplitWindowBuilder) super.clone();
    }
    
}
