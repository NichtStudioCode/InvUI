package xyz.xenondevs.invui.item.builder;

import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.inventoryaccess.InventoryAccess;
import xyz.xenondevs.inventoryaccess.component.BaseComponentWrapper;
import xyz.xenondevs.inventoryaccess.component.ComponentWrapper;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.util.ComponentUtils;
import xyz.xenondevs.invui.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
public abstract class AbstractItemBuilder<S> implements ItemProvider {
    
    protected ItemStack base;
    protected Material material;
    protected int amount = 1;
    protected int damage;
    protected int customModelData;
    protected ComponentWrapper displayName;
    protected List<ComponentWrapper> lore;
    protected List<ItemFlag> itemFlags;
    protected HashMap<Enchantment, Pair<Integer, Boolean>> enchantments;
    protected List<Function<ItemStack, ItemStack>> modifiers;
    
    /**
     * Constructs a new {@link AbstractItemBuilder} based on the given {@link Material}.
     *
     * @param material The {@link Material}
     */
    public AbstractItemBuilder(@NotNull Material material) {
        this.material = material;
    }
    
    /**
     * Constructs a new {@link AbstractItemBuilder} based on the given {@link Material} and amount.
     *
     * @param material The {@link Material}
     * @param amount   The amount
     */
    public AbstractItemBuilder(@NotNull Material material, int amount) {
        this.material = material;
        this.amount = amount;
    }
    
    /**
     * Constructs a new {@link AbstractItemBuilder} based on the give {@link ItemStack}.
     * This will keep the {@link ItemStack} and uses it's {@link ItemMeta}
     *
     * @param base The {@link ItemStack to use as a base}
     */
    public AbstractItemBuilder(@NotNull ItemStack base) {
        this.base = base.clone();
        this.amount = base.getAmount();
    }
    
