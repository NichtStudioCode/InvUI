@file:OptIn(ExperimentalReactiveApi::class)

package xyz.xenondevs.invui.dsl

import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import xyz.xenondevs.commons.provider.Provider
import xyz.xenondevs.invui.Click
import xyz.xenondevs.invui.ExperimentalReactiveApi
import xyz.xenondevs.invui.dsl.property.ItemProviderDslProperty
import xyz.xenondevs.invui.item.AbstractItem
import xyz.xenondevs.invui.item.Item
import xyz.xenondevs.invui.item.ItemProvider

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
    
    fun build(): Item =
        DslItemImpl(itemProvider, clickHandlers.toList(), bundleSelectHandlers.toList())
    
}

@ExperimentalDslApi
private class DslItemImpl(
    private val itemProvider: Provider<ItemProvider>,
    private val clickHandlers: List<Click.() -> Unit>,
    private val bundleSelectHandlers: List<BundleSelect.() -> Unit>
) : AbstractItem() {
    
    init {
        itemProvider.observeWeak(this) { thisRef -> thisRef.notifyWindows() }
    }
    
    override fun getItemProvider(viewer: Player) = itemProvider.get()
    
    override fun handleClick(clickType: ClickType, player: Player, click: Click) {
        clickHandlers.forEach { it(click) }
    }
    
    override fun handleBundleSelect(player: Player, bundleSlot: Int) {
        val bs = BundleSelect(player, bundleSlot)
        bundleSelectHandlers.forEach { it(bs) }
    }
    
    
}