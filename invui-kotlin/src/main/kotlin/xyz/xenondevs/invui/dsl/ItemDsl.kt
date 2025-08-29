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

@ExperimentalDslApi
class BundleSelect internal constructor(val player: Player, val bundleSlot: Int)

@ItemDslMarker
@ExperimentalDslApi
sealed interface ItemDsl {
    
    val itemProvider: ItemProviderDslProperty
    
    fun onClick(handler: Click.() -> Unit)
    
    fun onBundleSelect(handler: BundleSelect.() -> Unit)
    
}

@ExperimentalDslApi
internal class ItemDslImpl : ItemDsl {
    
    override val itemProvider = ItemProviderDslProperty()
    private val clickHandlers = mutableListOf<Click.() -> Unit>()
    private val bundleSelectHandlers = mutableListOf<BundleSelect.() -> Unit>()
    
    override fun onClick(handler: Click.() -> Unit) {
        clickHandlers += handler
    }
    
    override fun onBundleSelect(handler: BundleSelect.() -> Unit) {
        bundleSelectHandlers += handler
    }
    
    fun build() = Item.builder().apply {
        setItemProvider(itemProvider)
        clickHandlers.forEach { addClickHandler(it) }
        bundleSelectHandlers.forEach { handler -> addBundleSelectHandler { player, bundleSlot -> BundleSelect(player, bundleSlot).handler() }}
    }.build()
    
}