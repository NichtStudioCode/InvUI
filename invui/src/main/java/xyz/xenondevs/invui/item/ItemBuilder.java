package xyz.xenondevs.invui.item;

import io.papermc.paper.datacomponent.DataComponentBuilder;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.Nullable;
import xyz.xenondevs.inventoryaccess.component.i18n.AdventureComponentLocalizer;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.jetbrains.annotations.ApiStatus.Experimental;

/**
 * Utility for building localized {@link ItemStack ItemStacks}.
 */
@SuppressWarnings("UnstableApiUsage")
public class ItemBuilder implements ItemProvider {
    
    private ItemStack itemStack;
    private @Nullable Component name;
    private @Nullable Component customName;
    private @Nullable List<Component> lore;
    private @Nullable List<Function<ItemStack, ItemStack>> modifiers;
    
    private final Map<String, ItemStack> buildCache = new HashMap<>();
    
    /**
     * Constructs a new {@link ItemBuilder} based on the given {@link Material}.
     *
     * @param material The {@link Material}
     */
    public ItemBuilder(Material material) {
        this(new ItemStack(material));
    }
    
    /**
     * Constructs a new {@link ItemBuilder} based on the given {@link Material} and amount.
     *
     * @param material The {@link Material}
     * @param amount   The amount
     */
    public ItemBuilder(Material material, int amount) {
        this(new ItemStack(material, amount));
    }
    
    /**
     * Constructs a new {@link ItemBuilder} based on the give {@link ItemStack}.
     *
     * @param base The {@link ItemStack to use as a base}
     */
    public ItemBuilder(ItemStack base) {
        this.itemStack = base.clone();
        
        if (base.isDataOverridden(DataComponentTypes.ITEM_NAME)) {
            name = base.getData(DataComponentTypes.ITEM_NAME);
        }
        
        if (base.isDataOverridden(DataComponentTypes.CUSTOM_NAME)) {
            customName = base.getData(DataComponentTypes.CUSTOM_NAME);
        }
        
        if (base.isDataOverridden(DataComponentTypes.LORE)) {
            ItemLore lore = base.getData(DataComponentTypes.LORE);
            assert lore != null;
            this.lore = lore.lines();
        }
    }
    
    /**
     * Builds the {@link ItemStack}
     *
     * @return The {@link ItemStack}
     */
    @Override
    public ItemStack get(String lang) {
        return buildCache.computeIfAbsent(lang, lang1 -> {
            ItemStack itemStack = this.itemStack.clone();
            
            if (name != null) {
                itemStack.setData(
                    DataComponentTypes.ITEM_NAME,
                    AdventureComponentLocalizer.getInstance().localize(lang, name)
                );
            }
            
            if (customName != null) {
                itemStack.setData(
                    DataComponentTypes.CUSTOM_NAME,
                    AdventureComponentLocalizer.getInstance().localize(lang, customName)
                );
            }
            
            if (lore != null) {
                ItemLore.Builder lore = ItemLore.lore();
                for (Component line : this.lore) {
                    lore.addLine(AdventureComponentLocalizer.getInstance().localize(lang, line));
                }
                
                itemStack.setData(DataComponentTypes.LORE, lore.build());
            }
            
            if (modifiers != null) {
                for (var modifier : modifiers) {
                    itemStack = modifier.apply(itemStack);
                }
            }
            
            return itemStack;
        });
    }
    
    //<editor-fold desc="base">
    
    /**
     * Sets the {@link Material} of this builder.
     *
     * @param material The {@link Material}
     * @return The builder instance
     */
    public ItemBuilder setMaterial(Material material) {
        itemStack = itemStack.withType(material);
        return this;
    }
    
    /**
     * Sets the amount.
     *
     * @param amount The amount
     * @return The builder instance
     */
    public ItemBuilder setAmount(int amount) {
        itemStack.setAmount(amount);
        return this;
    }
    //</editor-fold>
    
    //<editor-fold desc="name">
    
    /**
     * Sets the name.
     *
     * @param name The name
     * @return The builder instance
     */
    public ItemBuilder setName(Component name) {
        this.name = name;
        return this;
    }
    
    /**
     * Sets the name using mini-message format.
     *
     * @param name The name
     * @return The builder instance
     */
    public ItemBuilder setName(String name) {
        this.name = MiniMessage.miniMessage().deserialize(name);
        return this;
    }
    
    /**
     * Sets the name using legacy text format.
     *
     * @param name The name
     * @return The builder instance
     */
    public ItemBuilder setLegacyName(String name) {
        this.name = LegacyComponentSerializer.legacySection().deserialize(name);
        return this;
    }
    
    /**
     * Gets the custom name.
     *
     * @param customName The custom name
     * @return The builder instance
     */
    public ItemBuilder setCustomName(Component customName) {
        this.customName = customName;
        return this;
    }
    
    /**
     * Sets the custom name using mini-message format.
     *
     * @param customName The custom name
     * @return The builder instance
     */
    public ItemBuilder setCustomName(String customName) {
        this.customName = MiniMessage.miniMessage().deserialize(customName);
        return this;
    }
    
    /**
     * Sets the custom name using legacy text format.
     *
     * @param customName The custom name
     * @return The builder instance
     */
    public ItemBuilder setLegacyCustomName(String customName) {
        this.customName = LegacyComponentSerializer.legacySection().deserialize(customName);
        return this;
    }
    //</editor-fold>
    
    //<editor-fold desc="lore">
    
