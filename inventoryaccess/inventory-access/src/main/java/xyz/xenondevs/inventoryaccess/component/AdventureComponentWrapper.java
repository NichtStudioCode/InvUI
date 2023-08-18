package xyz.xenondevs.inventoryaccess.component;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.inventoryaccess.component.i18n.AdventureComponentLocalizer;
import xyz.xenondevs.inventoryaccess.component.i18n.Languages;
import xyz.xenondevs.inventoryaccess.util.AdventureComponentUtils;

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
    public @NotNull AdventureComponentWrapper localized(@NotNull String lang) {
        if (!Languages.getInstance().doesServerSideTranslations())
            return this;
        
        return new AdventureComponentWrapper(AdventureComponentLocalizer.getInstance().localize(lang, component));
    }
    
    @Override
    public @NotNull AdventureComponentWrapper withoutPreFormatting() {
        return new AdventureComponentWrapper(AdventureComponentUtils.withoutPreFormatting(component));
    }
    
    @Override
    public @NotNull AdventureComponentWrapper clone() {
        try {
            return (AdventureComponentWrapper) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
    
}
