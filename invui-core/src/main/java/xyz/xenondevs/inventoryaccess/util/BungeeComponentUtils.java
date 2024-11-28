package xyz.xenondevs.inventoryaccess.util;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.Arrays;

public class BungeeComponentUtils {
    
    private static final BaseComponent FORMATTING_TEMPLATE = new ComponentBuilder("")
        .italic(false)
        .bold(false)
        .strikethrough(false)
        .underlined(false)
        .obfuscated(false)
        .color(ChatColor.WHITE)
        .create()[0];
    
    public static BaseComponent[] withoutPreFormatting(String text) {
        return withoutPreFormatting(TextComponent.fromLegacyText(text));
    }
    
    public static BaseComponent[] withoutPreFormatting(BaseComponent... components) {
        BaseComponent[] duplicate = duplicate(components);
        
        BaseComponent previousComponent = FORMATTING_TEMPLATE;
        for (BaseComponent component : duplicate) {
            component.copyFormatting(previousComponent, false);
            previousComponent = component;
        }
        
        return duplicate;
    }
    
    public static BaseComponent[] duplicate(BaseComponent... components) {
        return Arrays.stream(components).map(BaseComponent::duplicate).toArray(BaseComponent[]::new);
    }
    
}
