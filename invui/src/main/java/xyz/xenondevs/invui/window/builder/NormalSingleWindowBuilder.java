package xyz.xenondevs.invui.window.builder;

import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.AbstractGui;
import xyz.xenondevs.invui.window.Window;
import xyz.xenondevs.invui.window.impl.NormalSingleWindowImpl;

import java.util.UUID;

public final class NormalSingleWindowBuilder extends AbstractSingleWindowBuilder<Window, UUID, NormalSingleWindowBuilder> {
    
    NormalSingleWindowBuilder() {
    }
    
    public NormalSingleWindowBuilder setViewer(@NotNull OfflinePlayer player) {
        setViewer(player.getUniqueId());
        return this;
    }
    
    @Override
    public @NotNull Window build() {
        var window = new NormalSingleWindowImpl(
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
    protected NormalSingleWindowBuilder getThis() {
        return this;
    }
    
}
