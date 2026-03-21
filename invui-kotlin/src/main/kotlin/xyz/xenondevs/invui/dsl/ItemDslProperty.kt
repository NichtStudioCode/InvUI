@file:OptIn(ExperimentalReactiveApi::class)

package xyz.xenondevs.invui.dsl

import org.bukkit.inventory.ItemStack
import xyz.xenondevs.commons.provider.Provider
import xyz.xenondevs.invui.ExperimentalReactiveApi
import xyz.xenondevs.invui.item.Item
import xyz.xenondevs.invui.item.ItemProvider
import xyz.xenondevs.invui.item.setItemProvider

/**
 * A DSL property that holds an [Item], used for item slots in trade DSLs.
 *
 * Can be set to an [Item], [ItemProvider], [ItemStack], or a reactive [Provider] of
 * [ItemProvider]/[ItemStack] using the [by] infix function:
 * ```
 * firstInput by ItemStack(Material.EMERALD, 10)
 * result by myItem
 * ```
 */
@ExperimentalDslApi
class ItemDslProperty internal constructor() {
    
    internal var value: Item = Item.EMPTY
    
    /**
     * Sets this property to an [Item].
     *
     * ```
     * firstInput by item {
     *     itemProvider {
     *         name by "<red>Custom Item"
     *         material by Material.DIAMOND
     *     }
     * }
     * ```
     */
    infix fun by(item: Item) {
        this.value = item
    }
    
    /**
     * Sets this property to an [Item] backed by a reactive [Provider] of [ItemProvider]s.
     * The displayed item updates automatically when the provider's value changes.
     *
     * ```
     * firstInput by myItemProviderProvider // Provider<ItemProvider>
     * ```
     */
    infix fun by(provider: Provider<ItemProvider>): Unit =
        by(Item.builder().setItemProvider(provider).build())
    
    /**
     * Sets this property to an [Item] backed by a reactive [Provider] of [ItemStack]s.
     * The displayed item updates automatically when the provider's value changes.
     *
     * ```
     * firstInput by myItemStackProvider // Provider<ItemStack>
     * ```
     */
    @JvmName("by1")
    infix fun by(provider: Provider<ItemStack>): Unit =
        by(Item.builder().setItemProvider(provider).build())
    
    /**
     * Sets this property to a simple [Item] wrapping the given [ItemProvider].
     *
     * ```
     * firstInput by itemProvider {
     *     material by Material.EMERALD
     *     amount by 10
     * }
     * ```
     */
    infix fun by(itemProvider: ItemProvider): Unit =
        by(Item.simple(itemProvider))
    
    /**
     * Sets this property to a simple [Item] wrapping the given [ItemStack].
     *
     * ```
     * firstInput by ItemStack(Material.EMERALD, 10)
     * ```
     */
    infix fun by(itemProvider: ItemStack): Unit =
        by(Item.simple(itemProvider))
    
}