package de.studiocode.invui.window.type.context;

import de.studiocode.invui.gui.Gui;
import de.studiocode.invui.gui.builder.GuiBuilder;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Supplier;

public final class NormalSingleWindowContext extends AbstractWindowContext<UUID> {
    
    private Supplier<Gui> guiSupplier;
    
    public @Nullable Gui getGui() {
        return guiSupplier.get();
    }
    
    public void setGui(@NotNull Supplier<Gui> guiSupplier) {
        this.guiSupplier = guiSupplier;
    }
    
    public void setGui(@NotNull Gui gui) {
        this.guiSupplier = () -> gui;
    }
    
    public void setGui(@NotNull GuiBuilder<?, ?> builder) {
        this.guiSupplier = builder::build;
    }
    
    public void setViewer(@NotNull OfflinePlayer player) {
        this.viewer = player.getUniqueId();
    }
    
}
