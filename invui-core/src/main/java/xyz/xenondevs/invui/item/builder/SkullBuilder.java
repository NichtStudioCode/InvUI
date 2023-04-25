package xyz.xenondevs.invui.item.builder;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.inventoryaccess.util.ReflectionRegistry;
import xyz.xenondevs.inventoryaccess.util.ReflectionUtils;
import xyz.xenondevs.invui.util.MojangApiUtils;

import java.io.Serializable;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public final class SkullBuilder extends AbstractItemBuilder<SkullBuilder> {
    
    private GameProfile gameProfile;
    
    /**
     * Create a {@link SkullBuilder} of a {@link Player Player's} {@link UUID}.
     *
     * @param uuid The {@link UUID} of the skull owner.
     */
    public SkullBuilder(@NotNull UUID uuid) {
        this(HeadTexture.of(uuid));
    }
    
    /**
     * Create a {@link SkullBuilder} with the {@link Player Player's} username.
     *
     * @param username The username of the skull owner.
     */
    public SkullBuilder(@NotNull String username) {
        this(HeadTexture.of(username));
    }
    
    /**
     * Create a {@link SkullBuilder} with a {@link HeadTexture}.
     *
     * @param headTexture The {@link HeadTexture} to be applied to the skull.
     */
    public SkullBuilder(@NotNull HeadTexture headTexture) {
        super(Material.PLAYER_HEAD);
        setGameProfile(headTexture);
    }
    
    private void setGameProfile(@NotNull HeadTexture texture) {
        gameProfile = new GameProfile(UUID.randomUUID(), null);
        PropertyMap propertyMap = gameProfile.getProperties();
        propertyMap.put("textures", new Property("textures", texture.getTextureValue()));
    }
    
    @Contract(value = "_ -> new", pure = true)
    @Override
    public @NotNull ItemStack get(@Nullable String lang) {
        ItemStack item = super.get(lang);
        ItemMeta meta = item.getItemMeta();
        
        if (gameProfile != null)
            ReflectionUtils.invokeMethod(ReflectionRegistry.CB_CRAFT_META_SKULL_SET_PROFILE_METHOD, meta, gameProfile);
        
        item.setItemMeta(meta);
        
        return item;
    }
    
    @Contract("_ -> this")
    @Override
    public @NotNull SkullBuilder setMaterial(@NotNull Material material) {
        return this;
    }
    
    @Contract(value = "-> new", pure = true)
    @Override
    public @NotNull SkullBuilder clone() {
        return super.clone();
    }
    
    /**
     * Contains the texture value for a player head.
     *
     * @see SkullBuilder
     */
    public static class HeadTexture implements Serializable {
        
        private static final Cache<UUID, String> textureCache = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.DAYS)
            .build();
        
        private static final Cache<String, UUID> uuidCache = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.DAYS)
            .build();
        
        private final String textureValue;
        
        /**
         * Creates a new {@link HeadTexture} from the raw texture value.
         *
         * @param textureValue The texture value of this {@link HeadTexture}
         * @see HeadTexture#of(OfflinePlayer)
         * @see HeadTexture#of(UUID)
         * @see HeadTexture#of(String)
         */
        public HeadTexture(@NotNull String textureValue) {
            this.textureValue = textureValue;
        }
        
        /**
         * Retrieves the {@link HeadTexture} from this {@link OfflinePlayer}
         * Please note that this data might not be pulled from the Mojang API as it might already be cached.
         * Use {@link HeadTexture#invalidateCache()} to invalidate the cache.
         *
         * @param offlinePlayer The skull owner.
         * @return The {@link HeadTexture} of that player.
         * @see HeadTexture#of(UUID)
         */
        public static HeadTexture of(@NotNull OfflinePlayer offlinePlayer) {
            return of(offlinePlayer.getUniqueId());
        }
        
        /**
         * Retrieves the {@link HeadTexture} from the username of the skull owner.
         * This will first retrieve the {@link UUID} of the player from either Bukkit's usercache.json file
         * (if the server is in only mode) or from the Mojang API (if the server is in offline mode).
         * <p>
         * Please note that this data might not be pulled from the Mojang API as it might already be cached.
         * Use {@link HeadTexture#invalidateCache()} to invalidate the cache.
         *
         * @param playerName The username of the player.
         * @return The {@link HeadTexture} of that player.
         * @see HeadTexture#of(UUID)
         */
        @SuppressWarnings("deprecation")
        public static HeadTexture of(@NotNull String playerName) {
            if (Bukkit.getServer().getOnlineMode()) {
                // if the server is in online mode, the Minecraft UUID cache (usercache.json) can be used
                return of(Bukkit.getOfflinePlayer(playerName).getUniqueId());
            } else {
                // the server isn't in online mode - the UUID has to be retrieved from the Mojang API
                try {
                    return of(uuidCache.get(playerName, () -> MojangApiUtils.getCurrentUUID(playerName)));
                } catch (ExecutionException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
        
        /**
         * Retrieves the {@link HeadTexture} from the {@link UUID} of the skull owner.
         * Please note that this data might not be pulled from the Mojang API as it might already be cached.
         * Use {@link HeadTexture#invalidateCache()} to invalidate the cache.
         *
         * @param uuid The {@link UUID} of the skull owner.
         * @return The {@link HeadTexture} of that player.
         */
        public static HeadTexture of(@NotNull UUID uuid) {
            try {
                return new HeadTexture(textureCache.get(uuid, () -> MojangApiUtils.getSkinData(uuid, false)[0]));
            } catch (ExecutionException e) {
                e.printStackTrace();
                return null;
            }
        }
        
        /**
         * Invalidates the uuid and texture value cache.
         * This means that when {@link HeadTexture#of(OfflinePlayer)}, {@link HeadTexture#of(UUID)}
         * and {@link HeadTexture#of(String)} are called, these values will be pulled from the
         * Mojang API again.
         */
        public static void invalidateCache() {
            uuidCache.invalidateAll();
            textureCache.invalidateAll();
        }
        
        /**
         * Gets the stored texture value.
         *
         * @return The stored texture value.
         */
        public @NotNull String getTextureValue() {
            return textureValue;
        }
        
    }
    
}
