package xyz.xenondevs.invui.dsl

import io.papermc.paper.datacomponent.DataComponentType
import io.papermc.paper.datacomponent.DataComponentTypes
import io.papermc.paper.datacomponent.item.ItemLore.lore
import io.papermc.paper.datacomponent.item.TooltipDisplay.tooltipDisplay
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ItemType
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType
import xyz.xenondevs.commons.provider.NULL_PROVIDER
import xyz.xenondevs.commons.provider.Provider
import xyz.xenondevs.commons.provider.combinedProvider
import xyz.xenondevs.commons.provider.dsl.ProviderDslProperty
import xyz.xenondevs.commons.provider.dsl.DslProperty
import xyz.xenondevs.commons.provider.provider
import xyz.xenondevs.invui.internal.util.ComponentUtils
import xyz.xenondevs.invui.item.ItemProvider
import xyz.xenondevs.invui.item.ItemWrapper
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Creates a reactive [Provider]-based [ItemProvider] using the DSL, starting from an empty
 * [ItemStack].
 *
 * ```
 * val myProvider = itemProvider {
 *     type by ItemType.DIAMOND_SWORD
 *     name by "<red>Fire Sword"
 *     lore by listOf("<gray>A legendary weapon")
 *     hasGlint by true
 * }
 * ```
 *
 * @see ItemProviderDsl
 */
@ExperimentalDslApi
inline fun itemProvider(itemProvider: ItemProviderDsl.() -> Unit): Provider<ItemProvider> {
    contract { callsInPlace(itemProvider, InvocationKind.EXACTLY_ONCE) }
    return ItemProviderDslImpl(provider(ItemStack.empty())).apply(itemProvider).build()
}

/**
 * Creates a reactive [Provider]-based [ItemProvider] using the DSL, starting from a reactive
 * [base][base] [ItemStack][ItemStack].
 *
 * ```
 * val baseStack: Provider<ItemStack> = ...
 * val myProvider = itemProvider(baseStack) {
 *     name by "<green>Enhanced Item"
 *     amount by 5
 * }
 * ```
 *
 * @see ItemProviderDsl
 */
@JvmName("itemProviderByItemStackProvider")
@ExperimentalDslApi
inline fun itemProvider(base: Provider<ItemStack>, itemProvider: ItemProviderDsl.() -> Unit): Provider<ItemProvider> {
    contract { callsInPlace(itemProvider, InvocationKind.EXACTLY_ONCE) }
    return ItemProviderDslImpl(base).apply(itemProvider).build()
}

/**
 * Creates a reactive [Provider]-based [ItemProvider] using the DSL, starting from a static
 * [base] [ItemStack].
 *
 * ```
 * val myProvider = itemProvider(ItemStack(Material.DIAMOND)) {
 *     name by "<aqua>Shiny Diamond"
 *     amount by 3
 * }
 * ```
 *
 * @see ItemProviderDsl
 */
@JvmName("itemProviderByItemStack")
@ExperimentalDslApi
inline fun itemProvider(base: ItemStack, itemProvider: ItemProviderDsl.() -> Unit): Provider<ItemProvider> {
    contract { callsInPlace(itemProvider, InvocationKind.EXACTLY_ONCE) }
    return itemProvider(provider(base), itemProvider)
}

/**
 * Creates a reactive [Provider]-based [ItemProvider] using the DSL, starting from an [ItemStack]
 * of the given static [ItemType].
 *
 * ```
 * val myProvider = itemProvider(ItemType.GOLDEN_APPLE) {
 *     name by "<gold>Special Apple"
 *     hasTooltip by true
 * }
 * ```
 *
 * @see ItemProviderDsl
 */
@JvmName("itemProviderByItemType")
@ExperimentalDslApi
inline fun itemProvider(type: ItemType, itemProvider: ItemProviderDsl.() -> Unit): Provider<ItemProvider> {
    contract { callsInPlace(itemProvider, InvocationKind.EXACTLY_ONCE) }
    return itemProvider(provider(type.createItemStack()), itemProvider)
}

/**
 * Creates a reactive [Provider]-based [ItemProvider] using the DSL, starting from a reactive [type].
 *
 * ```
 * val baseType: Provider<ItemType> = ...
 * val myProvider = itemProvider(baseType) {
 *     name by "<green>Enhanced Item"
 *     amount by 5
 * }
 * ```
 *
 * @see ItemProviderDsl
 */
@JvmName("itemProviderByItemTypeProvider")
@ExperimentalDslApi
inline fun itemProvider(type: Provider<ItemType>, itemProvider: ItemProviderDsl.() -> Unit): Provider<ItemProvider> {
    contract { callsInPlace(itemProvider, InvocationKind.EXACTLY_ONCE) }
    return itemProvider(type.map { it.createItemStack() }, itemProvider)
}

