package xyz.xenondevs.invui.dsl.property

import org.bukkit.inventory.ItemStack
import xyz.xenondevs.commons.provider.Provider
import xyz.xenondevs.commons.provider.mapNonNull
import xyz.xenondevs.invui.dsl.ExperimentalDslApi
import xyz.xenondevs.invui.item.ItemProvider
import xyz.xenondevs.invui.item.ItemWrapper

@ExperimentalDslApi
class ItemProviderDslProperty internal constructor() : ProviderDslProperty<ItemProvider>(ItemProvider.EMPTY) {
    
    infix fun by(itemStack: ItemStack): Unit =
        by(ItemWrapper(itemStack))
    
    @JvmName("by1")
    infix fun by(provider: Provider<ItemStack>): Unit =
        by(provider.map(::ItemWrapper))
    
}

@ExperimentalDslApi
class NullableItemProviderDslProperty internal constructor() : ProviderDslProperty<ItemProvider?>(null) {
    
    infix fun by(itemStack: ItemStack?): Unit =
        by(itemStack?.let(::ItemWrapper))
    
    @JvmName("by1")
    infix fun by(provider: Provider<ItemStack?>): Unit =
        by(provider.mapNonNull(::ItemWrapper))
    
}