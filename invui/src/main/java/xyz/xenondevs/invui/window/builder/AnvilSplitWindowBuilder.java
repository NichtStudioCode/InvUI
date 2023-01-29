package xyz.xenondevs.invui.window.builder;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.AbstractGui;
import xyz.xenondevs.invui.window.AnvilWindow;
import xyz.xenondevs.invui.window.impl.AnvilSplitWindowImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public final class AnvilSplitWindowBuilder extends AbstractSplitWindowBuilder<AnvilWindow, Player, AnvilSplitWindowBuilder> {
    
    private List<Consumer<String>> renameHandlers;
    
    AnvilSplitWindowBuilder() {
    }
    
    @Contract("_ -> this")
    public AnvilSplitWindowBuilder setRenameHandlers(@NotNull List<@NotNull Consumer<String>> renameHandlers) {
        this.renameHandlers = renameHandlers;
        return this;
    }
    
    @Contract("_ -> this")
    public AnvilSplitWindowBuilder addRenameHandler(@NotNull Consumer<String> renameHandler) {
        if (renameHandlers == null)
            renameHandlers = new ArrayList<>();
        
        renameHandlers.add(renameHandler);
        return this;
    }
    
    @Override
    public @NotNull AnvilWindow build() {
        if (viewer == null)
            throw new IllegalStateException("Viewer is not defined.");
        if (upperGuiSupplier == null)
            throw new IllegalStateException("Upper Gui is not defined.");
        if (lowerGuiSupplier == null)
            throw new IllegalStateException("Lower Gui is not defined.");
        
        var window = new AnvilSplitWindowImpl(
            viewer,
            title,
            (AbstractGui) upperGuiSupplier.get(),
            (AbstractGui) lowerGuiSupplier.get(),
            renameHandlers,
            closeable,
            retain
        );
        
        applyModifiers(window);
        
        return window;
    }
    
    @Override
    protected AnvilSplitWindowBuilder getThis() {
        return this;
    }
    
    @Override
    public @NotNull AnvilSplitWindowBuilder clone() {
        return (AnvilSplitWindowBuilder) super.clone();
    }
    
}
