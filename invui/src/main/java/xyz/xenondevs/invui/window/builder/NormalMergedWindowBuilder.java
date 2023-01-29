package xyz.xenondevs.invui.window.builder;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.AbstractGui;
import xyz.xenondevs.invui.window.Window;
import xyz.xenondevs.invui.window.impl.NormalMergedWindowImpl;

public final class NormalMergedWindowBuilder extends AbstractSingleWindowBuilder<Window, Player, NormalMergedWindowBuilder> {
    
    NormalMergedWindowBuilder() {
    }
    
    @Override
    public @NotNull Window build() {
        if (viewer == null)
            throw new IllegalStateException("Viewer is not defined.");
        if (guiSupplier == null)
            throw new IllegalStateException("Gui is not defined.");
        
        var window = new NormalMergedWindowImpl(
            viewer,
            title,
            (AbstractGui) guiSupplier.get(),
            closeable,
            retain
        );
        
        applyModifiers(window);
        
        return window;
    }
    
    @Override
    protected NormalMergedWindowBuilder getThis() {
        return this;
    }
    
    @Override
    public @NotNull NormalMergedWindowBuilder clone() {
        return (NormalMergedWindowBuilder) super.clone();
    }
    
}
