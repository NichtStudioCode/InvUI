package de.studiocode.invui.item;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import de.studiocode.invui.util.MojangApiUtils;
import de.studiocode.invui.util.Pair;
import de.studiocode.invui.util.reflection.ReflectionRegistry;
import de.studiocode.invui.util.reflection.ReflectionUtils;
import de.studiocode.invui.window.impl.BaseWindow;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class ItemBuilder implements Cloneable {
    
    protected Material material;
    protected int amount = 1;
    protected int damage;
    protected int customModelData = -1;
    protected String displayName;
    protected List<String> lore = new ArrayList<>();
    protected List<ItemFlag> itemFlags = new ArrayList<>();
    protected HashMap<Enchantment, Pair<Integer, Boolean>> enchantments = new HashMap<>();
    protected GameProfile gameProfile;
    
    /**
     * Constructs a new {@link ItemBuilder} based on the given {@link Material}.
     *
     * @param material The {@link Material}
     */
    public ItemBuilder(Material material) {
        this.material = material;
    }
    
    /**
     * Constructs a new {@link ItemBuilder} of skull with the specified {@link HeadTexture}.
     *
     * @param headTexture The {@link HeadTexture}
     */
    public ItemBuilder(@NotNull HeadTexture headTexture) {
        material = Material.PLAYER_HEAD;
        gameProfile = new GameProfile(UUID.randomUUID(), null);
        PropertyMap propertyMap = gameProfile.getProperties();
        propertyMap.put("textures", new Property("textures", headTexture.getTextureValue()));
    }
    
    /**
     * Builds the {@link ItemStack}
     *
     * @return The {@link ItemStack}
     */
    public ItemStack build() {
        ItemStack itemStack = new ItemStack(material, amount);
        
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta instanceof Damageable) ((Damageable) itemMeta).setDamage(damage);
        if (customModelData != -1) itemMeta.setCustomModelData(customModelData);
        if (displayName != null) itemMeta.setDisplayName(displayName);
        if (gameProfile != null)
            ReflectionUtils.setFieldValue(ReflectionRegistry.CB_CRAFT_META_SKULL_PROFILE_FIELD, itemMeta, gameProfile);
        enchantments.forEach((enchantment, pair) -> itemMeta.addEnchant(enchantment, pair.getFirst(), pair.getSecond()));
        itemMeta.addItemFlags(itemFlags.toArray(new ItemFlag[0]));
        itemMeta.setLore(lore);
        
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
    
    /**
     * Builds the {@link ItemStack} for a specific player.
     * This is the method called by {@link BaseWindow} which gives you
     * the option to (for example) create a subclass of {@link ItemBuilder} that automatically
     * translates the item's name into the player's language.
     *
     * @param playerUUID The {@link UUID} of the {@link Player}
     *                   for who this {@link ItemStack} should be built.
     * @return The {@link ItemStack}
     */
    public ItemStack buildFor(@NotNull UUID playerUUID) {
        return build();
    }
    
    public ItemBuilder setMaterial(Material material) {
        this.material = material;
        return this;
    }
    
    public ItemBuilder setAmount(int amount) {
        this.amount = amount;
        return this;
    }
    
    public ItemBuilder setDamage(int damage) {
        this.damage = damage;
        return this;
    }
    
    public ItemBuilder setCustomModelData(int customModelData) {
        this.customModelData = customModelData;
        return this;
    }
    
    public ItemBuilder setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }
    
    public ItemBuilder setLore(List<String> lore) {
        this.lore = lore;
        return this;
    }
    
    public ItemBuilder addLoreLines(String... lines) {
        lore.addAll(Arrays.asList(lines));
        return this;
    }
    
    public ItemBuilder removeLoreLines(String... lines) {
        lore.removeAll(Arrays.asList(lines));
        return this;
    }
    
    public ItemBuilder removeLoreLine(int index) {
        lore.remove(index);
        return this;
    }
    
    public ItemBuilder clearLore() {
        lore.clear();
        return this;
    }
    
    public ItemBuilder setItemFlags(List<ItemFlag> itemFlags) {
        this.itemFlags = itemFlags;
        return this;
    }
    
    public ItemBuilder addItemFlags(ItemFlag... itemFlags) {
        this.itemFlags.addAll(Arrays.asList(itemFlags));
        return this;
    }
    
    public ItemBuilder removeItemFlags(ItemFlag... itemFlags) {
        this.itemFlags.removeAll(Arrays.asList(itemFlags));
        return this;
    }
    
    public ItemBuilder clearItemFlags() {
        itemFlags.clear();
        return this;
    }
    
    public ItemBuilder setEnchantments(HashMap<Enchantment, Pair<Integer, Boolean>> enchantments) {
        this.enchantments = enchantments;
        return this;
    }
    
    public ItemBuilder addEnchantment(Enchantment enchantment, int level, boolean ignoreLevelRestriction) {
        enchantments.put(enchantment, new Pair<>(level, ignoreLevelRestriction));
        return this;
    }
    
    public ItemBuilder removeEnchantment(Enchantment enchantment) {
        enchantments.remove(enchantment);
        return this;
    }
    
    public ItemBuilder clearEnchantments() {
        enchantments.clear();
        return this;
    }
    
    @Override
    public ItemBuilder clone() {
        try {
            return ((ItemBuilder) super.clone())
                .setLore(new ArrayList<>(lore))
                .setItemFlags(new ArrayList<>(itemFlags))
                .setEnchantments(new HashMap<>(enchantments));
        } catch (CloneNotSupportedException e) {
            throw new Error(e);
        }
    }
    
    /**
     * Contains the texture value for a player head.
     *
     * @see ItemBuilder
     */
    public static class HeadTexture implements Serializable {
        
        private static final Cache<UUID, String> textureCache = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.DAYS)
            .build();
        
        private static final Cache<String, UUID> uuidCache = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.DAYS)
            .build();
        
        private final String textureValue;
        
        public HeadTexture(@NotNull String textureValue) {
            this.textureValue = textureValue;
        }
        
        public static HeadTexture of(@NotNull OfflinePlayer offlinePlayer) {
            return of(offlinePlayer.getUniqueId());
        }
        
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
        
        public static HeadTexture of(@NotNull UUID uuid) {
            try {
                return new HeadTexture(textureCache.get(uuid, () -> MojangApiUtils.getSkinData(uuid, false)[0]));
            } catch (ExecutionException e) {
                e.printStackTrace();
                return null;
            }
        }
        
        public static void invalidateCache() {
            uuidCache.invalidateAll();
            textureCache.invalidateAll();
        }
        
        public String getTextureValue() {
            return textureValue;
        }
        
    }
    
}
