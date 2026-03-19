@file:Suppress("UnstableApiUsage")

package xyz.xenondevs.invui.dsl

import io.papermc.paper.datacomponent.DataComponentType
import io.papermc.paper.datacomponent.DataComponentTypes
import io.papermc.paper.datacomponent.item.ItemLore.lore
import io.papermc.paper.datacomponent.item.TooltipDisplay.tooltipDisplay
import net.kyori.adventure.text.Component
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ItemType
import xyz.xenondevs.commons.provider.NULL_PROVIDER
import xyz.xenondevs.commons.provider.Provider
import xyz.xenondevs.commons.provider.combinedProvider
import xyz.xenondevs.commons.provider.provider
import xyz.xenondevs.invui.dsl.property.ProviderDslProperty
import xyz.xenondevs.invui.item.ItemProvider
import xyz.xenondevs.invui.item.ItemWrapper
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@ExperimentalDslApi
inline fun itemProvider(itemProvider: ItemProviderDsl.() -> Unit): Provider<ItemProvider> {
    contract { callsInPlace(itemProvider, InvocationKind.EXACTLY_ONCE) }
    return ItemProviderDslImpl(provider(ItemStack.empty())).apply(itemProvider).build()
}
@ExperimentalDslApi
inline fun itemProvider(base: Provider<ItemStack>, itemProvider: ItemProviderDsl.() -> Unit): Provider<ItemProvider> {
    contract { callsInPlace(itemProvider, InvocationKind.EXACTLY_ONCE) }
    return ItemProviderDslImpl(base).apply(itemProvider).build()
}

@ExperimentalDslApi
inline fun itemProvider(base: ItemStack, itemProvider: ItemProviderDsl.() -> Unit): Provider<ItemProvider> {
    contract { callsInPlace(itemProvider, InvocationKind.EXACTLY_ONCE) }
    return itemProvider(provider(base), itemProvider)
}

@ExperimentalDslApi
inline fun itemProvider(type: ItemType, itemProvider: ItemProviderDsl.() -> Unit): Provider<ItemProvider> {
    contract { callsInPlace(itemProvider, InvocationKind.EXACTLY_ONCE) }
    return itemProvider(provider(type.createItemStack()), itemProvider)
}

@ItemDslMarker
@ExperimentalDslApi
sealed interface ItemProviderDsl {
    
    val base: ProviderDslProperty<ItemStack>
    val type: ProviderDslProperty<ItemType?>
    val amount: ProviderDslProperty<Int?>
    val name: ProviderDslProperty<Component?>
    val customName: ProviderDslProperty<Component?>
    val lore: ProviderDslProperty<List<Component>?>
    val hasGlint: ProviderDslProperty<Boolean?>
    val hasTooltip: ProviderDslProperty<Boolean?>
    
    val data: DataComponentsPatchDsl
    
}

@ItemDslMarker
@ExperimentalDslApi
sealed interface DataComponentsPatchDsl {
    
    operator fun <T : Any> get(type: DataComponentType.Valued<T>): ProviderDslProperty<T?>
    
    operator fun <T : Any> get(type: DataComponentType.NonValued): ProviderDslProperty<Boolean?>
    
}

@Suppress("UNCHECKED_CAST")
@ExperimentalDslApi
internal class DataComponentsPatchImpl : DataComponentsPatchDsl {
    
    val components = mutableMapOf<DataComponentType, Provider<*>>()
    
    override fun <T : Any> get(type: DataComponentType.Valued<T>): ProviderDslProperty<T?> =
        ProviderDslProperty<T?>(components[type] as? Provider<T> ?: NULL_PROVIDER) { components[type] = it }
    
    override fun <T : Any> get(type: DataComponentType.NonValued): ProviderDslProperty<Boolean?> =
        ProviderDslProperty<Boolean?>(components[type] as? Provider<Boolean> ?: NULL_PROVIDER) { components[type] = it }
    
}

@PublishedApi
@ExperimentalDslApi
internal class ItemProviderDslImpl(
    private var _base: Provider<ItemStack>
) : ItemProviderDsl {
    
    private var _type = provider<ItemType?>(null)
    private var _amount = provider<Int?>(null)
    private var _name = provider<Component?>(null)
    private var _lore = provider<List<Component>?>(null)
    private var _hasTooltip = provider<Boolean?>(null)
    
    override val data = DataComponentsPatchImpl()
    
    override val base: ProviderDslProperty<ItemStack>
        get() = ProviderDslProperty(::_base)
    override val type: ProviderDslProperty<ItemType?>
        get() = ProviderDslProperty(::_type)
    override val amount: ProviderDslProperty<Int?>
        get() = ProviderDslProperty(::_amount)
    override val name: ProviderDslProperty<Component?>
        get() = ProviderDslProperty(::_name)
    override val lore: ProviderDslProperty<List<Component>?>
        get() = ProviderDslProperty(::_lore)
    override val hasTooltip: ProviderDslProperty<Boolean?>
        get() = ProviderDslProperty(::_hasTooltip)
    override val customName: ProviderDslProperty<Component?>
        get() = data[DataComponentTypes.CUSTOM_NAME]
    override val hasGlint: ProviderDslProperty<Boolean?>
        get() = data[DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE]
    
    fun build(): Provider<ItemProvider> {
        val dataTypeProviders = data.components.map { (type, dslProperty) -> dslProperty.map { type to it } }
        return combinedProvider(
            _base, _type, _amount, _name, _lore, _hasTooltip, combinedProvider(dataTypeProviders)
        ) { base, type, amount, name, lore, hasTooltip, dataTypes ->
            var result = base.clone()
            var hasTooltip = hasTooltip
            
            @Suppress("DEPRECATION")
            if (type != null)
                result = result.withType(type.asMaterial()!!)
            
            if (amount != null)
                result.amount = amount
            
            if (name != null) {
                hasTooltip = true
                result.setData(DataComponentTypes.ITEM_NAME, name)
            }
            
            if (lore != null) {
                hasTooltip = true
                result.setData(DataComponentTypes.LORE, lore(lore))
            }
            
            if (hasTooltip != null) {
                val prev = result.getData(DataComponentTypes.TOOLTIP_DISPLAY)
                val new = tooltipDisplay().apply {
                    hideTooltip(!hasTooltip)
                    // individual hidden components are only needed if tooltip is shown
                    if (hasTooltip) {
                        hiddenComponents(prev?.hiddenComponents() ?: emptySet())
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
            
            ItemWrapper(result)
        }
    }
    
    @Suppress("UNCHECKED_CAST")
    private fun <T : Any> ItemStack.setData(type: DataComponentType.Valued<T>, value: Any) {
        setData(type, value as T)
    }
    
}