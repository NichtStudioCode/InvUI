package xyz.xenondevs.invui.gui

import xyz.xenondevs.commons.provider.Provider
import xyz.xenondevs.invui.ExperimentalReactiveApi
import xyz.xenondevs.invui.PropertyAdapter
import xyz.xenondevs.invui.inventory.Inventory
import xyz.xenondevs.invui.item.ItemProvider
import xyz.xenondevs.invui.item.simpleOrConstItem

/**
 * Adds an item ingredient for [key] that uses the [ItemProvider] from [provider].
 */
@ExperimentalReactiveApi
fun <S : IngredientMapper<S>> IngredientMapper<S>.addIngredient(key: Char, provider: Provider<ItemProvider>): S =
    addIngredient(key, simpleOrConstItem(provider))

/**
 * Adds an [Inventory] ingredient for [key] that starts at [offset] and uses [background] for empty slots.
 */
@ExperimentalReactiveApi
fun <S : IngredientMapper<S>> IngredientMapper<S>.addIngredient(key: Char, inventory: Inventory, background: Provider<ItemProvider>, offset: Int = 0): S =
    addIngredient(key, inventory, PropertyAdapter(background), offset)