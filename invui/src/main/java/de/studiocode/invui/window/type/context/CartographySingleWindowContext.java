package de.studiocode.invui.window.type.context;

import de.studiocode.invui.gui.Gui;
import de.studiocode.invui.gui.builder.GuiBuilder;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public final class CartographySingleWindowContext extends AbstractWindowContext<Player> {
    
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
    
}
