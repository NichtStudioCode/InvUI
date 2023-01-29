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
        if (viewer == null)
            throw new IllegalStateException("Viewer is not defined.");
        if (upperGuiSupplier == null)
            throw new IllegalStateException("Upper Gui is not defined.");
        
        var window = new CartographySplitWindowImpl(
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
    protected CartographySplitWindowBuilder getThis() {
        return this;
    }
    
    @Override
    public @NotNull CartographySplitWindowBuilder clone() {
        return (CartographySplitWindowBuilder) super.clone();
    }
    
}
