package de.studiocode.inventoryaccess.component;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;

public class BaseComponentWrapper implements ComponentWrapper {
    
    private final BaseComponent[] components;
    
    public BaseComponentWrapper(BaseComponent[] components) {
        this.components = components;
    }
    
    @Override
    public String serializeToJson() {
        return ComponentSerializer.toString(components);
    }

}
