@file:OptIn(UnstableProviderApi::class)

package xyz.xenondevs.invui.dsl

import io.papermc.paper.datacomponent.DataComponentBuilder
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.inventory.ItemStack
import xyz.xenondevs.commons.provider.MutableProvider
import xyz.xenondevs.commons.provider.Provider
import xyz.xenondevs.commons.provider.UnstableProviderApi
import xyz.xenondevs.commons.provider.mutableProvider
import xyz.xenondevs.commons.provider.provider
import xyz.xenondevs.invui.item.ItemProvider
import xyz.xenondevs.invui.item.ItemWrapper
import kotlin.reflect.KMutableProperty0

/**
 * Sets this [Component]-typed property from a [MiniMessage][MiniMessage] string.
 *
 * ```
 * name by "<red>Fire Sword"
 * ```
 */
@JvmName("componentByString")
@ExperimentalDslApi
infix fun ProviderDslProperty<in Component>.by(miniMessage: String): Unit =
    by(MiniMessage.miniMessage().deserialize(miniMessage))

/**
 * Binds this [Component]-typed property to a reactive [Provider] of
 * [MiniMessage][MiniMessage] strings.
 *
 * ```
 * name by myStringProvider // Provider<String>
 * ```
 */
@JvmName("componentByStringProvider")
@ExperimentalDslApi
infix fun ProviderDslProperty<in Component>.by(miniMessage: Provider<String>): Unit =
    by(miniMessage.map(MiniMessage.miniMessage()::deserialize))

/**
 * Sets this [Component] list property from a list of [MiniMessage][MiniMessage] strings.
 *
 * ```
 * lore by listOf("<gray>Line 1", "<gray>Line 2")
 * ```
 */
@JvmName("componentListByStringList")
@ExperimentalDslApi
infix fun ProviderDslProperty<in List<Component>>.by(miniMessageList: List<String>): Unit =
    by(miniMessageList.map { MiniMessage.miniMessage().deserialize(it) })

/**
 * Binds this [Component] list property to a reactive [Provider] of
 * [MiniMessage][MiniMessage] string lists.
 *
 * ```
 * lore by myStringListProvider // Provider<List<String>>
 * ```
 */
@JvmName("componentListByStringListProvider")
@ExperimentalDslApi
infix fun ProviderDslProperty<in List<Component>>.by(miniMessageList: Provider<List<String>>): Unit =
    by(miniMessageList.map { list -> list.map { MiniMessage.miniMessage().deserialize(it) } })

/**
 * Sets this [ItemProvider]-typed property from an [ItemStack].
 *
 * ```
 * background by ItemStack(Material.GRAY_STAINED_GLASS_PANE)
 * ```
 */
@JvmName("itemProviderByItemStack")
@ExperimentalDslApi
infix fun ProviderDslProperty<in ItemProvider>.by(itemStack: ItemStack): Unit =
    by(ItemWrapper(itemStack))

/**
 * Binds this [ItemProvider]-typed property to a reactive [Provider] of [ItemStack]s.
 *
 * ```
 * background by myItemStackProvider // Provider<ItemStack>
 * ```
 */
@JvmName("itemProviderByItemStackProvider")
@ExperimentalDslApi
infix fun ProviderDslProperty<in ItemProvider>.by(itemStack: Provider<ItemStack>): Unit =
    by(itemStack.map(::ItemWrapper))

/**
 * Sets this data component property from a [DataComponentBuilder].
 *
 * ```
 * data[DataComponentTypes.LORE] by lore(listOf(Component.text("Line 1")))
 * ```
 */
@Suppress("UnstableApiUsage")
@JvmName("dataComponentValueByBuilder")
@ExperimentalDslApi
infix fun <T : Any> ProviderDslProperty<in T>.by(valueBuilder: DataComponentBuilder<T>): Unit =
    by(valueBuilder.build())

