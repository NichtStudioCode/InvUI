package xyz.xenondevs.invui.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.util.UUIDTypeAdapter;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.UUID;

@SuppressWarnings("deprecation")
public class MojangApiUtils {
    
    private static final String SKIN_DATA_URL = "https://sessionserver.mojang.com/session/minecraft/profile/%s?unsigned=%s";
    private static final String NAME_AT_TIME_URL = "https://api.mojang.com/users/profiles/minecraft/%s?at=%s";
    
    public static String[] getSkinData(UUID uuid, boolean requestSignature) throws IOException {
        String url = String.format(SKIN_DATA_URL, uuid, !requestSignature);
        Reader reader = new InputStreamReader(new URL(url).openConnection().getInputStream());
        JsonObject jsonObject = new JsonParser().parse(reader).getAsJsonObject();
        
        checkForError(jsonObject);
        if (jsonObject.has("properties")) {
            JsonElement properties = jsonObject.get("properties");
            JsonObject property = properties.getAsJsonArray().get(0).getAsJsonObject();
            String value = property.get("value").getAsString();
            String signature = requestSignature ? property.get("signature").getAsString() : "";
            return new String[] {value, signature};
        }
        
        return null;
    }
    
    public static UUID getCurrentUUID(String name) throws IOException {
        return getUuidAtTime(name, System.currentTimeMillis() / 1000);
    }
    
    public static UUID getUuidAtTime(String name, long timestamp) throws IOException {
        String url = String.format(NAME_AT_TIME_URL, name, timestamp);
        Reader reader = new InputStreamReader(new URL(url).openConnection().getInputStream());
        JsonObject jsonObject = new JsonParser().parse(reader).getAsJsonObject();
        
        checkForError(jsonObject);
        if (jsonObject.has("id")) {
            String id = jsonObject.get("id").getAsString();
            return UUIDTypeAdapter.fromString(id);
        }
        
        return null;
    }
    
    private static void checkForError(JsonObject jsonObject) throws MojangApiException {
        if (jsonObject.has("error") && jsonObject.has("errorMessage")) {
            if (jsonObject.has("errorMessage"))
                throw new MojangApiException(jsonObject.get("errorMessage").getAsString());
            else throw new MojangApiException("");
        }
    }
    
    public static class MojangApiException extends IOException {
        
        public MojangApiException(String message) {
            super(message);
        }
        
    }
    
}
