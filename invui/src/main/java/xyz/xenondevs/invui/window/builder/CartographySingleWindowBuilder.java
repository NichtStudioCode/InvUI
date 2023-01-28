package xyz.xenondevs.invui.window.builder;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.AbstractGui;
import xyz.xenondevs.invui.window.CartographyWindow;
import xyz.xenondevs.invui.window.impl.CartographySingleWindowImpl;

public final class CartographySingleWindowBuilder extends AbstractSingleWindowBuilder<CartographyWindow, Player, CartographySingleWindowBuilder> {
    
    CartographySingleWindowBuilder() {
    }
    
    @Override
    public @NotNull CartographyWindow build() {
        var window = new CartographySingleWindowImpl(
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
    protected CartographySingleWindowBuilder getThis() {
        return this;
    }
    
}
