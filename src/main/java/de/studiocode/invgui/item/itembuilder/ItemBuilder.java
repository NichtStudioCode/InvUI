package de.studiocode.invgui.item.itembuilder;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import de.studiocode.invgui.util.MojangApiUtils;
import de.studiocode.invgui.util.Pair;
import de.studiocode.invgui.util.ReflectionUtils;
import de.studiocode.invgui.window.impl.BaseWindow;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;

public class ItemBuilder {
    
    protected Material material;
    protected int amount = 1;
    protected int damage;
    protected int customModelData = -1;
    protected String displayName;
    protected List<String> lore = new ArrayList<>();
    protected List<ItemFlag> itemFlags = new ArrayList<>();
    protected HashMap<Enchantment, Pair<Integer, Boolean>> enchantments = new HashMap<>();
    protected GameProfile gameProfile;
    
    public ItemBuilder(Material material) {
        this.material = material;
    }
    
    public ItemBuilder(@NotNull PlayerHead playerHead) {
        material = Material.PLAYER_HEAD;
        gameProfile = new GameProfile(UUID.randomUUID(), null);
        PropertyMap propertyMap = gameProfile.getProperties();
        propertyMap.put("textures", new Property("textures", playerHead.getTextureValue()));
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
        if (gameProfile != null) ReflectionUtils.setValueOfDeclaredField(itemMeta, "profile", gameProfile);
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
    
    /**
     * Contains the texture value for a player head.
     * 
     * @see ItemBuilder
     */
    public static class PlayerHead {
        
        private final String textureValue;
        
        private PlayerHead(@NotNull String textureValue) {
            this.textureValue = textureValue;
        }
        
        public static PlayerHead of(@NotNull Player player) {
            return of(player.getUniqueId());
        }
        
        public static PlayerHead of(@NotNull String playerName) {
            try {
                return of(MojangApiUtils.getCurrentUUID(playerName));
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            return null;
        }
        
        public static PlayerHead of(@NotNull UUID uuid) {
            try {
                return new PlayerHead(MojangApiUtils.getSkinData(uuid, false)[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            return null;
        }
        
        public static PlayerHead fromTextureValue(@NotNull String textureValue) {
            return new PlayerHead(textureValue);
        }
        
        public String getTextureValue() {
            return textureValue;
        }
        
    }
    
}
