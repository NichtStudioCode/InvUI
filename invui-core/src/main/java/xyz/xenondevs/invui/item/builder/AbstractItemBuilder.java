package xyz.xenondevs.invui.item.builder;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
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
import xyz.xenondevs.inventoryaccess.component.BungeeComponentWrapper;
import xyz.xenondevs.inventoryaccess.component.ComponentWrapper;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Abstract base class for item builders.
 *
 * @param <S> Self reference, used for chaining.
 */
@SuppressWarnings("unchecked")
public abstract class AbstractItemBuilder<S> implements ItemProvider {
    
    /**
     * The {@link ItemStack} to use as a base.
     */
    protected ItemStack base;
    /**
     * The {@link Material} of the {@link ItemStack}.
     */
    protected Material material;
    /**
     * The amount of the {@link ItemStack}.
     */
    protected int amount = 1;
    /**
     * The damage value of the {@link ItemStack}
     */
    protected int damage;
    /**
     * The custom model data value of the {@link ItemStack}.
     */
    protected int customModelData;
    /**
     * The unbreakable state of the {@link ItemStack}.
     */
    protected Boolean unbreakable;
    /**
     * The display name of the {@link ItemStack}.
     */
    protected ComponentWrapper displayName;
    /**
     * The lore of the {@link ItemStack}.
     */
    protected List<ComponentWrapper> lore;
    /**
     * The selected {@link ItemFlag ItemFlags} of the {@link ItemStack}.
     */
    protected List<ItemFlag> itemFlags;
    /**
     * The enchantments of the {@link ItemStack}.
     */
    protected HashMap<Enchantment, Pair<Integer, Boolean>> enchantments;
    /**
     * Additional modifier functions to be run after building the {@link ItemStack}.
     */
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
            
            // unbreakable
            if (unbreakable != null)
                itemMeta.setUnbreakable(unbreakable);
            
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
    
    /**
     * Removes a lore line at the given index.
     *
     * @param index The index of the lore line to remove
     * @return The builder instance
     */
    @Contract("_ -> this")
    public @NotNull S removeLoreLine(int index) {
        if (lore != null) lore.remove(index);
        return (S) this;
    }
    
    /**
     * Clears the lore.
     *
     * @return The builder instance
     */
    @Contract("-> this")
    public @NotNull S clearLore() {
        if (lore != null) lore.clear();
        return (S) this;
    }
    
    /**
     * Gets the base {@link ItemStack} of this builder.
     *
     * @return The base {@link ItemStack}
     */
    public @Nullable ItemStack getBase() {
        return base;
    }
    
    /**
     * Gets the {@link Material} of this builder.
     *
     * @return The {@link Material}
     */
    public @Nullable Material getMaterial() {
        return material;
    }
    
    /**
     * Sets the {@link Material} of this builder.
     *
     * @param material The {@link Material}
     * @return The builder instance
     */
    @Contract("_ -> this")
    public @NotNull S setMaterial(@NotNull Material material) {
        this.material = material;
        return (S) this;
    }
    
    /**
     * Gets the amount.
     *
     * @return The amount
     */
    public int getAmount() {
        return amount;
    }
    
    /**
     * Sets the amount.
     *
     * @param amount The amount
     * @return The builder instance
     */
    @Contract("_ -> this")
    public @NotNull S setAmount(int amount) {
        this.amount = amount;
        return (S) this;
    }
    
    /**
     * Gets the damage value.
     *
     * @return The damage value
     */
    public int getDamage() {
        return damage;
    }
    
    /**
     * Sets the damage value.
     *
     * @param damage The damage value
     * @return The builder instance
     */
    @Contract("_ -> this")
    public @NotNull S setDamage(int damage) {
        this.damage = damage;
        return (S) this;
    }
    
    /**
     * Gets the custom model data value.
     *
     * @return The custom model data value
     */
    public int getCustomModelData() {
        return customModelData;
    }
    
    /**
     * Sets the custom model data value.
     *
     * @param customModelData The custom model data value
     * @return The builder instance
     */
    @Contract("_ -> this")
    public @NotNull S setCustomModelData(int customModelData) {
        this.customModelData = customModelData;
        return (S) this;
    }
    
    /**
     * Gets the unbreakable state, null for default.
     *
     * @return The unbreakable state
     */
    public @Nullable Boolean isUnbreakable() {
        return unbreakable;
    }
    
    /**
     * Sets the unbreakable state.
     *
     * @param unbreakable The unbreakable state
     * @return The builder instance
     */
    @Contract("_ -> this")
    public @NotNull S setUnbreakable(boolean unbreakable) {
        this.unbreakable = unbreakable;
        return (S) this;
    }
    
