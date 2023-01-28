package xyz.xenondevs.invui.window.builder;

import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.builder.GuiBuilder;
import xyz.xenondevs.invui.window.Window;

import java.util.function.Supplier;

public abstract class AbstractSplitWindowBuilder<W extends Window, V, S extends AbstractSplitWindowBuilder<W, V, S>> extends AbstractWindowBuilder<W, V, S> {
    
    protected Supplier<Gui> upperGuiSupplier;
    protected Supplier<Gui> lowerGuiSupplier;
    
    public S setUpperGui(@NotNull Supplier<Gui> guiSupplier) {
        this.upperGuiSupplier = guiSupplier;
        return getThis();
    }
    
    public S setUpperGui(@NotNull Gui gui) {
        this.upperGuiSupplier = () -> gui;
        return getThis();
    }
    
    public S setUpperGui(@NotNull GuiBuilder<?, ?> builder) {
        this.upperGuiSupplier = builder::build;
        return getThis();
    }
    
    public S setLowerGui(@NotNull Supplier<Gui> guiSupplier) {
        this.lowerGuiSupplier = guiSupplier;
        return getThis();
    }
    
    public S setLowerGui(@NotNull Gui gui) {
        this.lowerGuiSupplier = () -> gui;
        return getThis();
    }
    
    public S setLowerGui(@NotNull GuiBuilder<?, ?> builder) {
        this.lowerGuiSupplier = builder::build;
        return getThis();
    }
    
}
