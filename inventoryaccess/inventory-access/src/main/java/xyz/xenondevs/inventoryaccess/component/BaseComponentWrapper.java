package xyz.xenondevs.inventoryaccess.component;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.jetbrains.annotations.NotNull;

public class BaseComponentWrapper implements ComponentWrapper {
    
    private final BaseComponent[] components;
    
    public BaseComponentWrapper(BaseComponent[] components) {
        this.components = components;
    }
    
    @Override
    public @NotNull String serializeToJson() {
        return ComponentSerializer.toString(components);
    }
    
    @Override
    public @NotNull BaseComponentWrapper clone() {
        try {
            var clone = (BaseComponentWrapper) super.clone();
            for (int i = 0; i < clone.components.length; i++) {
                clone.components[i] = clone.components[i].duplicate();
            }
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
    
}
