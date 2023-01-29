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
        if (viewer == null)
            throw new IllegalStateException("Viewer is not defined.");
        if (guiSupplier == null)
            throw new IllegalStateException("Gui is not defined.");
        
        var window = new CartographySingleWindowImpl(
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
    protected CartographySingleWindowBuilder getThis() {
        return this;
    }
    
    @Override
    public @NotNull CartographySingleWindowBuilder clone() {
        return (CartographySingleWindowBuilder) super.clone();
    }
    
}
