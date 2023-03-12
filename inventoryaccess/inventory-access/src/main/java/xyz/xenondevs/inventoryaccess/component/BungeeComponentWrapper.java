package xyz.xenondevs.inventoryaccess.component;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.inventoryaccess.component.i18n.BungeeComponentLocalizer;
import xyz.xenondevs.inventoryaccess.component.i18n.Languages;
import xyz.xenondevs.inventoryaccess.util.BungeeComponentUtils;

public class BungeeComponentWrapper implements ComponentWrapper {
    
    private final BaseComponent[] components;
    
    public BungeeComponentWrapper(BaseComponent[] components) {
        this.components = components;
    }
    
    @Override
    public @NotNull BungeeComponentWrapper localized(@NotNull String lang) {
        if (!Languages.getInstance().doesServerSideTranslations())
            return this;
        
        return new BungeeComponentWrapper(BungeeComponentLocalizer.getInstance().localize(lang, components));
    }
    
    @Override
    public @NotNull BungeeComponentWrapper withoutPreFormatting() {
        return new BungeeComponentWrapper(BungeeComponentUtils.withoutPreFormatting(components));
    }
    
    @Override
    public @NotNull String serializeToJson() {
        return ComponentSerializer.toString(components);
    }
    
    @Override
    public @NotNull BungeeComponentWrapper clone() {
        try {
            var clone = (BungeeComponentWrapper) super.clone();
            for (int i = 0; i < clone.components.length; i++) {
                clone.components[i] = clone.components[i].duplicate();
            }
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
    
}
