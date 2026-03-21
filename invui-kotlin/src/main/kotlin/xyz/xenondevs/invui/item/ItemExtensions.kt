@file:OptIn(ExperimentalTypeInference::class)

package xyz.xenondevs.invui.item

import org.bukkit.inventory.ItemStack
import xyz.xenondevs.commons.provider.Provider
import xyz.xenondevs.invui.ExperimentalReactiveApi
import kotlin.experimental.ExperimentalTypeInference

/**
 * Calls [Item.notifyWindows] for all items in this [Iterable].
 */
fun Iterable<Item>.notifyWindows() = forEach { it.notifyWindows() }

/**
 * Sets the [provider] containing the [ItemProvider] for the [Item] built by this builder.
 */
@ExperimentalReactiveApi
fun <S : Item.Builder<*>> S.setItemProvider(
    provider: Provider<ItemProvider>
): S {
    addModifier { item -> provider.observeWeak(item) { weakItem -> weakItem.notifyWindows() } }
    setItemProvider { provider.get() }
    return this
}

/**
 * Sets the [provider] containing the [ItemStack] for the [Item] built by this builder.
 * (Shortcut for `setItemProvider(provider.map(::ItemWrapper))`)
 */
@JvmName("setItemProvider1")
@ExperimentalReactiveApi
fun <S : Item.Builder<*>> S.setItemProvider(
    provider: Provider<ItemStack>
): S = setItemProvider(provider.map(::ItemWrapper))