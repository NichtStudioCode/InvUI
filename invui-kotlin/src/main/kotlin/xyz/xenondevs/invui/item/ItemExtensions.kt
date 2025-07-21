package xyz.xenondevs.invui.item

import org.bukkit.inventory.ItemStack
import xyz.xenondevs.commons.provider.Provider
import xyz.xenondevs.invui.ExperimentalReactiveApi

/**
 * Calls [Item.notifyWindows] for all items in this [Iterable].
 */
fun Iterable<Item>.notifyWindows() = forEach { it.notifyWindows() }

@ExperimentalReactiveApi
fun <S : Item.Builder<*>> S.setItemProvider(
    provider: Provider<ItemProvider>
): S {
    addModifier { item -> provider.observeWeak(item) { weakItem -> weakItem.notifyWindows() } }
    setItemProvider { provider.get() }
    return this
}

@JvmName("setItemProvider1")
@ExperimentalReactiveApi
fun <S : Item.Builder<*>> S.setItemProvider(
    provider: Provider<ItemStack>
): S = setItemProvider(provider.map(::ItemWrapper))