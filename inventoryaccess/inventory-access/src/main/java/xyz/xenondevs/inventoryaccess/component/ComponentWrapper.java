package xyz.xenondevs.inventoryaccess.component;

import org.jetbrains.annotations.NotNull;

public interface ComponentWrapper extends Cloneable {
    
    @NotNull String serializeToJson();
    
    @NotNull ComponentWrapper clone();
    
}
