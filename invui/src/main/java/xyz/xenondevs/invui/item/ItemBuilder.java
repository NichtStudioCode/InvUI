package xyz.xenondevs.invui.item;

import io.papermc.paper.datacomponent.DataComponentBuilder;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import it.unimi.dsi.fastutil.booleans.BooleanArrayList;
import it.unimi.dsi.fastutil.booleans.BooleanList;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.floats.FloatList;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.i18n.Languages;
import xyz.xenondevs.invui.internal.util.ComponentUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.jetbrains.annotations.ApiStatus.Experimental;

/**
 * Utility for building (localized) {@link ItemStack ItemStacks}.
 */
@SuppressWarnings("UnstableApiUsage")
public final class ItemBuilder implements ItemProvider {
    
    private ItemStack itemStack;
    private @Nullable Component name;
    private @Nullable List<Component> lore;
    private @Nullable FloatList customModelDataFloats;
    private @Nullable BooleanList customModelDataBooleans;
    private @Nullable List<String> customModelDataStrings;
    private @Nullable List<Color> customModelDataColors;
    private @Nullable List<Function<ItemStack, ItemStack>> modifiers;
    
    private final Map<Locale, ItemStack> buildCache = new HashMap<>();
    
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
     * @param base The {@link ItemStack} to use as a base
     */
    public ItemBuilder(ItemStack base) {
        this.itemStack = base.clone();
        
        if (base.isDataOverridden(DataComponentTypes.LORE)) {
            ItemLore lore = base.getData(DataComponentTypes.LORE);
            assert lore != null;
            this.lore = lore.lines();
        }
    }
    
    /**
     * Builds the {@link ItemStack} in {@link Locale#US}.
     * 
     * @return The {@link ItemStack}
     */
    @Override
    public ItemStack get() {
        return get(Locale.US);
    }
    
