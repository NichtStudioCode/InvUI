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
import java.util.Map;
import java.util.function.Function;

public class Languages {
    
    private static final Languages INSTANCE = new Languages();
    private final Map<String, Map<String, String>> translations = new HashMap<>();
    private Function<Player, String> languageProvider = Player::getLocale;
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
     * @param lang         The lang code of the language.
     * @param translations The translations of the language.
     */
    public void addLanguage(String lang, Map<String, String> translations) {
        this.translations.put(lang, translations);
    }
    
    /**
     * Adds a language under the given lang code after reading it from the given reader.
     * <p>
     * Note: The language is read as a json object with the translation keys as keys and the format strings as
     * their string values. Any other json structure will result in an {@link IllegalStateException}.
     * An example for such a structure are Minecraft's lang files.
     *
     * @param lang   The lang code of the language.
     * @param reader The reader for a language json file.
     * @throws IOException           If an error occurs while reading.
     * @throws IllegalStateException If the json is not valid.
     */
    public void loadLanguage(String lang, Reader reader) throws IOException {
        var translations = new HashMap<String, String>();
        try (var jsonReader = new JsonReader(reader)) {
            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                var key = jsonReader.nextName();
                var value = jsonReader.nextString();
                translations.put(key, value);
            }
            
            addLanguage(lang, translations);
        }
    }
    
    /**
     * Adds a language under the given lang code after reading it from the given file.
     *
     * @param lang    The lang code of the language.
     * @param file    The file to read the language from.
     * @param charset The charset to use.
     * @throws IOException If an error occurs while reading.
     */
    public void loadLanguage(String lang, File file, Charset charset) throws IOException {
        try (var reader = new FileReader(file, charset)) {
            loadLanguage(lang, reader);
        }
    }
    
    /**
     * Retrieves the format string for the given key under the given language.
     *
     * @param lang The language to use.
     * @param key  The key of the format string.
     * @return The format string or null if there is no such language or key.
     */
    public @Nullable String getFormatString(String lang, String key) {
        var map = translations.get(lang);
        if (map == null)
            return null;
        return map.get(key);
    }
    
    /**
     * Sets the way the language is determined for a player.
     * By default, the language is determined using {@link Player#getLocale()}.
     *
     * @param languageProvider The language provider.
     */
    public void setLanguageProvider(Function<Player, String> languageProvider) {
        this.languageProvider = languageProvider;
    }
    
    /**
     * Gets the language for the given player by invoking the configured language provider.
     *
     * @param player The player to get the language for.
     * @return The language of the player.
     */
    public String getLanguage(Player player) {
        return languageProvider.apply(player);
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
        return localized(getLanguage(player), component);
    }
    
    /**
     * Translates the given component into the given language, if server-side translations are enabled.
     *
     * @param lang      The language to translate the component into.
     * @param component The component to translate.
     * @return The translated component or the original component if server-side translations are disabled.
     */
    public Component localized(String lang, Component component) {
        if (serverSideTranslations) {
            return ComponentLocalizer.getInstance().localize(lang, component);
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
