@file:OptIn(ExperimentalReactiveApi::class)

package xyz.xenondevs.invui.dsl

import org.bukkit.entity.Player
import xyz.xenondevs.invui.Click
import xyz.xenondevs.invui.ExperimentalReactiveApi
import xyz.xenondevs.invui.dsl.property.ItemProviderDslProperty
import xyz.xenondevs.invui.item.Item
import xyz.xenondevs.invui.item.setItemProvider

@ExperimentalDslApi
fun item(item: ItemDsl.() -> Unit): Item =
    ItemDslImpl().apply(item).build()

@ItemDslMarker
@ExperimentalDslApi
sealed interface ItemDsl {
    
    val itemProvider: ItemProviderDslProperty
    
    fun onClick(handler: (click: Click) -> Unit)
    
    fun onBundleSelect(handler: (player: Player, slot: Int) -> Unit)
    
}

@ExperimentalDslApi
internal class ItemDslImpl : ItemDsl {
    
    override val itemProvider = ItemProviderDslProperty()
    private val clickHandlers = mutableListOf<(Click) -> Unit>()
    private val bundleSelectHandlers = mutableListOf<(Player, Int) -> Unit>()
    
    override fun onClick(handler: (click: Click) -> Unit) {
        clickHandlers += handler
    }
    
    override fun onBundleSelect(handler: (player: Player, slot: Int) -> Unit) {
        bundleSelectHandlers += handler
    }
    
    fun build() = Item.builder().apply {
        setItemProvider(itemProvider.delegate)
        clickHandlers.forEach { addClickHandler(it) }
        bundleSelectHandlers.forEach { addBundleSelectHandler(it) }
    }.build()
    
}