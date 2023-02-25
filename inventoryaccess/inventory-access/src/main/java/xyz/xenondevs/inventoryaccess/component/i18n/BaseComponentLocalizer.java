package xyz.xenondevs.inventoryaccess.component.i18n;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;

import java.util.stream.Collectors;

public class BaseComponentLocalizer extends ComponentLocalizer<BaseComponent> {
    
    private static final BaseComponentLocalizer INSTANCE = new BaseComponentLocalizer();
    
    private BaseComponentLocalizer() {
    }
    
    public static BaseComponentLocalizer getInstance() {
        return INSTANCE;
    }
    
    public BaseComponent[] localize(String lang, BaseComponent[] components) {
        var localizedComponents = new BaseComponent[components.length];
        for (int i = 0; i < components.length; i++) {
            localizedComponents[i] = localize(lang, components[i]);
        }
        return localizedComponents;
    }
    
    @Override
    public BaseComponent localize(String lang, BaseComponent component) {
        BaseComponent duplicate;
        if (component instanceof TranslatableComponent) {
            duplicate = localizeTranslatable(lang, (TranslatableComponent) component);
        } else {
            duplicate = component.duplicate();
        }
        
        var extra = duplicate.getExtra();
        if (extra != null) {
            duplicate.setExtra(
                extra.stream()
                    .map(child -> localize(lang, child))
                    .collect(Collectors.toList())
            );
        }
        
        return duplicate;
    }
    
    private BaseComponent localizeTranslatable(String lang, TranslatableComponent component) {
        var formatString = Languages.getInstance().getFormatString(lang, component.getTranslate());
        if (formatString == null)
            return component;
        
        var children = decomposeFormatString(lang, formatString, component, component.getWith());
        var result = new TextComponent(children.toArray(BaseComponent[]::new));
        
        result.copyFormatting(component);
        
        var extra = component.getExtra();
        if (extra != null)
            result.setExtra(extra);
        
        return result;
    }
    
    @Override
    protected BaseComponent createTextComponent(String text) {
        return new TextComponent(text);
    }
    
}
