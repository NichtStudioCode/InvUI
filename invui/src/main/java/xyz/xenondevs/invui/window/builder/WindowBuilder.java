package xyz.xenondevs.invui.window.builder;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.window.Window;

public interface WindowBuilder<W extends Window> extends Cloneable {
    
    @Contract("-> new")
    @NotNull W build();
    
    @Contract("-> new")
    @NotNull WindowBuilder<W> clone();
    
}