    /**
     * Gets the display name.
     *
     * @return The display name
     */
    public @Nullable ComponentWrapper getDisplayName() {
        return displayName;
    }
    
    /**
     * Sets the display name.
     *
     * @param displayName The display name
     * @return The builder instance
     */
    @Contract("_ -> this")
    public @NotNull S setDisplayName(String displayName) {
        this.displayName = new BungeeComponentWrapper(TextComponent.fromLegacyText(displayName)).withoutPreFormatting();
        return (S) this;
    }
    
    /**
     * Sets the display name.
     *
     * @param displayName The display name
     * @return The builder instance
     */
    @Contract("_ -> this")
    public @NotNull S setDisplayName(BaseComponent... displayName) {
        this.displayName = new BungeeComponentWrapper(displayName).withoutPreFormatting();
        return (S) this;
    }
    
    /**
     * Sets the display name.
     *
     * @param component The display name
     * @return The builder instance
     */
    @Contract("_ -> this")
    public @NotNull S setDisplayName(ComponentWrapper component) {
        this.displayName = component.withoutPreFormatting();
        return (S) this;
    }
    
    //<editor-fold desc="lore">
    
    /**
     * Gets the lore.
     *
     * @return The lore
     */
    public @Nullable List<ComponentWrapper> getLore() {
        return lore;
    }
    
    /**
     * Sets the lore.
     *
     * @param lore The lore
     * @return The builder instance
     */
    @Contract("_ -> this")
    public @NotNull S setLore(@NotNull List<@NotNull ComponentWrapper> lore) {
        this.lore = lore.stream()
            .map(ComponentWrapper::withoutPreFormatting)
            .collect(Collectors.toList());
        return (S) this;
    }
    
    /**
     * Sets the lore using the legacy text format.
     *
     * @param lore The lore
     * @return The builder instance
     */
    @Contract("_ -> this")
    public @NotNull S setLegacyLore(@NotNull List<@NotNull String> lore) {
        this.lore = lore.stream()
            .map(line -> new BungeeComponentWrapper(TextComponent.fromLegacyText(line)).withoutPreFormatting())
            .collect(Collectors.toList());
        return (S) this;
    }
    
    /**
     * Adds lore lindes using the legacy text format.
     *
     * @param lines The lore lines
     * @return The builder instance
     */
    @Contract("_ -> this")
    public @NotNull S addLoreLines(@NotNull String... lines) {
        if (lore == null) lore = new ArrayList<>();
        
        for (String line : lines)
            lore.add(new BungeeComponentWrapper(TextComponent.fromLegacyText(line)).withoutPreFormatting());
        
        return (S) this;
    }
    
    /**
     * Adds lore lines.
     *
     * @param lines The lore lines, where each {@link BaseComponent} array represents a line.
     * @return The builder instance
     */
    @Contract("_ -> this")
    public @NotNull S addLoreLines(@NotNull BaseComponent[]... lines) {
        if (lore == null) lore = new ArrayList<>();
        
        for (BaseComponent[] line : lines)
            lore.add(new BungeeComponentWrapper(line).withoutPreFormatting());
        
        return (S) this;
    }
    
    /**
     * Adds lore lines.
     *
     * @param lines The lore lines
     * @return The builder instance
     */
    @Contract("_ -> this")
    public @NotNull S addLoreLines(@NotNull ComponentWrapper... lines) {
        if (lore == null) lore = new ArrayList<>();
        
        for (ComponentWrapper line : lines)
            lore.add(line.withoutPreFormatting());
        
        return (S) this;
    }
    
    /**
     * Adds lore lines.
     *
     * @param lines The lore lines
     * @return The builder instance
     */
    @Contract("_ -> this")
    public @NotNull S addLoreLines(@NotNull List<@NotNull ComponentWrapper> lines) {
        if (lore == null) lore = new ArrayList<>();
        
        for (ComponentWrapper line : lines)
            lore.add(line.withoutPreFormatting());
        
        return (S) this;
    }
    
    /**
     * Adds lore lines using the legacy text format.
     *
     * @param lines The lore lines
     * @return The builder instance
     */
    @Contract("_ -> this")
    public @NotNull S addLegacyLoreLines(@NotNull List<@NotNull String> lines) {
        if (lore == null) lore = new ArrayList<>();
        
        for (String line : lines)
            lore.add(new BungeeComponentWrapper(TextComponent.fromLegacyText(line)).withoutPreFormatting());
        
        return (S) this;
    }
    //</editor-fold>
    
    //<editor-fold desc="item flags">
    
    /**
     * Gets the configured {@link ItemFlag ItemFlags}.
     *
     * @return The {@link ItemFlag ItemFlags}
     */
    public @Nullable List<ItemFlag> getItemFlags() {
        return itemFlags;
    }
    