    /**
     * Builds the {@link ItemStack}
     *
     * @param locale The {@link Locale} to use for localization
     * @return The {@link ItemStack}
     */
    @Override
    public ItemStack get(Locale locale) {
        return buildCache.computeIfAbsent(locale, lang1 -> {
            ItemStack itemStack = this.itemStack.clone();
            
            if (name != null) {
                itemStack.setData(
                    DataComponentTypes.ITEM_NAME,
                    Languages.getInstance().localized(locale, name)
                );
                itemStack.unsetData(DataComponentTypes.CUSTOM_NAME);
            }
            
            if (lore != null) {
                ItemLore.Builder lore = ItemLore.lore();
                for (Component line : this.lore) {
                    lore.addLine(Languages.getInstance().localized(locale, line));
                }
                
                itemStack.setData(DataComponentTypes.LORE, lore.build());
            }
            
            // TODO: Custom model data (1.21.4)
            
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
        this.name = ComponentUtils.withoutPreFormatting(name);
        hideTooltip(false);
        return this;
    }
    
    /**
     * Sets the name using mini-message format.
     *
     * @param name The name
     * @return The builder instance
     */
    public ItemBuilder setName(String name) {
        return setName(MiniMessage.miniMessage().deserialize(name));
    }
    
    /**
     * Sets the name using legacy text format.
     *
     * @param name The name
     * @return The builder instance
     */
    public ItemBuilder setLegacyName(String name) {
        return setName(LegacyComponentSerializer.legacySection().deserialize(name));
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
        if (lore != null)
            lore.remove(index);
        return this;
    }
    
    /**
     * Clears the lore.
     *
     * @return The builder instance
     */
    public ItemBuilder clearLore() {
        this.lore = null;
        itemStack.unsetData(DataComponentTypes.LORE);
        return this;
    }
    
    /**
     * Sets the lore.
     * Automatically un-hides the tooltip if hidden.
     *
     * @param lore The lore
     * @return The builder instance
     */
    public ItemBuilder setLore(List<Component> lore) {
        this.lore = lore.stream()
            .map(ComponentUtils::withoutPreFormatting)
            .collect(Collectors.toCollection(ArrayList::new));
        hideTooltip(false);
        return this;
    }
    
    /**
     * Sets the lore using the legacy text format.
     * Automatically un-hides the tooltip if hidden.
     *
     * @param lore The lore
     * @return The builder instance
     */
    public ItemBuilder setLegacyLore(List<String> lore) {
        this.lore = lore.stream()
            .map(line -> LegacyComponentSerializer.legacySection().deserialize(line))
            .map(ComponentUtils::withoutPreFormatting)
            .collect(Collectors.toCollection(ArrayList::new));
        hideTooltip(false);
        return this;
    }
    
    /**
     * Adds lore lindes using the legacy text format.
     * Automatically un-hides the tooltip if hidden.
     *
     * @param lines The lore lines
     * @return The builder instance
     */
    public ItemBuilder addLegacyLoreLines(String... lines) {
        return addLegacyLoreLines(Arrays.asList(lines));
    }
    
    /**
     * Adds lore lines in mini-message format.
     * Automatically un-hides the tooltip if hidden.
     *
     * @param lines The lore lines
     * @return The builder instance
     */
    public ItemBuilder addLoreLines(String... lines) {
        return addMiniMessageLoreLines(Arrays.asList(lines));
    }
    
    /**
     * Adds lore lines.
     * Automatically un-hides the tooltip if hidden.
     *
     * @param lines The lore lines
     * @return The builder instance
     */
    public ItemBuilder addLoreLines(Component... lines) {
        if (lore == null)
            lore = new ArrayList<>();
        
        for (Component line : lines) {
            lore.add(ComponentUtils.withoutPreFormatting(line));
        }
        
        hideTooltip(false);
        
        return this;
    }
    
    /**
     * Adds lore lines.
     * Automatically un-hides the tooltip if hidden.
     *
     * @param lines The lore lines
     * @return The builder instance
     */
    public ItemBuilder addLoreLines(List<Component> lines) {
        if (lore == null)
            lore = new ArrayList<>();
        
        for (Component line : lines) {
            lore.add(ComponentUtils.withoutPreFormatting(line));
        }
        
        hideTooltip(false);
        
        return this;
    }
    
    /**
     * Adds lore lines using the mini-message format.
     * Automatically un-hides the tooltip if hidden.
     *
     * @param lines The lore lines
     * @return The builder instance
     */
    public ItemBuilder addMiniMessageLoreLines(List<String> lines) {
        if (lore == null)
            lore = new ArrayList<>();
        
        for (String line : lines) {
            Component component = MiniMessage.miniMessage().deserialize(line);
            component = ComponentUtils.withoutPreFormatting(component);
            lore.add(component);
        }
        
        hideTooltip(false);
        
        return this;
    }
    
    /**
     * Adds lore lines using the legacy text format.
     * Automatically un-hides the tooltip if hidden.
     *
     * @param lines The lore lines
     * @return The builder instance
     */
    public ItemBuilder addLegacyLoreLines(List<String> lines) {
        if (lore == null)
            lore = new ArrayList<>();
        
        for (String line : lines) {
            Component component = LegacyComponentSerializer.legacySection().deserialize(line);
            component = ComponentUtils.withoutPreFormatting(component);
            lore.add(component);
        }
        
        hideTooltip(false);
        
        return this;
    }
    
    //</editor-fold>
    
    //<editor-fold desc="custom model data">
    
    /**
     * Adds the given custom model data entry to the `floats` section.
     *
     * @param value The value to add
     * @return The builder instance
     */
    public ItemBuilder addCustomModelData(float value) {
        if (customModelDataFloats == null)
            customModelDataFloats = new FloatArrayList();
        
        customModelDataFloats.add(value);
        return this;
    }
    
    /**
     * Adds the given custom model data entry to the `floats` section.
     *
     * @param value The value to add
     * @return The builder instance
     */
    public ItemBuilder addCustomModelData(double value) {
        return addCustomModelData((float) value);
    }
    
    /**
     * Adds the given custom model data entry to the `floats` section.
     *
     * @param value The value to add
     * @return The builder instance
     */
    public ItemBuilder addCustomModelData(int value) {
        return addCustomModelData((float) value);
    }
    
    /**
     * Adds the given custom model data entry to the `flags` section.
     *
     * @param value The value to add
     * @return The builder instance
     */
    public ItemBuilder addCustomModelData(boolean value) {
        if (customModelDataBooleans == null)
            customModelDataBooleans = new BooleanArrayList();
        
        customModelDataBooleans.add(value);
        return this;
    }
    
    /**
     * Adds the given custom model data entry to the `strings` section.
     *
     * @param value The value to add
     * @return The builder instance
     */
    public ItemBuilder addCustomModelData(String value) {
        if (customModelDataStrings == null)
            customModelDataStrings = new ArrayList<>();
        
        customModelDataStrings.add(value);
        return this;
    }
    
    /**
     * Adds the given custom model data entry to the `colors` section.
     *
     * @param value The value to add
     * @return The builder instance
     */
    public ItemBuilder addCustomModelData(Color value) {
        if (customModelDataColors == null)
            customModelDataColors = new ArrayList<>();
        
        customModelDataColors.add(value);
        return this;
    }
    
    /**
     * Adds the given custom model data entry to the `colors` section.
     *
     * @param value The value to add
     * @return The builder instance
     */
    public ItemBuilder addCustomModelData(java.awt.Color value) {
        return addCustomModelData(Color.fromARGB(value.getRGB()));
    }
    
    /**
     * Sets the custom model data entry in `floats` at the given index to the given value,
     * filling smaller indices with zeros if necessary.
     *
     * @param index The index
     * @param value The value
     * @return The builder instance
     */
    public ItemBuilder setCustomModelData(int index, float value) {
        if (index < 0)
            throw new IndexOutOfBoundsException(index);
        
        if (customModelDataFloats == null)
            customModelDataFloats = new FloatArrayList();
        
        while (customModelDataFloats.size() <= index) {
            customModelDataFloats.add(0);
        }
        
        customModelDataFloats.set(index, value);
        return this;
    }
    
    /**
     * Sets the custom model data entry in `floats` at the given index to the given value,
     * filling smaller indices with zeros if necessary.
     *
     * @param index The index
     * @param value The value
     * @return The builder instance
     */
    public ItemBuilder setCustomModelData(int index, double value) {
        return setCustomModelData(index, (float) value);
    }
    
    /**
     * Sets the custom model data entry in `floats` at the given index to the given value,
     * filling smaller indices with zeros if necessary.
     *
     * @param index The index
     * @param value The value
     * @return The builder instance
     */
    public ItemBuilder setCustomModelData(int index, int value) {
        return setCustomModelData(index, (float) value);
    }
    
    /**
     * Sets the custom model data entry in `flags` at the given index to the given value,
     * filling smaller indices with `false` if necessary.
     *
     * @param index The index
     * @param value The value
     * @return The builder instance
     */
    public ItemBuilder setCustomModelData(int index, boolean value) {
        if (index < 0)
            throw new IndexOutOfBoundsException(index);
        
        if (customModelDataBooleans == null)
            customModelDataBooleans = new BooleanArrayList();
        
        while (customModelDataBooleans.size() <= index) {
            customModelDataBooleans.add(false);
        }
        
        customModelDataBooleans.set(index, value);
        return this;
    }
    
    /**
     * Sets the custom model data entry in `strings` at the given index to the given value,
     * filling smaller indices with empty strings if necessary.
     *
     * @param index The index
     * @param value The value
     * @return The builder instance
     */
    public ItemBuilder setCustomModelData(int index, String value) {
        if (index < 0)
            throw new IndexOutOfBoundsException(index);
        
        if (customModelDataStrings == null)
            customModelDataStrings = new ArrayList<>();
        
        while (customModelDataStrings.size() <= index) {
            customModelDataStrings.add("");
        }
        
        customModelDataStrings.set(index, value);
        return this;
    }
    
    /**
     * Sets the custom model data entry in `colors` at the given index to the given value,
     * filling smaller indices with white if necessary.
     *
     * @param index The index
     * @param value The value
     * @return The builder instance
     */
    public ItemBuilder setCustomModelData(int index, Color value) {
        if (index < 0)
            throw new IndexOutOfBoundsException(index);
        
        if (customModelDataColors == null)
            customModelDataColors = new ArrayList<>();
        
        while (customModelDataColors.size() <= index) {
            customModelDataColors.add(Color.WHITE);
        }
        
        customModelDataColors.set(index, value);
        return this;
    }
    
    /**
     * Sets the custom model data entry in `colors` at the given index to the given value,
     * filling smaller indices with white if necessary.
     *
     * @param index The index
     * @param value The value
     * @return The builder instance
     */
    public ItemBuilder setCustomModelData(int index, java.awt.Color value) {
        return setCustomModelData(index, Color.fromARGB(value.getRGB()));
    }
    
    /**
     * Sets all custom model data entries.
     *
     * @param floats  The `floats` section
     * @param flags   The `flags` section
     * @param strings The `strings` section
     * @param colors  The `colors` section
     * @return The builder instance
     */
    public ItemBuilder setCustomModelData(float[] floats, boolean[] flags, String[] strings, Color[] colors) {
        customModelDataFloats = new FloatArrayList(floats);
        customModelDataBooleans = new BooleanArrayList(flags);
        customModelDataStrings = new ArrayList<>(Arrays.asList(strings));
        customModelDataColors = new ArrayList<>(Arrays.asList(colors));
        return this;
    }
    
    /**
     * Sets all custom model data entries in the `floats` section.
     *
     * @param floats The `floats` section
     * @return The builder instance
     */
    public ItemBuilder setCustomModelData(float[] floats) {
        customModelDataFloats = new FloatArrayList(floats);
        return this;
    }
    
    /**
     * Sets all custom model data entries in the `flags` section.
     *
     * @param flags The `flags` section
     * @return The builder instance
     */
    public ItemBuilder setCustomModelData(boolean[] flags) {
        customModelDataBooleans = new BooleanArrayList(flags);
        return this;
    }
    
    /**
     * Sets all custom model data entries in the `strings` section.
     *
     * @param strings The `strings` section
     * @return The builder instance
     */
    public ItemBuilder setCustomModelData(String[] strings) {
        customModelDataStrings = new ArrayList<>(Arrays.asList(strings));
        return this;
    }
    
    /**
     * Sets all custom model data entries in the `colors` section.
     *
     * @param colors The `colors` section
     * @return The builder instance
     */
    public ItemBuilder setCustomModelData(Color[] colors) {
        customModelDataColors = new ArrayList<>(Arrays.asList(colors));
        return this;
    }
    
    /**
     * Clears all custom model data entries.
     *
     * @param floats  The `floats` section
     * @param flags   The `flags` section
     * @param strings The `strings` section
     * @param colors  The `colors` section
     * @return The builder instance
     */
    public ItemBuilder setCustomModelData(List<Float> floats, List<Boolean> flags, List<String> strings, List<Color> colors) {
        customModelDataFloats = new FloatArrayList(floats);
        customModelDataBooleans = new BooleanArrayList(flags);
        customModelDataStrings = new ArrayList<>(strings);
        customModelDataColors = new ArrayList<>(colors);
        return this;
    }
    
    /**
     * Sets all custom model data entries in the `floats` section.
     *
     * @param floats The `floats` section
     * @return The builder instance
     */
    public ItemBuilder setCustomModelDataFloats(List<Float> floats) {
        customModelDataFloats = new FloatArrayList(floats);
        return this;
    }
    
    /**
     * Sets all custom model data entries in the `flags` section.
     *
     * @param flags The `flags` section
     * @return The builder instance
     */
    public ItemBuilder setCustomModelDataFlags(List<Boolean> flags) {
        customModelDataBooleans = new BooleanArrayList(flags);
        return this;
    }
    
    /**
     * Sets all custom model data entries in the `strings` section.
     *
     * @param strings The `strings` section
     * @return The builder instance
     */
    public ItemBuilder setCustomModelDataStrings(List<String> strings) {
        customModelDataStrings = new ArrayList<>(strings);
        return this;
    }
    
    /**
     * Sets all custom model data entries in the `colors` section.
     *
     * @param colors The `colors` section
     * @return The builder instance
     */
    public ItemBuilder setCustomModelDataColors(List<Color> colors) {
        customModelDataColors = new ArrayList<>(colors);
        return this;
    }
    
    /**
     * Clears all custom model data entries.
     *
     * @return The builder instance
     */
    public ItemBuilder clearCustomModelData() {
        itemStack.unsetData(DataComponentTypes.CUSTOM_MODEL_DATA);
        
        customModelDataFloats = null;
        customModelDataBooleans = null;
        customModelDataStrings = null;
        customModelDataColors = null;
        
        return this;
    }
    
    //</editor-fold>
    
    //<editor-fold desc="misc">
    
    /**
     * Hides or un-hides the entire tooltip.
     *
     * @param hide Whether to hide the tooltip
     * @return The builder instance
     */
    public ItemBuilder hideTooltip(boolean hide) {
        if (hide) {
            itemStack.setData(DataComponentTypes.HIDE_TOOLTIP);
        } else {
            itemStack.unsetData(DataComponentTypes.HIDE_TOOLTIP);
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
    public <T> ItemBuilder set(DataComponentType.Valued<T> type, DataComponentBuilder<T> valueBuilder) {
        itemStack.setData(type, valueBuilder);
        return this;
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
    public <T> ItemBuilder set(final DataComponentType.Valued<T> type, T value) {
        itemStack.setData(type, value);
        return this;
    }
    
    /**
     * Proxy method for {@link ItemStack#setData(DataComponentType.NonValued)}
     * <p>
     * Marks the given component as present in the item stack.
     *
     * @param type the data component type
     */
    @Experimental
    public ItemBuilder set(DataComponentType.NonValued type) {
        itemStack.setData(type);
        return this;
    }
    
    /**
     * Proxy method for {@link ItemStack#unsetData(DataComponentType)}
     * <p>
     * Marks the given component as removed from the item stack.
     *
     * @param type the data component type
     */
    @Experimental
    public ItemBuilder unset(DataComponentType type) {
        itemStack.unsetData(type);
        return this;
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