    /**
     * Builds the {@link ItemStack}
     *
     * @return The {@link ItemStack}
     */
    @Contract(value = "_ -> new", pure = true)
    @Override
    public @NotNull ItemStack get(@Nullable String lang) {
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
            if (displayName != null) {
                InventoryAccess.getItemUtils().setDisplayName(
                    itemMeta,
                    (lang != null) ? displayName.localized(lang) : displayName
                );
            }
            
            // lore
            if (lore != null) {
                if (lang != null) {
                    var translatedLore = lore.stream()
                        .map(wrapper -> wrapper.localized(lang))
                        .collect(Collectors.toList());
                    
                    InventoryAccess.getItemUtils().setLore(itemMeta, translatedLore);
                } else {
                    InventoryAccess.getItemUtils().setLore(itemMeta, lore);
                }
            }
            
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
    
    @Contract("_ -> this")
    public @NotNull S removeLoreLine(int index) {
        if (lore != null) lore.remove(index);
        return (S) this;
    }
    
    @Contract("-> this")
    public @NotNull S clearLore() {
        if (lore != null) lore.clear();
        return (S) this;
    }
    
    public @Nullable ItemStack getBase() {
        return base;
    }
    
    public @Nullable Material getMaterial() {
        return material;
    }
    
    @Contract("_ -> this")
    public @NotNull S setMaterial(@NotNull Material material) {
        this.material = material;
        return (S) this;
    }
    
    public int getAmount() {
        return amount;
    }
    
    @Contract("_ -> this")
    public @NotNull S setAmount(int amount) {
        this.amount = amount;
        return (S) this;
    }
    
    public int getDamage() {
        return damage;
    }
    
    @Contract("_ -> this")
    public @NotNull S setDamage(int damage) {
        this.damage = damage;
        return (S) this;
    }
    
    public int getCustomModelData() {
        return customModelData;
    }
    
    @Contract("_ -> this")
    public @NotNull S setCustomModelData(int customModelData) {
        this.customModelData = customModelData;
        return (S) this;
    }
    
    public @Nullable ComponentWrapper getDisplayName() {
        return displayName;
    }
    
    @Contract("_ -> this")
    public @NotNull S setDisplayName(String displayName) {
        this.displayName = new BaseComponentWrapper(ComponentUtils.withoutPreFormatting(displayName));
        return (S) this;
    }
    
    @Contract("_ -> this")
    public @NotNull S setDisplayName(BaseComponent... displayName) {
        this.displayName = new BaseComponentWrapper(ComponentUtils.withoutPreFormatting(displayName));
        return (S) this;
    }
    
    @Contract("_ -> this")
    public @NotNull S setDisplayName(ComponentWrapper component) {
        this.displayName = component;
        return (S) this;
    }
    
    //<editor-fold desc="lore">
    public @Nullable List<ComponentWrapper> getLore() {
        return lore;
    }
    
    @Contract("_ -> this")
    public @NotNull S setLore(List<ComponentWrapper> lore) {
        this.lore = lore;
        return (S) this;
    }
    
    @Contract("_ -> this")
    public @NotNull S setLegacyLore(@NotNull List<String> lore) {
        this.lore = lore.stream()
            .map(line -> new BaseComponentWrapper(ComponentUtils.withoutPreFormatting(line)))
            .collect(Collectors.toList());
        return (S) this;
    }
    
    @Contract("_ -> this")
    public @NotNull S addLoreLines(@NotNull String... lines) {
        if (lore == null) lore = new ArrayList<>();
        
        for (String line : lines)
            lore.add(new BaseComponentWrapper(ComponentUtils.withoutPreFormatting(line)));
        return (S) this;
    }
    
    @Contract("_ -> this")
    public @NotNull S addLoreLines(@NotNull BaseComponent[]... lines) {
        if (lore == null) lore = new ArrayList<>();
        
        lore.addAll(
            Arrays.stream(lines)
                .map(line -> new BaseComponentWrapper(ComponentUtils.withoutPreFormatting(line)))
                .collect(Collectors.toList())
        );
        
        return (S) this;
    }
    
    @Contract("_ -> this")
    public @NotNull S addLoreLines(@NotNull ComponentWrapper... lines) {
        if (lore == null) lore = new ArrayList<>();
        
        lore.addAll(Arrays.asList(lines));
        
        return (S) this;
    }
    //</editor-fold>
    
    //<editor-fold desc="item flags">
    public @Nullable List<ItemFlag> getItemFlags() {
        return itemFlags;
    }
    
    @Contract("_ -> this")
    public @NotNull S setItemFlags(@NotNull List<ItemFlag> itemFlags) {
        this.itemFlags = itemFlags;
        return (S) this;
    }
    
    @Contract("_ -> this")
    public @NotNull S addItemFlags(@NotNull ItemFlag... itemFlags) {
        if (this.itemFlags == null) this.itemFlags = new ArrayList<>();
        this.itemFlags.addAll(Arrays.asList(itemFlags));
        return (S) this;
    }
    
    @Contract("_ -> this")
    public @NotNull S removeItemFlags(@NotNull ItemFlag... itemFlags) {
        if (this.itemFlags != null)
            this.itemFlags.removeAll(Arrays.asList(itemFlags));
        return (S) this;
    }
    
    @Contract("-> this")
    public @NotNull S clearItemFlags() {
        if (itemFlags != null) itemFlags.clear();
        return (S) this;
    }
    //</editor-fold>
    
    //<editor-fold desc="enchantments">
    public @Nullable HashMap<Enchantment, Pair<Integer, Boolean>> getEnchantments() {
        return enchantments;
    }
    
    @Contract("_ -> this")
    public @NotNull S setEnchantments(@NotNull HashMap<Enchantment, Pair<Integer, Boolean>> enchantments) {
        this.enchantments = enchantments;
        return (S) this;
    }
    
    @Contract("_, _, _ -> this")
    public @NotNull S addEnchantment(Enchantment enchantment, int level, boolean ignoreLevelRestriction) {
        if (enchantments == null) enchantments = new HashMap<>();
        enchantments.put(enchantment, new Pair<>(level, ignoreLevelRestriction));
        return (S) this;
    }
    
    @Contract("_ -> this")
    public @NotNull S removeEnchantment(Enchantment enchantment) {
        if (enchantments == null) enchantments = new HashMap<>();
        enchantments.remove(enchantment);
        return (S) this;
    }
    
    @Contract("-> this")
    public @NotNull S clearEnchantments() {
        if (enchantments != null) enchantments.clear();
        return (S) this;
    }
    //</editor-fold>
    
    //<editor-fold desc="modifiers">
    public @Nullable List<Function<ItemStack, ItemStack>> getModifiers() {
        return modifiers;
    }
    
    @Contract("_ -> this")
    public @NotNull S addModifier(Function<ItemStack, ItemStack> modifier) {
        if (modifiers == null) modifiers = new ArrayList<>();
        modifiers.add(modifier);
        return (S) this;
    }
    
    @Contract("-> this")
    public @NotNull S clearModifiers() {
        if (modifiers != null) modifiers.clear();
        return (S) this;
    }
    //</editor-fold>
    
    @SuppressWarnings("unchecked")
    @Contract(value = "-> new", pure = true)
    @Override
    public @NotNull S clone() {
        try {
            AbstractItemBuilder<S> clone = ((AbstractItemBuilder<S>) super.clone());
            if (base != null) clone.base = base.clone();
            if (lore != null) clone.lore = new ArrayList<>(lore);
            if (itemFlags != null) clone.itemFlags = new ArrayList<>(itemFlags);
            if (enchantments != null) clone.enchantments = new HashMap<>(enchantments);
            if (modifiers != null) clone.modifiers = new ArrayList<>(modifiers);
            
            return (S) clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }
    
}
