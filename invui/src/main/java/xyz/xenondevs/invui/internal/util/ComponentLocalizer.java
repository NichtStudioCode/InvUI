package xyz.xenondevs.invui.internal.util;

import net.kyori.adventure.text.*;
import net.kyori.adventure.text.minimessage.MiniMessage;
import xyz.xenondevs.invui.i18n.Languages;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;

public class ComponentLocalizer {
    
    private static final Pattern FORMAT_PATTERN = Pattern.compile("%(?:(\\d+)\\$)?([A-Za-z%]|$)");
    private static final ComponentLocalizer INSTANCE = new ComponentLocalizer();
    
    private Function<String, Component> componentCreator = MiniMessage.miniMessage()::deserialize;
    
    private ComponentLocalizer() {
    }
    
    public static ComponentLocalizer getInstance() {
        return INSTANCE;
    }
    
    public void setComponentCreator(Function<String, Component> componentCreator) {
        this.componentCreator = componentCreator;
    }
    
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Component localize(String lang, Component component) {
        if (!(component instanceof BuildableComponent))
            throw new IllegalStateException("Component is not a BuildableComponent");
        
        return localize(lang, (BuildableComponent) component);
    }
    
    @SuppressWarnings("NonExtendableApiUsage")
    private <C extends BuildableComponent<C, B>, B extends ComponentBuilder<C, B>> BuildableComponent<?, ?> localize(String lang, BuildableComponent<C, B> component) {
        ComponentBuilder<?, ?> builder;
        if (component instanceof TranslatableComponent) {
            builder = localizeTranslatable(lang, (TranslatableComponent) component).toBuilder();
        } else {
            builder = component.toBuilder();
        }
        
        builder.mapChildrenDeep(child -> {
            if (child instanceof TranslatableComponent)
                return localizeTranslatable(lang, (TranslatableComponent) child);
            return child;
        });
        
        return builder.build();
    }
    
    private BuildableComponent<?, ?> localizeTranslatable(String lang, TranslatableComponent component) {
        var formatString = Languages.getInstance().getFormatString(lang, component.key());
        if (formatString == null)
            return component;
        
        var children = decomposeFormatString(lang, formatString, component.arguments());
        return Component.textOfChildren(children.toArray(ComponentLike[]::new)).style(component.style());
    }
    
    private List<Component> decomposeFormatString(String lang, String formatString, List<TranslationArgument> args) {
        var matcher = FORMAT_PATTERN.matcher(formatString);
        
        var components = new ArrayList<Component>();
        var sb = new StringBuilder();
        var nextArgIdx = 0;
        
        var i = 0;
        while (matcher.find(i)) {
            var start = matcher.start();
            var end = matcher.end();
            
            // check for escaped %
            var matchedStr = formatString.substring(start, end);
            if ("%%".equals(matchedStr)) {
                sb.append('%');
            } else {
                // check for invalid format, only %s is supported
                var argType = matcher.group(2);
                if (!"s".equals(argType)) {
                    throw new IllegalStateException("Unsupported placeholder format: '" + matchedStr + "'");
                }
                
                // retrieve argument index
                var argIdxStr = matcher.group(1);
                var argIdx = argIdxStr == null ? nextArgIdx++ : Integer.parseInt(argIdxStr) - 1;
                
                // validate argument index
                if (argIdx < 0)
                    throw new IllegalStateException("Invalid argument index: " + argIdx);
                
                // append the text before the argument
                sb.append(formatString, i, start);
                // add text component
                components.add(componentCreator.apply(sb.toString()));
                // add argument component
                components.add(args.size() <= argIdx ? componentCreator.apply("") : localize(lang, args.get(argIdx).asComponent()));
                // clear string builder
                sb.setLength(0);
            }
            
            // start next search after matcher end index
            i = end;
        }
        
        // append the text after the last argument
        if (i < formatString.length()) {
            sb.append(formatString, i, formatString.length());
            components.add(componentCreator.apply(sb.toString()));
        }
        
        return components;
    }
    
}
