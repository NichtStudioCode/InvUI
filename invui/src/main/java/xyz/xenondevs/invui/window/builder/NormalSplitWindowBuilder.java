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
        var window = new NormalSplitWindowImpl(
            viewer,
            title,
            (AbstractGui) upperGuiSupplier.get(),
            (AbstractGui) lowerGuiSupplier.get(),
            closeable,
            retain
        );
        
        applyChanges(window);
        
        return window;
    }
    
    @Override
    protected NormalSplitWindowBuilder getThis() {
        return this;
    }
    
}
