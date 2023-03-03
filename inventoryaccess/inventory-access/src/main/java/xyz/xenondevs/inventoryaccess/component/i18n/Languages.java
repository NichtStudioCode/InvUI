package xyz.xenondevs.inventoryaccess.component.i18n;

import com.google.gson.stream.JsonReader;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.inventoryaccess.component.ComponentWrapper;

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
    
    public static @NotNull Languages getInstance() {
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
    public void addLanguage(@NotNull String lang, @NotNull Map<@NotNull String, @NotNull String> translations) {
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
    public void loadLanguage(@NotNull String lang, @NotNull Reader reader) throws IOException {
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
    public void loadLanguage(@NotNull String lang, @NotNull File file, @NotNull Charset charset) throws IOException {
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
    public @Nullable String getFormatString(@NotNull String lang, @NotNull String key) {
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
    public void setLanguageProvider(@NotNull Function<@NotNull Player, @NotNull String> languageProvider) {
        this.languageProvider = languageProvider;
    }
    
    /**
     * Gets the language for the given player by invoking the configured language provider.
     *
     * @param player The player to get the language for.
     * @return The language of the player.
     */
    public @NotNull String getLanguage(@NotNull Player player) {
        return languageProvider.apply(player);
    }
    
    /**
     * Enables or disables server-side translations for {@link ComponentWrapper ComponentWrappers}.
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
    
}
