package xyz.xenondevs.invui.item;

import com.google.common.collect.Sets;
import io.papermc.paper.datacomponent.DataComponentBuilder;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import io.papermc.paper.datacomponent.item.TooltipDisplay;
import it.unimi.dsi.fastutil.booleans.BooleanArrayList;
import it.unimi.dsi.fastutil.booleans.BooleanList;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.floats.FloatList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.StyleBuilderApplicable;
import net.kyori.adventure.text.minimessage.tag.TagPattern;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomModelData;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.i18n.Languages;
import xyz.xenondevs.invui.internal.util.ArrayUtils;
import xyz.xenondevs.invui.internal.util.ComponentUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;
import static net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacySection;
import static org.jetbrains.annotations.ApiStatus.Experimental;

/**
 * Utility for building (localized) {@link ItemStack ItemStacks}.
 */
@SuppressWarnings("UnstableApiUsage")
public final class ItemBuilder implements ItemProvider {
    
    private ItemStack itemStack;
    private @Nullable ComponentHolder name;
    private @Nullable ComponentHolder customName;
    private @Nullable List<ComponentHolder> lore;
    private @Nullable FloatList customModelDataFloats;
    private @Nullable BooleanList customModelDataBooleans;
    private @Nullable List<String> customModelDataStrings;
    private @Nullable IntList customModelDataColors;
    private @Nullable Map<String, TagResolver> placeholders;
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
        
        ItemLore lore = base.getData(DataComponentTypes.LORE);
        if (lore != null) {
            this.lore = lore.lines().stream()
                .map(DirectComponentHolder::new)
                .collect(Collectors.toCollection(ArrayList::new));
        }
        
