package xyz.xenondevs.invui.i18n;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.junit.jupiter.api.Test;

import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LanguagesTest {
    
    @Test
    public void testLocalizeSimple() {
        var l = Languages.getInstance();
        l.addLanguage(Locale.US, Map.of("item.minecraft.stone", "Stone"));
        
        var translatable = Component.translatable("item.minecraft.stone");
        var translated = l.localized(Locale.US, translatable);
        
        assertTextEquals("Stone", translated);
    }
    
    @Test
    public void testLocalizeWithParams() {
        var l = Languages.getInstance();
        l.addLanguage(Locale.US, Map.of("a.b.c", "Sale: <arg:0>% off"));
        
        var translatable = Component.translatable("a.b.c", Component.text("50"));
        var translated = l.localized(Locale.US, translatable);
        
        assertTextEquals("Sale: 50% off", translated);
    }
    
    @Test
    public void testLocalizeWithNestedParams() {
        var l = Languages.getInstance();
        l.addLanguage(
            Locale.US,
            Map.of(
                "a", "Sale: <arg:0>% off",
                "b", "<arg:0><arg:1>",
                "c", "5",
                "d", "0"
            ));
        
        var translatable = Component.translatable("a", Component.translatable("b", Component.translatable("c"), Component.translatable("d")));
        var translated = l.localized(Locale.US, translatable);
        
        assertTextEquals("Sale: 50% off", translated);
    }
    
    @Test
    public void testLocalizedWithPlaceholders() {
        var l = Languages.getInstance();
        l.addLanguage(Locale.US, Map.of("a.b.c", "Sale: <percentage>% off"));
        
        var translatable = Component.translatable("a.b.c");
        var translated = l.localized(Locale.US, translatable, Placeholder.unparsed("percentage", "50"));
        
        assertTextEquals("Sale: 50% off", translated);
    }
    
    private void assertTextEquals(String expected, Component actual) {
        var actualText = PlainTextComponentSerializer.plainText().serialize(actual);
        assertEquals(expected, actualText);
    }
    
}