/**
 * DSL scope for configuring an [ItemProvider] with reactive properties.
 *
 * Provides convenient properties for common item attributes like [name], [lore], and [amount].
 * Each property can be set to a static value or bound to a [Provider] for reactive updates.
 * String values for [name] and [lore] are automatically parsed as
 * [MiniMessage][net.kyori.adventure.text.minimessage.MiniMessage].
 *
 * For data components not covered by the convenience properties, use [data] to access
 * arbitrary [DataComponentType][io.papermc.paper.datacomponent.DataComponentType]s directly.
 *
 * ```
 * val myProvider = itemProvider(ItemType.DIAMOND_SWORD) {
 *     name by "<red>Fire Sword"
 *     lore by listOf("<gray>A legendary weapon", "<gray>Forged in flames")
 *     hasGlint by true
 *     data[DataComponentTypes.MAX_DAMAGE] by 500
 * }
 * ```
 */
@ExperimentalDslApi
sealed interface ItemProviderDsl {
    
    /**
     * The base [ItemStack] that all other properties are applied on top of.
     *
     * Defaults to an empty [ItemStack]. Can be set to a static value or bound to a [Provider]:
     * ```
     * base by ItemStack(Material.DIAMOND_SWORD)
     * ```
     */
    val base: ProviderDslProperty<ItemStack>
    
    /**
     * The [ItemType] to override on the base stack, or `null` to keep the base stack's type.
     *
     * Defaults to `null`. Can be set to a static value or bound to a [Provider]:
     * ```
     * type by ItemType.NETHERITE_SWORD
     * ```
     */
    val type: ProviderDslProperty<ItemType?>
    
    /**
     * The stack amount to override, or `null` to keep the base stack's amount.
     *
     * Defaults to `null`. Can be set to a static value or bound to a [Provider]:
     * ```
     * amount by 16
     * ```
     */
    val amount: ProviderDslProperty<Int?>
    
    /**
     * The item name ([DataComponentTypes.ITEM_NAME][io.papermc.paper.datacomponent.DataComponentTypes.ITEM_NAME]),
     * or `null` to keep the base stack's name. Setting this automatically enables the tooltip.
     *
     * Defaults to `null`. Can be set to a [Component] or bound to a [Provider]:
     * ```
     * name by Component.text("Fire Sword").color(NamedTextColor.RED)
     * ```
     *
     * [MiniMessage][net.kyori.adventure.text.minimessage.MiniMessage] strings are also supported
     * via extension functions:
     * ```
     * name by "<red>Fire Sword"
     * ```
     */
    val name: ProviderDslProperty<Component?>
    
    /**
     * The custom name ([DataComponentTypes.CUSTOM_NAME][io.papermc.paper.datacomponent.DataComponentTypes.CUSTOM_NAME]),
     * or `null` to keep the base stack's custom name. Unlike [name], this is the
     * player-visible renamed name (as from an anvil).
     *
     * Defaults to `null`. Can be set to a [Component] or bound to a [Provider]:
     * ```
     * customName by Component.text("My Renamed Sword").decorate(TextDecoration.ITALIC)
     * ```
     *
     * [MiniMessage][net.kyori.adventure.text.minimessage.MiniMessage] strings are also supported
     * via extension functions:
     * ```
     * customName by "<italic>My Renamed Sword"
     * ```
     */
    val customName: ProviderDslProperty<Component?>
    
    /**
     * The item lore lines, or `null` to keep the base stack's lore. Setting this automatically
     * enables the tooltip.
     *
     * Defaults to `null`. Can be set to a list of [Component]s or bound to a [Provider]:
     * ```
     * lore by listOf(
     *     Component.text("Line 1").color(NamedTextColor.GRAY),
     *     Component.text("Line 2").color(NamedTextColor.GRAY),
     * )
     * ```
     *
     * [MiniMessage][net.kyori.adventure.text.minimessage.MiniMessage] string lists are also
     * supported via extension functions:
     * ```
     * lore by listOf("<gray>Line 1", "<gray>Line 2")
     * ```
     */
    val lore: ProviderDslProperty<List<Component>?>
    
    /**
     * Whether the item has an enchantment glint, or `null` to keep the base stack's glint state.
     *
     * Defaults to `null`. Can be set to a static value or bound to a [Provider]:
     * ```
     * hasGlint by true
     * ```
     */
    val hasGlint: ProviderDslProperty<Boolean?>
    