    /**
     * Removes a lore line at the given index.
     *
     * @param index The index of the lore line to remove
     * @return The builder instance
     */
    public ItemBuilder removeLoreLine(int index) {
        if (lore != null) lore.remove(index);
        return this;
    }
    
    /**
     * Clears the lore.
     *
     * @return The builder instance
     */
    public ItemBuilder clearLore() {
        if (lore != null) lore.clear();
        return this;
    }
    
    /**
     * Sets the lore.
     *
     * @param lore The lore
     * @return The builder instance
     */
    public ItemBuilder setLore(List<Component> lore) {
        this.lore = new ArrayList<>(lore);
        return this;
    }
    
    /**
     * Sets the lore using the legacy text format.
     *
     * @param lore The lore
     * @return The builder instance
     */
    public ItemBuilder setLegacyLore(List<String> lore) {
        this.lore = lore.stream()
            .map(line -> LegacyComponentSerializer.legacySection().deserialize(line))
            .collect(Collectors.toCollection(ArrayList::new));
        return this;
    }
    
    /**
     * Adds lore lindes using the legacy text format.
     *
     * @param lines The lore lines
     * @return The builder instance
     */
    public ItemBuilder addLegacyLoreLines(String... lines) {
        if (lore == null)
            lore = new ArrayList<>();
        
        for (String line : lines) {
            lore.add(LegacyComponentSerializer.legacySection().deserialize(line));
        }
        
        return this;
    }
    
    /**
     * Adds lore lines in mini-message format.
     *
     * @param lines The lore lines
     * @return The builder instance
     */
    public ItemBuilder addLoreLines(String... lines) {
        if (lore == null)
            lore = new ArrayList<>();
        
        for (String line : lines) {
            lore.add(MiniMessage.miniMessage().deserialize(line));
        }
        
        return this;
    }
    
    /**
     * Adds lore lines.
     *
     * @param lines The lore lines
     * @return The builder instance
     */
    public ItemBuilder addLoreLines(Component... lines) {
        if (lore == null)
            lore = new ArrayList<>();
        
        lore.addAll(Arrays.asList(lines));
        
        return this;
    }
    
    /**
     * Adds lore lines.
     *
     * @param lines The lore lines
     * @return The builder instance
     */
    public ItemBuilder addLoreLines(List<Component> lines) {
        if (lore == null)
            lore = new ArrayList<>();
        
        lore.addAll(lines);
        
        return this;
    }
    
    /**
     * Adds lore lines using the legacy text format.
     *
     * @param lines The lore lines
     * @return The builder instance
     */
    public ItemBuilder addLegacyLoreLines(List<String> lines) {
        if (lore == null)
            lore = new ArrayList<>();
        
        for (String line : lines) {
            lore.add(LegacyComponentSerializer.legacySection().deserialize(line));
        }
        
        return this;
    }
    //</editor-fold>
    
    //<editor-fold desc="modifiers">
    
    /**
     * Adds a modifier function, which will be run after building the {@link ItemStack}.
     *
     * @param modifier The modifier function
     * @return The builder instance
     */
    public ItemBuilder addModifier(Function<ItemStack, ItemStack> modifier) {
        if (modifiers == null)
            modifiers = new ArrayList<>();
        modifiers.add(modifier);
        return this;
    }
    
    /**
     * Removes all modifier functions.
     *
     * @return The builder instance
     */
    public ItemBuilder clearModifiers() {
        if (modifiers != null)
            modifiers.clear();
        return this;
    }
    //</editor-fold>
    
    //<editor-fold desc="data component">
    
    /**
     * Proxy method for {@link ItemStack#setData(DataComponentType.Valued, DataComponentBuilder)}
     * <p>
     * Sets the given data component to the specified value.
     *
     * @param type         the data component type
     * @param valueBuilder value builder
     * @param <T>          value type
     */
    @Experimental
    public <T> void set(DataComponentType.Valued<T> type, DataComponentBuilder<T> valueBuilder) {
        itemStack.setData(type, valueBuilder);
    }
    
    /**
     * Proxy method for {@link ItemStack#setData(DataComponentType.Valued, Object)}
     * <p>
     * Sets the given data component to the specified value.
     *
     * @param type  the data component type
     * @param value value to set
     * @param <T>   value type
     */
    @Experimental
    public <T> void set(final DataComponentType.Valued<T> type, T value) {
        itemStack.setData(type, value);
    }
    
    /**
     * Proxy method for {@link ItemStack#setData(DataComponentType.NonValued)}
     * <p>
     * Marks the given component as present in the item stack.
     *
     * @param type the data component type
     */
    @Experimental
    public void set(DataComponentType.NonValued type) {
        itemStack.setData(type);
    }
    
    /**
     * Proxy method for {@link ItemStack#unsetData(DataComponentType)}
     * <p>
     * Marks the given component as removed from the item stack.
     *
     * @param type the data component type
     */
    @Experimental
    public void unset(DataComponentType type) {
        itemStack.unsetData(type);
    }
    //</editor-fold>
    
    /**
     * Clones this builder.
     *
     * @return The cloned builder
     */
    @Override
    public ItemBuilder clone() {
        try {
            ItemBuilder clone = ((ItemBuilder) super.clone());
            clone.itemStack = itemStack.clone();
            if (lore != null) clone.lore = new ArrayList<>(lore);
            if (modifiers != null) clone.modifiers = new ArrayList<>(modifiers);
            
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }
    
}
