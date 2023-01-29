package xyz.xenondevs.inventoryaccess.component;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.jetbrains.annotations.NotNull;

public class AdventureComponentWrapper implements ComponentWrapper {
    
    private final Component component;
    
    public AdventureComponentWrapper(Component component) {
        this.component = component;
    }
    
    @Override
    public @NotNull String serializeToJson() {
        return GsonComponentSerializer.gson().serialize(component);
    }
    
    @Override
    public @NotNull ComponentWrapper clone() {
        try {
            return (ComponentWrapper) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
    
}