    /**
     * Whether the item shows its tooltip, or `null` to keep the base stack's tooltip state.
     * Automatically set to `true` when [name] or [lore] is set.
     *
     * Defaults to `null`. Can be set to a static value or bound to a [Provider]:
     * ```
     * hasTooltip by false
     * ```
     */
    val hasTooltip: ProviderDslProperty<Boolean?>
    
    /**
     * A set of [DataComponentTypes][DataComponentType] that are hidden in the tooltip.
     * 
     * ```
     * hiddenComponents by setOf(DataComponentTypes.ENCHANTMENTS)
     * ```
     */
    val hiddenComponents: ProviderDslProperty<Set<DataComponentType>?>
    
    /**
     * Access to arbitrary data components beyond the convenience properties.
     *
     * ```
     * data[DataComponentTypes.MAX_DAMAGE] by 500
     * data[DataComponentTypes.FIRE_RESISTANT] by true
     * ```
     *
     * @see DataComponentsPatchDsl
     */
    val data: DataComponentsPatchDsl
    
    /**
     * Access to [PersistentDataContainer] entries.
     * 
     * ```
     * pdc[key("myplugin", "mykey"), PersistentDataType.STRING] by "myvalue"
     * ```
     */
    val pdc: PersistentDataContainerDsl
    
}

/**
 * DSL scope for setting arbitrary data components on an [ItemProvider].
 *
 * Accessed via [ItemProviderDsl.data]. Use the [get] operator with a
 * [DataComponentType] to get a [ProviderDslProperty] for that component:
 *
 * ```
 * itemProvider(ItemType.DIAMOND_SWORD) {
 *     data[DataComponentTypes.MAX_DAMAGE] by 500
 *     data[DataComponentTypes.FIRE_RESISTANT] by true
 * }
 * ```
 */
@ExperimentalDslApi
sealed interface DataComponentsPatchDsl {
    
    /**
     * Returns a [ProviderDslProperty] for the given valued data component type.
     * Set to `null` to leave unchanged, or to a value (or [Provider]) to override:
     * ```
     * data[DataComponentTypes.MAX_DAMAGE] by 500
     * ```
     */
    operator fun <T : Any> get(type: DataComponentType.Valued<T>): ProviderDslProperty<T?>
    
    /**
     * Returns a [ProviderDslProperty] for the given non-valued data component type.
     * Set to `true` to apply, `false` to remove, or `null` to leave unchanged:
     * ```
     * data[DataComponentTypes.FIRE_RESISTANT] by true
     * ```
     */
    operator fun <T : Any> get(type: DataComponentType.NonValued): ProviderDslProperty<Boolean?>
    
}

@Suppress("UNCHECKED_CAST")
@ExperimentalDslApi
internal class DataComponentsPatchDslImpl : DataComponentsPatchDsl {
    
    val components = mutableMapOf<DataComponentType, Provider<*>>()
    
    override fun <T : Any> get(type: DataComponentType.Valued<T>): ProviderDslProperty<T?> =
        ProviderDslProperty<T?>(components[type] as? Provider<T> ?: NULL_PROVIDER) { components[type] = it }
    
    override fun <T : Any> get(type: DataComponentType.NonValued): ProviderDslProperty<Boolean?> =
        ProviderDslProperty<Boolean?>(components[type] as? Provider<Boolean> ?: NULL_PROVIDER) { components[type] = it }
    
}

/**
 * DSL scope for setting [PersistentDataContainer] entries.
 * 
 * Accessed via [ItemProviderDsl.pdc]. Use the [get] operator with a [Key] 
 * and [PersistentDataType] to get a [ProviderDslProperty] for that entry:
 * 
 * ```
 * itemProvider(ItemType.DIAMOND_SWORD) {
 *     pdc[key("myplugin", "mykey"), PersistentDataType.STRING] by "myvalue"
 * }
 * ```
 */
sealed interface PersistentDataContainerDsl {
    
    operator fun <T : Any> get(key: Key, type: PersistentDataType<*, T>): ProviderDslProperty<T?>
    
}

@Suppress("UNCHECKED_CAST")
internal class PersistentDataContainerDslImpl : PersistentDataContainerDsl {
    
    val entries = mutableMapOf<Key, Pair<PersistentDataType<*, *>, Provider<*>>>()
    
    override fun <T : Any> get(key: Key, type: PersistentDataType<*, T>): ProviderDslProperty<T?> =
        ProviderDslProperty<T?>(entries[key]?.second as? Provider<T> ?: NULL_PROVIDER) { entries[key] = type to it }
    
}

