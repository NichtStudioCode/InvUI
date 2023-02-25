package xyz.xenondevs.inventoryaccess.component.i18n;

import com.google.gson.stream.JsonReader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.inventoryaccess.component.ComponentWrapper;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

public class Languages {
    
    private static final Languages INSTANCE = new Languages();
    private final Map<String, Map<String, String>> translations = new HashMap<>();
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
