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

@OverloadResolutionByLambdaReturnType
@ExperimentalReactiveApi
fun <S : Item.Builder<*>, T> S.setItemProvider(provider: Provider<T>, transform: (T) -> ItemProvider) =
    setItemProvider(provider.map(transform))

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
fun <S : Item.Builder<*>, T> S.setItemProvider(provider: Provider<T>, transform: (T) -> ItemStack) =
    setItemProvider(provider.map(transform))

@JvmName("setItemProvider1")
@ExperimentalReactiveApi
fun <S : Item.Builder<*>> S.setItemProvider(
    provider: Provider<ItemStack>
): S = setItemProvider(provider.map(::ItemWrapper))