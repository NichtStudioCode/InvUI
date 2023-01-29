package xyz.xenondevs.invui.window.builder;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.AbstractGui;
import xyz.xenondevs.invui.window.AnvilWindow;
import xyz.xenondevs.invui.window.impl.AnvilSingleWindowImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public final class AnvilSingleWindowBuilder extends AbstractSingleWindowBuilder<AnvilWindow, Player, AnvilSingleWindowBuilder> {
    
    private List<Consumer<String>> renameHandlers;
    
    AnvilSingleWindowBuilder() {
    }
    
    @Contract("_ -> this")
    public AnvilSingleWindowBuilder setRenameHandlers(@NotNull List<@NotNull Consumer<String>> renameHandlers) {
        this.renameHandlers = renameHandlers;
        return this;
    }
    
    @Contract("_ -> this")
    public AnvilSingleWindowBuilder addRenameHandler(@NotNull Consumer<String> renameHandler) {
        if (renameHandlers == null)
            renameHandlers = new ArrayList<>();
        
        renameHandlers.add(renameHandler);
        return this;
    }
    
    @Override
    public @NotNull AnvilWindow build() {
        if (viewer == null)
            throw new IllegalStateException("Viewer is not defined.");
        if (guiSupplier == null)
            throw new IllegalStateException("Gui is not defined.");
        
        var window = new AnvilSingleWindowImpl(
            viewer,
            title,
            (AbstractGui) guiSupplier.get(),
            renameHandlers,
            closeable,
            retain
        );
    
        applyModifiers(window);
    
        return window;
    }
    
    @Override
    protected AnvilSingleWindowBuilder getThis() {
        return null;
    }
    
    @Override
    public @NotNull AnvilSingleWindowBuilder clone() {
        return (AnvilSingleWindowBuilder) super.clone();
    }
    
}
