package de.studiocode.invui.item;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import de.studiocode.inventoryaccess.util.ReflectionRegistry;
import de.studiocode.inventoryaccess.util.ReflectionUtils;
import de.studiocode.inventoryaccess.version.InventoryAccess;
import de.studiocode.invui.util.ComponentUtils;
import de.studiocode.invui.util.MojangApiUtils;
import de.studiocode.invui.util.Pair;
import de.studiocode.invui.window.impl.BaseWindow;
import net.md_5.bungee.api.chat.BaseComponent;
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
import java.util.function.Function;
import java.util.stream.Collectors;

public class ItemBuilder implements ItemProvider {
    
    protected ItemStack base;
    protected Material material;
    protected int amount = 1;
    protected int damage;
    protected int customModelData;
    protected BaseComponent[] displayName;
    protected List<BaseComponent[]> lore;
    protected List<ItemFlag> itemFlags;
    protected HashMap<Enchantment, Pair<Integer, Boolean>> enchantments;
    protected GameProfile gameProfile;
    protected List<Function<ItemStack, ItemStack>> modifiers;
    
    /**
     * Constructs a new {@link ItemBuilder} based on the given {@link Material}.
     *
     * @param material The {@link Material}
     */
    public ItemBuilder(@NotNull Material material) {
        this.material = material;
    }
    
    /**
     * Constructs a new {@link ItemBuilder} based on the given {@link Material} and amount.
     *
     * @param material The {@link Material}
     * @param amount   The amount
     */
    public ItemBuilder(@NotNull Material material, int amount) {
        this.material = material;
        this.amount = amount;
    }
    
    /**
     * Constructs a new {@link ItemBuilder} based on the give {@link ItemStack}.
     * This will keep the {@link ItemStack} and uses it's {@link ItemMeta}
     *
     * @param base The {@link ItemStack to use as a base}
     */
    public ItemBuilder(@NotNull ItemStack base) {
        this.base = base.clone();
        this.amount = base.getAmount();
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
    @Override
    public ItemStack get() {
        ItemStack itemStack;
        if (base != null) {
            itemStack = base;
            itemStack.setAmount(amount);
        } else {
            itemStack = new ItemStack(material, amount);
        }
        
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            // display name
            if (displayName != null)
                InventoryAccess.getItemUtils().setDisplayName(itemMeta, displayName);
            
            // lore
            if (lore != null)
                InventoryAccess.getItemUtils().setLore(itemMeta, lore);
            
            // damage
            if (itemMeta instanceof Damageable)
                ((Damageable) itemMeta).setDamage(damage);
            
            // custom model data
            if (customModelData != 0)
                itemMeta.setCustomModelData(customModelData);
            
            // enchantments
            if (enchantments != null) {
                if (base != null)
                    itemMeta.getEnchants().forEach((enchantment, level) -> itemMeta.removeEnchant(enchantment));
                
                enchantments.forEach((enchantment, pair) -> itemMeta.addEnchant(enchantment, pair.getFirst(), pair.getSecond()));
            }
            
            // item flags
            if (itemFlags != null) {
                if (base != null)
                    itemMeta.removeItemFlags(itemMeta.getItemFlags().toArray(new ItemFlag[0]));
                
                itemMeta.addItemFlags(itemFlags.toArray(new ItemFlag[0]));
            }
            
            // game profile
            if (gameProfile != null)
                ReflectionUtils.setFieldValue(ReflectionRegistry.CB_CRAFT_META_SKULL_PROFILE_FIELD, itemMeta, gameProfile);
            
            // apply to the item stack
            itemStack.setItemMeta(itemMeta);
        }
        
        // run modifiers
        if (modifiers != null) {
            for (Function<ItemStack, ItemStack> modifier : modifiers)
                itemStack = modifier.apply(itemStack);
        }
        
        return itemStack;
    }
    
    /**
     * Builds the {@link ItemStack} for a specific player.
     * This is the method called by {@link BaseWindow} which gives you
     * the option to (for example) create a subclass of {@link ItemBuilder} that automatically
     * translates the item's name into the player's language.
     *
     * @param playerUUID The {@link UUID} of the {@link Player}
     *                   for whom this {@link ItemStack} should be built.
     * @return The {@link ItemStack}
     */
    @Override
    public ItemStack getFor(@NotNull UUID playerUUID) {
        return get();
    }
    
    public ItemBuilder setLegacyLore(@NotNull List<String> lore) {
        this.lore = lore.stream()
            .map(ComponentUtils::withoutPreFormatting)
            .collect(Collectors.toList());
        return this;
    }
    
    public ItemBuilder addLoreLines(@NotNull String... lines) {
        if (lore == null) lore = new ArrayList<>();
        
        for (String line : lines)
            lore.add(ComponentUtils.withoutPreFormatting(line));
        return this;
    }
    
