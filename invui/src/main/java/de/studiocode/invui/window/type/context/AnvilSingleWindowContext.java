package de.studiocode.invui.window.type.context;

import de.studiocode.invui.gui.GUI;
import de.studiocode.invui.gui.builder.GUIBuilder;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

public final class AnvilSingleWindowContext extends AbstractWindowContext<Player> {
    
    private Supplier<GUI> guiSupplier;
    private Consumer<String> renameHandler;
    
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
    
    public @Nullable Consumer<String> getRenameHandler() {
        return renameHandler;
    }
    
    public void setRenameHandler(@NotNull Consumer<String> renameHandler) {
        this.renameHandler = renameHandler;
    }
    
}