        CustomModelData cmd = CraftItemStack.unwrap(base).get(DataComponents.CUSTOM_MODEL_DATA);
        if (cmd != null) {
            customModelDataFloats = new FloatArrayList(cmd.floats());
            customModelDataBooleans = new BooleanArrayList(cmd.flags());
            customModelDataStrings = new ArrayList<>(cmd.strings());
            customModelDataColors = new IntArrayList(cmd.colors());
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
     * Retrieves the {@link ItemStack}. This may {@link #build(Locale)} the
     * {@link ItemStack} or return a cached version.
     *
     * @param locale The {@link Locale} to use for localization
     * @return The {@link ItemStack}
     */
    @Override
    public ItemStack get(Locale locale) {
        return buildCache.computeIfAbsent(locale, this::build);
    }
    
    /**
     * Builds the {@link ItemStack}.
     *
     * @return The {@link ItemStack}
     */
    public ItemStack build() {
        return build(Locale.US);
    }
    
    /**
     * Builds the {@link ItemStack}.
     *
     * @param locale The {@link Locale} to use for localization
     * @return The {@link ItemStack}
     */
    public ItemStack build(Locale locale) {
        ItemStack itemStack = this.itemStack.clone();
        
        TagResolver[] resolvers = this.placeholders != null
            ? this.placeholders.values().toArray(TagResolver[]::new)
            : new TagResolver[0];
        
        if (this.name != null) {
            Component name = this.name.get(resolvers);
            name = Languages.getInstance().localized(locale, name, resolvers);
            itemStack.setData(DataComponentTypes.ITEM_NAME, name);
        }
        
        if (this.customName != null) {
            Component customName = this.customName.get(resolvers);
            customName = Languages.getInstance().localized(locale, customName, resolvers);
            itemStack.setData(DataComponentTypes.CUSTOM_NAME, customName);
        }
        
        if (lore != null) {
            ItemLore.Builder lore = ItemLore.lore();
            for (ComponentHolder lineHolder : this.lore) {
                Component line = lineHolder.get(resolvers);
                line = Languages.getInstance().localized(locale, line, resolvers);
                lore.addLine(line);
            }
            
            itemStack.setData(DataComponentTypes.LORE, lore.build());
        }
        
        if (customModelDataFloats != null
            || customModelDataBooleans != null
            || customModelDataStrings != null
            || customModelDataColors != null
        ) {
            CraftItemStack.unwrap(itemStack).set(
                DataComponents.CUSTOM_MODEL_DATA,
                new CustomModelData(
                    customModelDataFloats != null ? new FloatArrayList(customModelDataFloats) : new FloatArrayList(),
                    customModelDataBooleans != null ? new BooleanArrayList(customModelDataBooleans) : new BooleanArrayList(),
                    customModelDataStrings != null ? new ArrayList<>(customModelDataStrings) : new ArrayList<>(),
                    customModelDataColors != null ? new IntArrayList(customModelDataColors) : new IntArrayList()
                )
            );
        }
        
        if (modifiers != null) {
            for (var modifier : modifiers) {
                itemStack = modifier.apply(itemStack);
            }
        }
        
        return itemStack;
    }
    
    //<editor-fold desc="base">
    
    /**
     * Sets the {@link Material} of this builder.
     *
     * @param material The {@link Material}
     * @return The builder instance
     */
    public ItemBuilder setMaterial(Material material) {
        buildCache.clear();
        
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
        buildCache.clear();
        
        itemStack.setAmount(amount);
        return this;
    }
    
    //</editor-fold>
    
    //<editor-fold desc="placeholders">
    
    private ItemBuilder setPlaceholder(@TagPattern String key, TagResolver placeholder) {
        buildCache.clear();
        
        if (placeholders == null)
            placeholders = new HashMap<>();
        
        placeholders.put(key, placeholder);
        
        return this;
    }
    
    /**
     * Sets the placeholder under the given key to the given value.
     * <p>
     * Placeholders are applied to item name, custom name and lore, if
     * they are set using mini-message format.
     *
     * @param key   The key
     * @param value The value, interpreted as a mini-message string
     * @return The builder instance
     * @see Placeholder#parsed(String, String)
     */
    public ItemBuilder setPlaceholder(@TagPattern String key, String value) {
        return setPlaceholder(key, Placeholder.parsed(key, value));
    }
    
    /**
     * Sets the placeholder under the given key to the given value.
     * <p>
     * Placeholders are applied to item name, custom name and lore, if
     * they are set using mini-message format.
     *
     * @param key   The key
     * @param value The value, interpreted as a plain string
     * @return The builder instance
     * @see Placeholder#unparsed(String, String)
     */
    public ItemBuilder setPlaceholderUnparsed(@TagPattern String key, String value) {
        return setPlaceholder(key, Placeholder.unparsed(key, value));
    }
    
    /**
     * Sets the placeholder under the given key to the given value.
     * <p>
     * Placeholders are applied to item name, custom name and lore, if
     * they are set using mini-message format.
     *
     * @param key   The key
     * @param value The value
     * @return The builder instance
     * @see Placeholder#component(String, ComponentLike)
     */
    public ItemBuilder setPlaceholder(@TagPattern String key, ComponentLike value) {
        return setPlaceholder(key, Placeholder.component(key, value));
    }
    
    /**
     * Sets the placeholder under the given key to the given value.
     * <p>
     * Placeholders are applied to item name, custom name and lore, if
     * they are set using mini-message format.
     *
     * @param key   The key
     * @param style The style to apply to the placeholder
     * @return The builder instance
     * @see Placeholder#styling(String, StyleBuilderApplicable...)
     */
    public ItemBuilder setPlaceholder(@TagPattern String key, StyleBuilderApplicable... style) {
        return setPlaceholder(key, Placeholder.styling(key, style));
    }
    
    private ItemBuilder setPlaceholders(Map<String, TagResolver> placeholders) {
        buildCache.clear();
        this.placeholders = placeholders;
        return this;
    }
    
    /**
     * Replaces all previously configured placeholders with the given map, where each value
     * is interpreted as a parsed placeholder.
     * <p>
     * Placeholders are applied to item name, custom name and lore, if
     * they are set using mini-message format.
     *
     * @param placeholders The placeholders
     * @return The builder instance
     * @see Placeholder#parsed(String, String)
     */
    @SuppressWarnings("PatternValidation")
    public ItemBuilder setPlaceholdersParsed(Map<String, String> placeholders) {
        var map = new HashMap<String, TagResolver>();
        placeholders.forEach((key, value) -> map.put(key, Placeholder.parsed(key, value)));
        return setPlaceholders(map);
    }
    
    /**
     * Replaces all previously configured placeholders with the given map, where each value
     * is interpreted as an unparsed placeholder.
     * <p>
     * Placeholders are applied to item name, custom name and lore, if
     * they are set using mini-message format.
     *
     * @param placeholders The placeholders
     * @return The builder instance
     * @see Placeholder#unparsed(String, String)
     */
    @SuppressWarnings("PatternValidation")
    public ItemBuilder setPlaceholdersUnparsed(Map<String, String> placeholders) {
        var map = new HashMap<String, TagResolver>();
        placeholders.forEach((key, value) -> map.put(key, Placeholder.unparsed(key, value)));
        return setPlaceholders(map);
    }
    
    /**
     * Replaces all previously configured placeholders with the given map.
     * <p>
     * Placeholders are applied to item name, custom name and lore, if
     * they are set using mini-message format.
     *
     * @param placeholders The placeholders
     * @return The builder instance
     * @see Placeholder#component(String, ComponentLike)
     */
    @SuppressWarnings("PatternValidation")
    public ItemBuilder setPlaceholdersComponent(Map<String, ComponentLike> placeholders) {
        var map = new HashMap<String, TagResolver>();
        placeholders.forEach((key, value) -> map.put(key, Placeholder.component(key, value)));
        return setPlaceholders(map);
    }
    
    /**
     * Replaces all previously configured placeholders with the given map.
     * <p>
     * Placeholders are applied to item name, custom name and lore, if
     * they are set using mini-message format.
     *
     * @param placeholders The placeholders
     * @return The builder instance
     * @see Placeholder#styling(String, StyleBuilderApplicable...)
     */
    @SuppressWarnings("PatternValidation")
    public ItemBuilder setPlaceholdersStyling(Map<String, StyleBuilderApplicable[]> placeholders) {
        var map = new HashMap<String, TagResolver>();
        placeholders.forEach((key, value) -> map.put(key, Placeholder.styling(key, value)));
        return setPlaceholders(map);
    }
    
    //</editor-fold>
    
    //<editor-fold desc="name">
    
    /**
     * Sets the name.
     * Removes custom name.
     * Automatically un-hides the tooltip if hidden.
     *
     * @param name The name
     * @return The builder instance
     */
    public ItemBuilder setName(Component name) {
        buildCache.clear();
        
        this.name = new DirectComponentHolder(name);
        this.customName = null;
        unset(DataComponentTypes.CUSTOM_NAME);
        hideTooltip(false);
        return this;
    }
    
    /**
     * Sets the name using mini-message format.
     * Removes custom name.
     * Automatically un-hides the tooltip if hidden.
     *
     * @param name The name
     * @return The builder instance
     */
    public ItemBuilder setName(String name) {
        buildCache.clear();
        
        this.name = new MiniMessageComponentHolder(name);
        this.customName = null;
        unset(DataComponentTypes.CUSTOM_NAME);
        hideTooltip(false);
        return this;
    }
    
    /**
     * Sets the name using legacy text format.
     * Removes custom name.
     * Automatically un-hides the tooltip if hidden.
     *
     * @param name The name
     * @return The builder instance
     */
    public ItemBuilder setLegacyName(String name) {
        return setName(legacySection().deserialize(name));
    }
    
    /**
     * Sets the custom name.
     * Removes item name.
     * Automatically un-hides the tooltip if hidden.
     *
     * @param customName The custom name
     * @return The builder instance
     */
    public ItemBuilder setCustomName(Component customName) {
        buildCache.clear();
        
        this.customName = new DirectComponentHolder(customName);
        this.name = null;
        unset(DataComponentTypes.ITEM_NAME);
        hideTooltip(false);
        return this;
    }
    
    /**
     * Sets the name using mini-message format.
     * Removes item name.
     * Automatically un-hides the tooltip if hidden.
     *
     * @param customName The name
     * @return The builder instance
     */
    public ItemBuilder setCustomName(String customName) {
        buildCache.clear();
        
        this.customName = new MiniMessageComponentHolder(customName);
        this.name = null;
        unset(DataComponentTypes.ITEM_NAME);
        hideTooltip(false);
        return this;
    }
    
    /**
     * Sets the name using legacy text format.
     * Removes item name.
     * Automatically un-hides the tooltip if hidden.
     *
     * @param customName The name
     * @return The builder instance
     */
    public ItemBuilder setLegacyCustomName(String customName) {
        return setCustomName(legacySection().deserialize(customName));
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
        buildCache.clear();
        
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
        buildCache.clear();
        
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
        buildCache.clear();
        
        this.lore = lore.stream()
            .map(DirectComponentHolder::new)
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
        buildCache.clear();
        
        this.lore = lore.stream()
            .map(line -> legacySection().deserialize(line))
            .map(DirectComponentHolder::new)
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
        buildCache.clear();
        
        if (lore == null)
            lore = new ArrayList<>();
        
        for (Component line : lines) {
            lore.add(new DirectComponentHolder(line));
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
        buildCache.clear();
        
        if (lore == null)
            lore = new ArrayList<>();
        
        for (Component line : lines) {
            lore.add(new DirectComponentHolder(line));
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
        buildCache.clear();
        
        if (lore == null)
            lore = new ArrayList<>();
        
        for (String line : lines) {
            lore.add(new MiniMessageComponentHolder(line));
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
        buildCache.clear();
        
        if (lore == null)
            lore = new ArrayList<>();
        
        for (String line : lines) {
            lore.add(new DirectComponentHolder(legacySection().deserialize(line)));
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
        buildCache.clear();
        
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
        buildCache.clear();
        
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
        buildCache.clear();
        
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
        buildCache.clear();
        
        if (customModelDataColors == null)
            customModelDataColors = new IntArrayList();
        
        customModelDataColors.add(value.asARGB());
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
        
        buildCache.clear();
        
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
        
        buildCache.clear();
        
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
        
        buildCache.clear();
        
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
        
        buildCache.clear();
        
        if (customModelDataColors == null)
            customModelDataColors = new IntArrayList();
        
        while (customModelDataColors.size() <= index) {
            customModelDataColors.add(0xFFFFFFFF);
        }
        
        customModelDataColors.set(index, value.asARGB());
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
        buildCache.clear();
        
        customModelDataFloats = new FloatArrayList(floats);
        customModelDataBooleans = new BooleanArrayList(flags);
        customModelDataStrings = new ArrayList<>(Arrays.asList(strings));
        customModelDataColors = Arrays.stream(colors)
            .map(Color::asARGB)
            .collect(Collectors.toCollection(IntArrayList::new));
        return this;
    }
    
    /**
     * Sets all custom model data entries in the `floats` section.
     *
     * @param floats The `floats` section
     * @return The builder instance
     */
    public ItemBuilder setCustomModelData(float[] floats) {
        buildCache.clear();
        
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
        buildCache.clear();
        
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
        buildCache.clear();
        
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
        buildCache.clear();
        
        customModelDataColors = Arrays.stream(colors)
            .map(Color::asARGB)
            .collect(Collectors.toCollection(IntArrayList::new));
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
        buildCache.clear();
        
        customModelDataFloats = new FloatArrayList(floats);
        customModelDataBooleans = new BooleanArrayList(flags);
        customModelDataStrings = new ArrayList<>(strings);
        customModelDataColors = colors.stream()
            .map(Color::asARGB)
            .collect(Collectors.toCollection(IntArrayList::new));
        return this;
    }
    
    /**
     * Sets all custom model data entries in the `floats` section.
     *
     * @param floats The `floats` section
     * @return The builder instance
     */
    public ItemBuilder setCustomModelDataFloats(List<Float> floats) {
        buildCache.clear();
        
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
        buildCache.clear();
        
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
        buildCache.clear();
        
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
        buildCache.clear();
        
        customModelDataColors = colors.stream()
            .map(Color::asARGB)
            .collect(Collectors.toCollection(IntArrayList::new));
        return this;
    }
    
    /**
     * Clears all custom model data entries.
     *
     * @return The builder instance
     */
    public ItemBuilder clearCustomModelData() {
        buildCache.clear();
        
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
        buildCache.clear();
        
        TooltipDisplay display = itemStack.getData(DataComponentTypes.TOOLTIP_DISPLAY);
        itemStack.setData(
            DataComponentTypes.TOOLTIP_DISPLAY,
            TooltipDisplay.tooltipDisplay()
                .hideTooltip(hide)
                .hiddenComponents(display != null ? display.hiddenComponents() : Set.of())
                .build()
        );
        
        return this;
    }
    
    /**
     * Hides the tooltip for the given data components.
     *
     * @param type  The first data component type to hide the tooltip for
     * @param types Additional data component types to hide the tooltip for
     * @return The builder instance
     * @see #showTooltip(DataComponentType, DataComponentType...)
     * @see #hideTooltip(boolean)
     */
    public ItemBuilder hideTooltip(DataComponentType type, DataComponentType... types) {
        buildCache.clear();
        
        TooltipDisplay display = itemStack.getData(DataComponentTypes.TOOLTIP_DISPLAY);
        itemStack.setData(
            DataComponentTypes.TOOLTIP_DISPLAY,
            TooltipDisplay.tooltipDisplay()
                .hideTooltip(display != null && display.hideTooltip())
                .hiddenComponents(display != null ? display.hiddenComponents() : Set.of())
                .hiddenComponents(Set.of(ArrayUtils.concat(DataComponentType[]::new, type, types)))
                .build()
        );
        
        return this;
    }
    
    /**
     * Un-hides the tooltip for the given data components that were previously hidden.
     *
     * @param type  The first data component type to un-hide the tooltip for
     * @param types Additional data component types to un-hide the tooltip for
     * @return The builder instance
     * @see #hideTooltip(DataComponentType, DataComponentType...)
     * @see #hideTooltip(boolean)
     */
    public ItemBuilder showTooltip(DataComponentType type, DataComponentType... types) {
        TooltipDisplay display = itemStack.getData(DataComponentTypes.TOOLTIP_DISPLAY);
        if (display == null)
            return this;
        
        buildCache.clear();
        
        itemStack.setData(
            DataComponentTypes.TOOLTIP_DISPLAY,
            TooltipDisplay.tooltipDisplay()
                .hideTooltip(display.hideTooltip())
                .hiddenComponents(Sets.difference(
                    display.hiddenComponents(),
                    Set.of(ArrayUtils.concat(DataComponentType[]::new, type, types))
                ))
                .build()
        );
        
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
        buildCache.clear();
        
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
        buildCache.clear();
        
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
     * @return The builder instance
     */
    @Experimental
    public <T> ItemBuilder set(DataComponentType.Valued<T> type, DataComponentBuilder<T> valueBuilder) {
        buildCache.clear();
        
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
     * @return The builder instance
     */
    @Experimental
    public <T> ItemBuilder set(final DataComponentType.Valued<T> type, T value) {
        buildCache.clear();
        
        itemStack.setData(type, value);
        return this;
    }
    
    /**
     * Proxy method for {@link ItemStack#setData(DataComponentType.NonValued)}
     * <p>
     * Marks the given component as present in the item stack.
     *
     * @param type the data component type
     * @return The builder instance
     */
    @Experimental
    public ItemBuilder set(DataComponentType.NonValued type) {
        buildCache.clear();
        
        itemStack.setData(type);
        return this;
    }
    
    /**
     * Proxy method for {@link ItemStack#unsetData(DataComponentType)}
     * <p>
     * Marks the given component as removed from the item stack.
     *
     * @param type the data component type
     * @return The builder instance
     */
    @Experimental
    public ItemBuilder unset(DataComponentType type) {
        buildCache.clear();
        
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
            if (lore != null)
                clone.lore = new ArrayList<>(lore);
            if (customModelDataFloats != null)
                clone.customModelDataFloats = new FloatArrayList(customModelDataFloats);
            if (customModelDataBooleans != null)
                clone.customModelDataBooleans = new BooleanArrayList(customModelDataBooleans);
            if (customModelDataStrings != null)
                clone.customModelDataStrings = new ArrayList<>(customModelDataStrings);
            if (customModelDataColors != null)
                clone.customModelDataColors = new IntArrayList(customModelDataColors);
            if (placeholders != null)
                clone.placeholders = new HashMap<>(placeholders);
            if (modifiers != null)
                clone.modifiers = new ArrayList<>(modifiers);
            
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }
    
    private sealed interface ComponentHolder {
        
        Component get(TagResolver[] resolvers);
        
    }
    
    private record DirectComponentHolder(Component component) implements ComponentHolder {
        
        private DirectComponentHolder(Component component) {
            this.component = ComponentUtils.withoutPreFormatting(component);
        }
        
        @Override
        public Component get(TagResolver[] resolvers) {
            return component;
        }
        
    }
    
    private record MiniMessageComponentHolder(String format) implements ComponentHolder {
        
        @Override
        public Component get(TagResolver[] resolvers) {
            Component component = miniMessage().deserialize(format, resolvers);
            component = ComponentUtils.withoutPreFormatting(component);
            return component;
        }
        
    }
    
}
