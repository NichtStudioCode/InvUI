@file:OptIn(UnstableProviderApi::class)

package xyz.xenondevs.invui.dsl

import io.papermc.paper.datacomponent.DataComponentBuilder
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ItemType
import xyz.xenondevs.commons.provider.Provider
import xyz.xenondevs.commons.provider.UnstableProviderApi
import xyz.xenondevs.commons.provider.dsl.DslProperty
import xyz.xenondevs.invui.item.ItemProvider
import xyz.xenondevs.invui.item.ItemWrapper

/**
 * Sets this [Component]-typed property to a [MiniMessage][MiniMessage] string.
 *
 * ```
 * name by "<red>Fire Sword"
 * ```
 */
@JvmName("componentByString")
@ExperimentalDslApi
infix fun DslProperty<in Component>.by(miniMessage: String): Unit =
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
infix fun DslProperty<in Component>.by(miniMessage: Provider<String>): Unit =
    by(miniMessage.map(MiniMessage.miniMessage()::deserialize))

/**
 * Sets this [Component] list property to a list of [MiniMessage][MiniMessage] strings.
 *
 * ```
 * lore by listOf("<gray>Line 1", "<gray>Line 2")
 * ```
 */
@JvmName("componentListByStringList")
@ExperimentalDslApi
infix fun DslProperty<in List<Component>>.by(miniMessageList: List<String>): Unit =
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
infix fun DslProperty<in List<Component>>.by(miniMessageList: Provider<List<String>>): Unit =
    by(miniMessageList.map { list -> list.map { MiniMessage.miniMessage().deserialize(it) } })

/**
 * Sets this [ItemProvider]-typed property to an [ItemStack].
 *
 * ```
 * background by ItemStack(Material.GRAY_STAINED_GLASS_PANE)
 * ```
 */
@JvmName("itemProviderByItemStack")
@ExperimentalDslApi
infix fun DslProperty<in ItemProvider>.by(itemStack: ItemStack): Unit =
    by(ItemWrapper(itemStack))

/**
 * Binds this [ItemProvider]-typed property to a reactive [Provider] of an [ItemStack].
 *
 * ```
 * background by myItemStackProvider // Provider<ItemStack>
 * ```
 */
@JvmName("itemProviderByItemStackProvider")
@ExperimentalDslApi
infix fun DslProperty<in ItemProvider>.by(itemStack: Provider<ItemStack>): Unit =
    by(itemStack.map(::ItemWrapper))

/**
 * Sets this [ItemProvider]-typed property to an [ItemType].
 *
 * ```
 * background by ItemStack(ItemType.GRAY_STAINED_GLASS_PANE)
 * ```
 */
@JvmName("itemProviderByItemType")
@ExperimentalDslApi
infix fun DslProperty<in ItemProvider>.by(itemType: ItemType): Unit =
    by(ItemWrapper(itemType.createItemStack()))

/**
 * Sets this [ItemProvider]-typed property to a reactive [Provider] of an [ItemType].
 *
 * ```
 * background by myItemTypeProvider // Provider<ItemType>
 * ```
 */
@JvmName("itemProviderByItemTypeProvider")
@ExperimentalDslApi
infix fun DslProperty<in ItemProvider>.by(itemType: Provider<ItemType>): Unit =
    by(itemType.map { ItemWrapper(it.createItemStack()) })

/**
 * Sets this data component property from a [DataComponentBuilder].
 *
 * ```
 * data[DataComponentTypes.LORE] by lore(listOf(Component.text("Line 1")))
 * ```
 */
@JvmName("dataComponentValueByBuilder")
@ExperimentalDslApi
infix fun <T : Any> DslProperty<in T>.by(valueBuilder: DataComponentBuilder<T>): Unit =
    by(valueBuilder.build())

/**
 * Binds this data component property to a reactive [Provider] of [DataComponentBuilder]s.
 *
 * ```
 * data[DataComponentTypes.LORE] by myLoreBuilderProvider // Provider<DataComponentBuilder<ItemLore>>
 * ```
 */
@JvmName("dataComponentValueByBuilderProvider")
@ExperimentalDslApi
infix fun <T : Any> DslProperty<in T>.by(valueBuilder: Provider<DataComponentBuilder<T>>): Unit =
    by(valueBuilder.map(DataComponentBuilder<T>::build))