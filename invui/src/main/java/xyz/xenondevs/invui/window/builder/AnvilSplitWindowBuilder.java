package xyz.xenondevs.invui.window.builder;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.AbstractGui;
import xyz.xenondevs.invui.window.AnvilWindow;
import xyz.xenondevs.invui.window.impl.AnvilSplitWindowImpl;

import java.util.function.Consumer;

public final class AnvilSplitWindowBuilder extends AbstractSplitWindowBuilder<AnvilWindow, Player, AnvilSplitWindowBuilder> {
    
    private Consumer<String> renameHandler;
    
    AnvilSplitWindowBuilder() {
    }
    
    public AnvilSplitWindowBuilder setRenameHandler(@NotNull Consumer<String> renameHandler) {
        this.renameHandler = renameHandler;
        return this;
    }
    
    @Override
    public @NotNull AnvilWindow build() {
        var window = new AnvilSplitWindowImpl(
            viewer,
            title,
            (AbstractGui) upperGuiSupplier.get(),
            (AbstractGui) lowerGuiSupplier.get(),
            renameHandler,
            closeable,
            retain
        );
        
        applyChanges(window);
        
        return window;
    }
    
    @Override
    protected AnvilSplitWindowBuilder getThis() {
        return this;
    }
    
}
