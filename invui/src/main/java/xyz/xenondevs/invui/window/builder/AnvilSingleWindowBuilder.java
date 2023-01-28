package xyz.xenondevs.invui.window.builder;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.AbstractGui;
import xyz.xenondevs.invui.window.AnvilWindow;
import xyz.xenondevs.invui.window.impl.AnvilSingleWindowImpl;

import java.util.function.Consumer;

public final class AnvilSingleWindowBuilder extends AbstractSingleWindowBuilder<AnvilWindow, Player, AnvilSingleWindowBuilder> {
    
    private Consumer<String> renameHandler;
    
    AnvilSingleWindowBuilder() {
    }
    
    public void setRenameHandler(@NotNull Consumer<String> renameHandler) {
        this.renameHandler = renameHandler;
    }
    
    @Override
    public @NotNull AnvilWindow build() {
        var window = new AnvilSingleWindowImpl(
            viewer,
            title,
            (AbstractGui) guiSupplier.get(),
            renameHandler,
            closeable,
            retain
        );
    
        applyChanges(window);
    
        return window;
    }
    
    @Override
    protected AnvilSingleWindowBuilder getThis() {
        return null;
    }
    
}
