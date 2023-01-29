package xyz.xenondevs.invui.gui.builder;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.Gui;

public interface GuiBuilder<G extends Gui> extends Cloneable {
    
    @Contract("-> new")
    @NotNull G build();
    
    @Contract("-> new")
    @NotNull GuiBuilder<G> clone();
    
}
