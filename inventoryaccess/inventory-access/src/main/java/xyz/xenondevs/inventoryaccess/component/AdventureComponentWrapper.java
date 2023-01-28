package xyz.xenondevs.inventoryaccess.component;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;

public class AdventureComponentWrapper implements ComponentWrapper {
    
    private final Component component;
    
    public AdventureComponentWrapper(Component component) {
        this.component = component;
    }
    
    @Override
    public String serializeToJson() {
        return GsonComponentSerializer.gson().serialize(component);
    }
    
}
