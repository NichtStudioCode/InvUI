package de.studiocode.invui.window.type.context;

import de.studiocode.invui.gui.Gui;
import de.studiocode.invui.gui.builder.GuiBuilder;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

public final class AnvilSplitWindowContext extends AbstractWindowContext<Player> {
    
    private Supplier<Gui> upperGuiSupplier;
    private Supplier<Gui> lowerGuiSupplier;
    private Consumer<String> renameHandler;
    
    public @Nullable Gui getUpperGui() {
        return upperGuiSupplier.get();
    }
    
    public void setUpperGui(@NotNull Supplier<Gui> guiSupplier) {
        this.upperGuiSupplier = guiSupplier;
    }
    
    public void setUpperGui(@NotNull Gui gui) {
        this.upperGuiSupplier = () -> gui;
    }
    
    public void setUpperGui(@NotNull GuiBuilder<?, ?> builder) {
        this.upperGuiSupplier = builder::build;
    }
    
    public @Nullable Gui getLowerGui() {
        return lowerGuiSupplier.get();
    }
    
    public void setLowerGui(@NotNull Supplier<Gui> guiSupplier) {
        this.lowerGuiSupplier = guiSupplier;
    }
    
    public void setLowerGui(@NotNull Gui gui) {
        this.lowerGuiSupplier = () -> gui;
    }
    
    public void setLowerGui(@NotNull GuiBuilder<?, ?> builder) {
        this.lowerGuiSupplier = builder::build;
    }
    
    public @Nullable Consumer<String> getRenameHandler() {
        return renameHandler;
    }
    
    public void setRenameHandler(@NotNull Consumer<String> renameHandler) {
        this.renameHandler = renameHandler;
    }
    
}
