package xyz.xenondevs.invui.i18n;

import com.google.gson.stream.JsonReader;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.internal.util.ComponentLocalizer;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

/**
 * Handles localization of items and window titles.
 */
public class Languages {
    
    private static final Languages INSTANCE = new Languages();
    private final Map<Locale, Map<String, String>> translations = new HashMap<>();
    private Function<Player, Locale> localeProvider = Player::locale;
    private boolean serverSideTranslations = true;
    
    private Languages() {
    }
    
    public static Languages getInstance() {
        return INSTANCE;
    }
    
    /**
     * Adds a language under the given lang code.
     * <p>
     * This method will replace any existing language with the same lang code.
     *
     * @param locale         The lang code of the language.
     * @param translations The translations of the language.
     */
    public void addLanguage(Locale locale, Map<String, String> translations) {
        this.translations.put(locale, translations);
    }
    
    /**
     * Adds a language under the given lang code after reading it from the given reader.
     * <p>
     * Note: The language is read as a json object with the translation keys as keys and the format strings as
     * their string values. Any other json structure will result in an {@link IllegalStateException}.
     * An example for such a structure are Minecraft's lang files.
     *
     * @param locale       The locale.
     * @param reader The reader for a language json file.
     * @throws IOException           If an error occurs while reading.
     * @throws IllegalStateException If the json is not valid.
     */
    public void loadLanguage(Locale locale, Reader reader) throws IOException {
        var translations = new HashMap<String, String>();
        try (var jsonReader = new JsonReader(reader)) {
            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                var key = jsonReader.nextName();
                var value = jsonReader.nextString();
                translations.put(key, value);
            }
            
            addLanguage(locale, translations);
        }
    }
    
    /**
     * Adds a language under the given lang code after reading it from the given file.
     *
     * @param locale    The locale.
     * @param file    The file to read the language from.
     * @param charset The charset to use.
     * @throws IOException If an error occurs while reading.
     */
    public void loadLanguage(Locale locale, File file, Charset charset) throws IOException {
        try (var reader = new FileReader(file, charset)) {
            loadLanguage(locale, reader);
        }
    }
    
    /**
     * Retrieves the format string for the given key under the given language.
     *
     * @param locale The locale.
     * @param key  The key of the format string.
     * @return The format string or null if there is no such language or key.
     */
    public @Nullable String getFormatString(Locale locale, String key) {
        var map = translations.get(locale);
        if (map == null)
            return null;
        return map.get(key);
    }
    
    /**
     * Sets the way the language is determined for a player.
     * By default, the language is determined using {@link Player#locale()}.
     *
     * @param localeProvider The language provider.
     */
    public void setLocaleProvider(Function<Player, Locale> localeProvider) {
        this.localeProvider = localeProvider;
    }
    
    /**
     * Gets the language for the given player by invoking the configured language provider.
     *
     * @param player The player to get the language for.
     * @return The language of the player.
     */
    public Locale getLocale(Player player) {
        return localeProvider.apply(player);
    }
    
    /**
     * Enables or disables server-side translations for components.
     *
     * @param enable Whether server-side translations should be enabled.
     */
    public void enableServerSideTranslations(boolean enable) {
        serverSideTranslations = enable;
    }
    
    /**
     * Checks whether server-side translations are enabled.
     *
     * @return Whether server-side translations are enabled.
     */
    public boolean doesServerSideTranslations() {
        return serverSideTranslations;
    }
    
    /**
     * Translates the given component into the language of the given player, if server-side translations are enabled.
     *
     * @param player    The player to translate the component for.
     * @param component The component to translate.
     * @return The translated component or the original component if server-side translations are disabled.
     */
    public Component localized(Player player, Component component) {
        return localized(getLocale(player), component);
    }
    
    /**
     * Translates the given component into the given language, if server-side translations are enabled.
     *
     * @param locale   The language to translate the component to.
     * @param component The component to translate.
     * @return The translated component or the original component if server-side translations are disabled.
     */
    public Component localized(Locale locale, Component component) {
        if (serverSideTranslations) {
            return ComponentLocalizer.getInstance().localize(locale, component);
        }
        
        return component;
    }
    
    /**
     * Configures how components are created from translation strings.
     *
     * @param componentCreator The function that creates components from translation strings.
     */
    public void setComponentCreator(Function<String, Component> componentCreator) {
        ComponentLocalizer.getInstance().setComponentCreator(componentCreator);
    }
    
}
