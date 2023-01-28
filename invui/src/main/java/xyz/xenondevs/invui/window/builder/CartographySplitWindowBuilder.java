package xyz.xenondevs.invui.window.builder;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.AbstractGui;
import xyz.xenondevs.invui.window.CartographyWindow;
import xyz.xenondevs.invui.window.impl.CartographySplitWindowImpl;

public final class CartographySplitWindowBuilder extends AbstractSplitWindowBuilder<CartographyWindow, Player, CartographySplitWindowBuilder> {
    
    CartographySplitWindowBuilder() {
    }
    
    @Override
    public @NotNull CartographyWindow build() {
        var window = new CartographySplitWindowImpl(
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
    protected CartographySplitWindowBuilder getThis() {
        return this;
    }
    
}