@PublishedApi
@ExperimentalDslApi
internal class ItemProviderDslImpl(
    private var _base: Provider<ItemStack>
) : ItemProviderDsl {
    
    private var _type = provider<ItemType?>(null)
    private var _amount = provider<Int?>(null)
    private var _name = provider<Component?>(null)
    private var _customName = provider<Component?>(null)
    private var _lore = provider<List<Component>?>(null)
    private var _hasTooltip = provider<Boolean?>(null)
    private var _hiddenComponents = provider<Set<DataComponentType>?>(null)
    
    override val pdc = PersistentDataContainerDslImpl()
    override val data = DataComponentsPatchDslImpl()
    
    override val base: ProviderDslProperty<ItemStack>
        get() = ProviderDslProperty(::_base)
    override val type: ProviderDslProperty<ItemType?>
        get() = ProviderDslProperty(::_type)
    override val amount: ProviderDslProperty<Int?>
        get() = ProviderDslProperty(::_amount)
    override val name: ProviderDslProperty<Component?>
        get() = ProviderDslProperty(::_name)
    override val customName: ProviderDslProperty<Component?>
        get() = ProviderDslProperty(::_customName)
    override val lore: ProviderDslProperty<List<Component>?>
        get() = ProviderDslProperty(::_lore)
    override val hasTooltip: ProviderDslProperty<Boolean?>
        get() = ProviderDslProperty(::_hasTooltip)
    override val hiddenComponents: ProviderDslProperty<Set<DataComponentType>?>
        get() = ProviderDslProperty(::_hiddenComponents)
    override val hasGlint: ProviderDslProperty<Boolean?>
        get() = data[DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE]
    
    fun build(): Provider<ItemProvider> {
        val dataTypeProviders = data.components.map { (type, dslProperty) -> dslProperty.map { type to it } }
        val pdcProviders = pdc.entries.map { (key, pair) -> pair.second.map { Triple(key, pair.first, it) } }
        return combinedProvider(
            _base, _type, _amount, _name, _customName, _lore, _hasTooltip, _hiddenComponents, combinedProvider(dataTypeProviders), combinedProvider(pdcProviders)
        ) { base, type, amount, name, customName, lore, hasTooltip, hiddenComponents, dataTypes, pdcValues ->
            var result: ItemStack
            if (base.isEmpty && type != null) {
                result = type.createItemStack()
            } else if (type != null) {
                @Suppress("DEPRECATION")
                result = base.clone().withType(type.asMaterial()!!)
            } else {
                result = base.clone()
            }
            
            if (amount != null)
                result.amount = amount
            
            var hasTooltip = hasTooltip
            
            if (name != null) {
                hasTooltip = true
                result.setData(DataComponentTypes.ITEM_NAME, ComponentUtils.withoutPreFormatting(name))
            }
            
            if (customName != null) {
                hasTooltip = true
                result.setData(DataComponentTypes.CUSTOM_NAME, ComponentUtils.withoutPreFormatting(customName))
            }
            
            if (lore != null) {
                hasTooltip = true
                result.setData(DataComponentTypes.LORE, lore(lore.map(ComponentUtils::withoutPreFormatting)))
            }
            
            if (hasTooltip != null) {
                val prev = result.getData(DataComponentTypes.TOOLTIP_DISPLAY)
                val new = tooltipDisplay().apply {
                    hideTooltip(!hasTooltip)
                    // individual hidden components are only needed if tooltip is shown
                    if (hasTooltip) {
                        hiddenComponents(hiddenComponents ?: prev?.hiddenComponents() ?: emptySet())
                    }
                }
                result.setData(DataComponentTypes.TOOLTIP_DISPLAY, new)
            }
            
            for ((type, value) in dataTypes) {
                when (type) {
                    is DataComponentType.Valued<*> -> result.setData(type, value ?: continue)
                    is DataComponentType.NonValued -> {
                        value as Boolean? ?: continue
                        if (value) {
                            result.setData(type)
                        } else {
                            result.unsetData(type)
                        }
                    }
                    
                    else -> throw UnsupportedOperationException()
                }
            }
            
            result.editPersistentDataContainer { pdc ->
                for ((key, dataType, value) in pdcValues) {
                    val key = NamespacedKey(key.namespace(), key.value())
                    if (value != null) {
                        @Suppress("UNCHECKED_CAST")
                        dataType as PersistentDataType<*, Any?>
                        pdc[key, dataType] = value
                    } else {
                        pdc.remove(key)
                    }
                }
            }
            
            ItemWrapper(result)
        }
    }
    
    @Suppress("UNCHECKED_CAST")
    private fun <T : Any> ItemStack.setData(type: DataComponentType.Valued<T>, value: Any) {
        setData(type, value as T)
    }
    
}