    /**
     * Sets the {@link ItemFlag ItemFlags}.
     *
     * @param itemFlags The {@link ItemFlag ItemFlags}
     * @return The builder instance
     */
    @Contract("_ -> this")
    public @NotNull S setItemFlags(@NotNull List<ItemFlag> itemFlags) {
        this.itemFlags = itemFlags;
        return (S) this;
    }
    
    /**
     * Adds {@link ItemFlag ItemFlags}.
     *
     * @param itemFlags The {@link ItemFlag ItemFlags}
     * @return The builder instance
     */
    @Contract("_ -> this")
    public @NotNull S addItemFlags(@NotNull ItemFlag... itemFlags) {
        if (this.itemFlags == null) this.itemFlags = new ArrayList<>();
        this.itemFlags.addAll(Arrays.asList(itemFlags));
        return (S) this;
    }
    
    /**
     * Adds all existing {@link ItemFlag ItemFlags}.
     *
     * @return The builder instance
     */
    @Contract("-> this")
    public @NotNull S addAllItemFlags() {
        this.itemFlags = new ArrayList<>(Arrays.asList(ItemFlag.values()));
        return (S) this;
    }
    
    /**
     * Removes the specified {@link ItemFlag ItemFlags}.
     *
     * @param itemFlags The {@link ItemFlag ItemFlags} to remove
     * @return The builder instance
     */
    @Contract("_ -> this")
    public @NotNull S removeItemFlags(@NotNull ItemFlag... itemFlags) {
        if (this.itemFlags != null)
            this.itemFlags.removeAll(Arrays.asList(itemFlags));
        return (S) this;
    }
    
    /**
     * Removes all {@link ItemFlag ItemFlags}.
     *
     * @return The builder instance
     */
    @Contract("-> this")
    public @NotNull S clearItemFlags() {
        if (itemFlags != null) itemFlags.clear();
        return (S) this;
    }
    //</editor-fold>
    
    //<editor-fold desc="enchantments">
    
    /**
     * Gets the enchantments.
     *
     * @return The enchantments
     */
    public @Nullable HashMap<Enchantment, Pair<Integer, Boolean>> getEnchantments() {
        return enchantments;
    }
    
    /**
     * Sets the enchantments.
     *
     * @param enchantments The enchantments
     * @return The builder instance
     */
    @Contract("_ -> this")
    public @NotNull S setEnchantments(@NotNull HashMap<Enchantment, Pair<Integer, Boolean>> enchantments) {
        this.enchantments = enchantments;
        return (S) this;
    }
    
    /**
     * Adds an enchantment.
     *
     * @param enchantment            The enchantment
     * @param level                  The level
     * @param ignoreLevelRestriction Whether to ignore the level restriction
     * @return The builder instance
     */
    @Contract("_, _, _ -> this")
    public @NotNull S addEnchantment(Enchantment enchantment, int level, boolean ignoreLevelRestriction) {
        if (enchantments == null) enchantments = new HashMap<>();
        enchantments.put(enchantment, new Pair<>(level, ignoreLevelRestriction));
        return (S) this;
    }
    
    /**
     * Adds an enchantment.
     *
     * @param enchantment The enchantment
     * @return The builder instance
     */
    @Contract("_ -> this")
    public @NotNull S removeEnchantment(Enchantment enchantment) {
        if (enchantments != null) enchantments.remove(enchantment);
        return (S) this;
    }
    
    /**
     * Removes all enchantments.
     *
     * @return The builder instance
     */
    @Contract("-> this")
    public @NotNull S clearEnchantments() {
        if (enchantments != null) enchantments.clear();
        return (S) this;
    }
    //</editor-fold>
    
    //<editor-fold desc="modifiers">
    
    /**
     * Gets the configured modifier functions.
     *
     * @return The modifier functions
     */
    public @Nullable List<Function<ItemStack, ItemStack>> getModifiers() {
        return modifiers;
    }
    
    /**
     * Adds a modifier function, which will be run after building the {@link ItemStack}.
     *
     * @param modifier The modifier function
     * @return The builder instance
     */
    @Contract("_ -> this")
    public @NotNull S addModifier(Function<ItemStack, ItemStack> modifier) {
        if (modifiers == null) modifiers = new ArrayList<>();
        modifiers.add(modifier);
        return (S) this;
    }
    
    /**
     * Removes all modifier functions.
     *
     * @return The builder instance
     */
    @Contract("-> this")
    public @NotNull S clearModifiers() {
        if (modifiers != null) modifiers.clear();
        return (S) this;
    }
    //</editor-fold>
    
    /**
     * Clones this builder.
     *
     * @return The cloned builder
     */
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
