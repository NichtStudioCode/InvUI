package de.studiocode.invui.util;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;

public class ComponentUtils {
    
    public static BaseComponent[] fromLegacyText(String text) {
        return new ComponentBuilder("")
            .italic(false)
            .bold(false)
            .strikethrough(false)
            .underlined(false)
            .obfuscated(false)
            .append(TextComponent.fromLegacyText(text))
            .create();
    }
    
}
