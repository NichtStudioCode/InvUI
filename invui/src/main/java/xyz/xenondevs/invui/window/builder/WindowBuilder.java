package xyz.xenondevs.invui.window.builder;

import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.window.Window;

public interface WindowBuilder<W extends Window> {
    
    @NotNull W build();
    
}
