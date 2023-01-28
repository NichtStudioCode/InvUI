package de.studiocode.invui.window.type.context;

import de.studiocode.invui.gui.GUI;
import de.studiocode.invui.gui.builder.GUIBuilder;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Supplier;

public final class NormalSingleWindowContext extends AbstractWindowContext<UUID> {
    
    private Supplier<GUI> guiSupplier;
    
    public @Nullable GUI getGUI() {
        return guiSupplier.get();
    }
    
    public void setGUI(@NotNull Supplier<GUI> guiSupplier) {
        this.guiSupplier = guiSupplier;
    }
    
    public void setGUI(@NotNull GUI gui) {
        this.guiSupplier = () -> gui;
    }
    
    public void setGUI(@NotNull GUIBuilder<?, ?> builder) {
        this.guiSupplier = builder::build;
    }
    
    public void setViewer(@NotNull OfflinePlayer player) {
        this.viewer = player.getUniqueId();
    }
    
}
