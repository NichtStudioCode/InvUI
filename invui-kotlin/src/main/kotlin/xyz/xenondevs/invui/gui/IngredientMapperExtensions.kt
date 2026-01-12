package xyz.xenondevs.invui.gui

import xyz.xenondevs.commons.provider.Provider
import xyz.xenondevs.invui.ExperimentalReactiveApi
import xyz.xenondevs.invui.PropertyAdapter
import xyz.xenondevs.invui.inventory.Inventory
import xyz.xenondevs.invui.item.Item
import xyz.xenondevs.invui.item.ItemProvider
import xyz.xenondevs.invui.item.setItemProvider

@ExperimentalReactiveApi
fun <S : IngredientMapper<S>> IngredientMapper<S>.addIngredient(key: Char, provider: Provider<ItemProvider>): S =
    addIngredient(key, Item.builder().setItemProvider(provider).build())

@ExperimentalReactiveApi
fun <S : IngredientMapper<S>> IngredientMapper<S>.addIngredient(key: Char, inventory: Inventory, background: Provider<ItemProvider>, offset: Int = 0): S =
    addIngredient(key, inventory, PropertyAdapter(background), offset)