@file:OptIn(ExperimentalReactiveApi::class)

package xyz.xenondevs.invui.dsl.property

import org.bukkit.inventory.ItemStack
import xyz.xenondevs.commons.provider.Provider
import xyz.xenondevs.invui.ExperimentalReactiveApi
import xyz.xenondevs.invui.dsl.ExperimentalDslApi
import xyz.xenondevs.invui.item.Item
import xyz.xenondevs.invui.item.ItemProvider
import xyz.xenondevs.invui.item.setItemProvider

@ExperimentalDslApi
class ItemDslProperty internal constructor() {
    
    internal var value: Item = Item.EMPTY
    
    infix fun by(item: Item) {
        this.value = item
    }
    
    infix fun by(provider: Provider<ItemProvider>): Unit =
        by(Item.builder().setItemProvider(provider).build())
    
    @JvmName("by1")
    infix fun by(provider: Provider<ItemStack>): Unit =
        by(Item.builder().setItemProvider(provider).build())
    
    infix fun by(itemProvider: ItemProvider): Unit =
        by(Item.simple(itemProvider))
    
    infix fun by(itemProvider: ItemStack): Unit =
        by(Item.simple(itemProvider))
    
}