/**
 * Binds this data component property to a reactive [Provider] of [DataComponentBuilder]s.
 *
 * ```
 * data[DataComponentTypes.LORE] by myLoreBuilderProvider // Provider<DataComponentBuilder<ItemLore>>
 * ```
 */
@Suppress("UnstableApiUsage")
@JvmName("dataComponentValueByBuilderProvider")
@ExperimentalDslApi
infix fun <T : Any> ProviderDslProperty<in T>.by(valueBuilder: Provider<DataComponentBuilder<T>>): Unit =
    by(valueBuilder.map(DataComponentBuilder<T>::build))

/**
 * Sets this [List] property to a single value.
 * ```
 * lore by Component.text("Single line lore")
 * ```
 */
@JvmName("listByValue")
@ExperimentalDslApi
infix fun <T : Any> ProviderDslProperty<in List<T>>.by(value: T): Unit =
    by(listOf(value))

/**
 * Sets this [List] property to a provider of a single value.
 * ```
 * lore by provider(Component.text("Single line lore"))
 * ```
 */
@JvmName("listByValueProvider")
@ExperimentalDslApi
infix fun <T : Any> ProviderDslProperty<in List<T>>.by(value: Provider<T>): Unit =
    by(value.map(::listOf))

/**
 * A DSL property backed by a [Provider]. Can be set to a static value or bound to a reactive
 * [Provider] using the [by] infix function.
 *
 * Used throughout the DSL for configurable properties like backgrounds, titles, and progress
 * values:
 * ```
 * // static value
 * background by ItemStack(Material.GRAY_STAINED_GLASS_PANE)
 *
 * // reactive provider
 * background by myItemStackProvider
 * ```
 */
@ExperimentalDslApi
class ProviderDslProperty<T> internal constructor(
    override val delegate: Provider<T>,
    private val set: (Provider<T>) -> Unit
) : Provider<T> by delegate {
    
    internal constructor(
        field: KMutableProperty0<Provider<T>>
    ) : this(field.get(), field::set)
    
    internal constructor(
        initial: T,
        set: (Provider<T>) -> Unit
    ) : this(provider(initial), set)
    
    /**
     * Binds this property to a reactive [Provider]. The property will update automatically
     * when the provider's value changes.
     */
    infix fun by(provider: Provider<T>) {
        set(provider.delegate)
    }
    
    /**
     * Sets this property to a static value.
     */
    infix fun by(value: T) {
        by(provider(value))
    }
    
    override fun equals(other: Any?): Boolean = other is Provider<*> && other.delegate === delegate
    override fun hashCode(): Int = System.identityHashCode(delegate)
    
}

/**
 * A DSL property backed by a [MutableProvider]. Can be set to a static value or bound to a
 * reactive [MutableProvider] using the [by] infix function.
 *
 * Used for properties like page index or selected tab that can change at runtime:
 * ```
 * // static initial value
 * page by 0
 *
 * // reactive mutable provider (can be changed later)
 * val pageProvider = mutableProvider(0)
 * page by pageProvider
 * ```
 */
@ExperimentalDslApi
class MutableProviderDslProperty<T> private constructor(
    override val delegate: MutableProvider<T>,
    private val set: (MutableProvider<T>) -> Unit
) : MutableProvider<T> by delegate {
    
    internal constructor(
        field: KMutableProperty0<MutableProvider<T>>
    ) : this(field.get(), field::set)
    
    /**
     * Binds this property to a reactive [MutableProvider]. The property will update automatically
     * when the provider's value changes, and changes to the property will propagate back.
     */
    infix fun by(provider: MutableProvider<T>) {
        set(provider.delegate)
    }
    
    /**
     * Sets this property to a static value.
     */
    infix fun by(value: T) {
        by(mutableProvider(value))
    }
    
    override fun equals(other: Any?): Boolean = other is Provider<*> && other.delegate === delegate
    override fun hashCode(): Int = System.identityHashCode(delegate)
    
}