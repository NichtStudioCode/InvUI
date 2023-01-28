package xyz.xenondevs.invui.window.builder;

import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.builder.GuiBuilder;
import xyz.xenondevs.invui.window.Window;

import java.util.function.Supplier;

public abstract class AbstractSingleWindowBuilder<W extends Window, V, S extends AbstractSingleWindowBuilder<W, V, S>> extends AbstractWindowBuilder<W, V, S> {
    
    protected Supplier<Gui> guiSupplier;
    
    public S setGui(@NotNull Supplier<Gui> guiSupplier) {
        this.guiSupplier = guiSupplier;
        return getThis();
    }
    
    public S setGui(@NotNull Gui gui) {
        this.guiSupplier = () -> gui;
        return getThis();
    }
    
    public S setGui(@NotNull GuiBuilder<?, ?> builder) {
        this.guiSupplier = builder::build;
        return getThis();
    }    
    
    
}
