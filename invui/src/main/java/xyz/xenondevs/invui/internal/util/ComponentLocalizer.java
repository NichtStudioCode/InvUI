package xyz.xenondevs.invui.internal.util;

import net.kyori.adventure.text.*;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import xyz.xenondevs.invui.i18n.Languages;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Pattern;

public class ComponentLocalizer {
    
    private static final Pattern FORMAT_PATTERN = Pattern.compile("%(?:(\\d+)\\$)?([A-Za-z%]|$)");
    private static final ComponentLocalizer INSTANCE = new ComponentLocalizer();
    
    private BiFunction<String, TagResolver[], Component> componentCreator = MiniMessage.miniMessage()::deserialize;
    
    private ComponentLocalizer() {
    }
    
    public static ComponentLocalizer getInstance() {
        return INSTANCE;
    }
    
    public void setComponentCreator(BiFunction<String, TagResolver[], Component> componentCreator) {
        this.componentCreator = componentCreator;
    }
    
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Component localize(Locale locale, Component component, TagResolver[] resolvers) {
        if (!(component instanceof BuildableComponent))
            throw new IllegalStateException("Component is not a BuildableComponent");
        
        return localize(locale, (BuildableComponent) component, resolvers);
    }
    
    @SuppressWarnings("NonExtendableApiUsage")
    private <C extends BuildableComponent<C, B>, B extends ComponentBuilder<C, B>> BuildableComponent<?, ?> localize(Locale locale, BuildableComponent<C, B> component, TagResolver[] resolvers) {
        ComponentBuilder<?, ?> builder;
        if (component instanceof TranslatableComponent) {
            builder = localizeTranslatable(locale, (TranslatableComponent) component, resolvers).toBuilder();
        } else {
            builder = component.toBuilder();
        }
        
        builder.mapChildrenDeep(child -> {
            if (child instanceof TranslatableComponent)
                return localizeTranslatable(locale, (TranslatableComponent) child, resolvers);
            return child;
        });
        
        return builder.build();
    }
    
    private BuildableComponent<?, ?> localizeTranslatable(Locale locale, TranslatableComponent component, TagResolver[] resolvers) {
        var formatString = Languages.getInstance().getFormatString(locale, component.key());
        if (formatString == null)
            return component;
        
        var children = decomposeFormatString(locale, formatString, component.arguments(), resolvers);
        return Component.textOfChildren(children.toArray(ComponentLike[]::new)).style(component.style());
    }
    
    private List<Component> decomposeFormatString(Locale locale, String formatString, List<TranslationArgument> args, TagResolver[] resolvers) {
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
                components.add(componentCreator.apply(sb.toString(), resolvers));
                // add argument component
                components.add(args.size() <= argIdx ? componentCreator.apply("", resolvers) : localize(locale, args.get(argIdx).asComponent(), resolvers));
                // clear string builder
                sb.setLength(0);
            }
            
            // start next search after matcher end index
            i = end;
        }
        
        // append the text after the last argument
        if (i < formatString.length()) {
            sb.append(formatString, i, formatString.length());
            components.add(componentCreator.apply(sb.toString(), resolvers));
        }
        
        return components;
    }
    
}
