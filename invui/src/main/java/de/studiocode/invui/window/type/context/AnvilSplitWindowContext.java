package de.studiocode.invui.window.type.context;

import de.studiocode.invui.gui.GUI;
import de.studiocode.invui.gui.builder.GUIBuilder;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

public final class AnvilSplitWindowContext extends AbstractWindowContext<Player> {
    
    private Supplier<GUI> upperGuiSupplier;
    private Supplier<GUI> lowerGuiSupplier;
    private Consumer<String> renameHandler;
    
    public @Nullable GUI getUpperGUI() {
        return upperGuiSupplier.get();
    }
    
    public void setUpperGUI(@NotNull Supplier<GUI> guiSupplier) {
        this.upperGuiSupplier = guiSupplier;
    }
    
    public void setUpperGUI(@NotNull GUI gui) {
        this.upperGuiSupplier = () -> gui;
    }
    
    public void setUpperGUI(@NotNull GUIBuilder<?, ?> builder) {
        this.upperGuiSupplier = builder::build;
    }
    
    public @Nullable GUI getLowerGUI() {
        return lowerGuiSupplier.get();
    }
    
    public void setLowerGUI(@NotNull Supplier<GUI> guiSupplier) {
        this.lowerGuiSupplier = guiSupplier;
    }
    
    public void setLowerGUI(@NotNull GUI gui) {
        this.lowerGuiSupplier = () -> gui;
    }
    
    public void setLowerGUI(@NotNull GUIBuilder<?, ?> builder) {
        this.lowerGuiSupplier = builder::build;
    }
    
    public @Nullable Consumer<String> getRenameHandler() {
        return renameHandler;
    }
    
    public void setRenameHandler(@NotNull Consumer<String> renameHandler) {
        this.renameHandler = renameHandler;
    }
    
}
