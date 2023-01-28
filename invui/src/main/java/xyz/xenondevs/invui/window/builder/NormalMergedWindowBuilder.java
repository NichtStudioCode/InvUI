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
        var window = new NormalMergedWindowImpl(
            viewer,
            title,
            (AbstractGui) guiSupplier.get(),
            closeable,
            retain
        );
        
        applyChanges(window);
        
        return window;
    }
    
    @Override
    protected NormalMergedWindowBuilder getThis() {
        return this;
    }
    
}