    public ItemBuilder addLoreLines(@NotNull BaseComponent[]... lines) {
        if (lore == null) lore = new ArrayList<>();
        
        lore.addAll(Arrays.stream(lines).map(ComponentUtils::withoutPreFormatting).collect(Collectors.toList()));
        return this;
    }
    
    public ItemBuilder removeLoreLine(int index) {
        if (lore != null) lore.remove(index);
        return this;
    }
    
    public ItemBuilder clearLore() {
        if (lore != null) lore.clear();
        return this;
    }
    
    public ItemBuilder addItemFlags(@NotNull ItemFlag... itemFlags) {
        if (this.itemFlags == null) this.itemFlags = new ArrayList<>();
        this.itemFlags.addAll(Arrays.asList(itemFlags));
        return this;
    }
    
    public ItemBuilder removeItemFlags(@NotNull ItemFlag... itemFlags) {
        if (this.itemFlags != null)
            this.itemFlags.removeAll(Arrays.asList(itemFlags));
        return this;
    }
    
    public ItemBuilder clearItemFlags() {
        if (itemFlags != null) itemFlags.clear();
        return this;
    }
    
    public ItemBuilder addEnchantment(Enchantment enchantment, int level, boolean ignoreLevelRestriction) {
        if (enchantments == null) enchantments = new HashMap<>();
        enchantments.put(enchantment, new Pair<>(level, ignoreLevelRestriction));
        return this;
    }
    
    public ItemBuilder removeEnchantment(Enchantment enchantment) {
        if (enchantments == null) enchantments = new HashMap<>();
        enchantments.remove(enchantment);
        return this;
    }
    
    public ItemBuilder clearEnchantments() {
        if (enchantments != null) enchantments.clear();
        return this;
    }
    
    public ItemBuilder addModifier(Function<ItemStack, ItemStack> modifier) {
        if (modifiers == null) modifiers = new ArrayList<>();
        modifiers.add(modifier);
        return this;
    }
    
    public ItemBuilder clearModifiers() {
        if (modifiers != null) modifiers.clear();
        return this;
    }
    
    public ItemStack getBase() {
        return base;
    }
    
    public Material getMaterial() {
        return material;
    }
    
    public ItemBuilder setMaterial(@NotNull Material material) {
        this.material = material;
        return this;
    }
    
    public int getAmount() {
        return amount;
    }
    
    public ItemBuilder setAmount(int amount) {
        this.amount = amount;
        return this;
    }
    
    public int getDamage() {
        return damage;
    }
    
    public ItemBuilder setDamage(int damage) {
        this.damage = damage;
        return this;
    }
    
    public int getCustomModelData() {
        return customModelData;
    }
    
    public ItemBuilder setCustomModelData(int customModelData) {
        this.customModelData = customModelData;
        return this;
    }
    
    public BaseComponent[] getDisplayName() {
        return displayName;
    }
    
    public ItemBuilder setDisplayName(String displayName) {
        this.displayName = ComponentUtils.withoutPreFormatting(displayName);
        return this;
    }
    
    public ItemBuilder setDisplayName(BaseComponent... displayName) {
        this.displayName = ComponentUtils.withoutPreFormatting(displayName);
        return this;
    }
    
    public List<BaseComponent[]> getLore() {
        return lore;
    }
    
    public ItemBuilder setLore(List<BaseComponent[]> lore) {
        this.lore = lore;
        return this;
    }
    
    public List<ItemFlag> getItemFlags() {
        return itemFlags;
    }
    
    public ItemBuilder setItemFlags(@NotNull List<ItemFlag> itemFlags) {
        this.itemFlags = itemFlags;
        return this;
    }
    
    public HashMap<Enchantment, Pair<Integer, Boolean>> getEnchantments() {
        return enchantments;
    }
    
    public ItemBuilder setEnchantments(@NotNull HashMap<Enchantment, Pair<Integer, Boolean>> enchantments) {
        this.enchantments = enchantments;
        return this;
    }
    
    public GameProfile getGameProfile() {
        return gameProfile;
    }
    
    public List<Function<ItemStack, ItemStack>> getModifiers() {
        return modifiers;
    }
    
    @Override
    public ItemBuilder clone() {
        try {
            ItemBuilder clone = ((ItemBuilder) super.clone());
            if (base != null) clone.base = base.clone();
            if (lore != null) clone.lore = new ArrayList<>(lore);
            if (itemFlags != null) clone.itemFlags = new ArrayList<>(itemFlags);
            if (enchantments != null) clone.enchantments = new HashMap<>(enchantments);
            if (modifiers != null) clone.modifiers = new ArrayList<>(modifiers);
            
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
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
