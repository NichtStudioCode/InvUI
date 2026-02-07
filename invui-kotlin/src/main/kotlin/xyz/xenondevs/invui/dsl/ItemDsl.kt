@file:OptIn(ExperimentalReactiveApi::class)

package xyz.xenondevs.invui.dsl

import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import xyz.xenondevs.commons.provider.Provider
import xyz.xenondevs.commons.provider.provider
import xyz.xenondevs.invui.Click
import xyz.xenondevs.invui.ExperimentalReactiveApi
import xyz.xenondevs.invui.dsl.property.ProviderDslProperty
import xyz.xenondevs.invui.item.AbstractItem
import xyz.xenondevs.invui.item.Item
import xyz.xenondevs.invui.item.ItemProvider
import xyz.xenondevs.invui.item.simpleOrConstItem
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@ExperimentalDslApi
inline fun item(item: ItemDsl.() -> Unit): Item {
    contract { callsInPlace(item, InvocationKind.EXACTLY_ONCE) }
    return ItemDslImpl().apply(item).build()
}

@ItemDslMarker
@ExperimentalDslApi
sealed interface ClickDsl {
    
    val clickType: ClickType
    val player: Player
    val hotbarButton: Int
    
}

@ItemDslMarker
@ExperimentalDslApi
sealed interface BundleSelectDsl {
    
    val player: Player
    val bundleSlot: Int
    
}

@ItemDslMarker
@ExperimentalDslApi
sealed interface ItemDsl {
    
    val itemProvider: ProviderDslProperty<ItemProvider>
    
    fun onClick(handler: ClickDsl.() -> Unit)
    
    fun onBundleSelect(handler: BundleSelectDsl.() -> Unit)
    
}

@PublishedApi
@ExperimentalDslApi
internal class ItemDslImpl : ItemDsl {
    
    private var _itemProvider = provider(ItemProvider.EMPTY)
    
    override val itemProvider: ProviderDslProperty<ItemProvider>
        get() = ProviderDslProperty(::_itemProvider)
    private val clickHandlers = mutableListOf<ClickDsl.() -> Unit>()
    private val bundleSelectHandlers = mutableListOf<BundleSelectDsl.() -> Unit>()
    
    override fun onClick(handler: ClickDsl.() -> Unit) {
        clickHandlers += handler
    }
    
    override fun onBundleSelect(handler: BundleSelectDsl.() -> Unit) {
        bundleSelectHandlers += handler
    }
    
    fun build(): Item {
        if (clickHandlers.isEmpty() && bundleSelectHandlers.isEmpty())
            return simpleOrConstItem(_itemProvider)
        return DslItemImpl(_itemProvider, clickHandlers.toList(), bundleSelectHandlers.toList())
    }
    
}

@ExperimentalDslApi
internal class ClickDslImpl(
    override val clickType: ClickType,
    override val player: Player,
    override val hotbarButton: Int
) : ClickDsl

@ExperimentalDslApi
internal class BundleSelectDslImpl(
    override val player: Player,
    override val bundleSlot: Int
) : BundleSelectDsl

@ExperimentalDslApi
private class DslItemImpl(
    private val itemProvider: Provider<ItemProvider>,
    private val clickHandlers: List<ClickDsl.() -> Unit>,
    private val bundleSelectHandlers: List<BundleSelectDsl.() -> Unit>
) : AbstractItem() {
    
    init {
        itemProvider.observeWeak(this) { thisRef -> thisRef.notifyWindows() }
    }
    
    override fun getItemProvider(viewer: Player) = itemProvider.get()
    
    override fun handleClick(clickType: ClickType, player: Player, click: Click) {
        val cd = ClickDslImpl(clickType, player, click.hotbarButton)
        clickHandlers.forEach { it(cd) }
    }
    
    override fun handleBundleSelect(player: Player, bundleSlot: Int) {
        val bsd = BundleSelectDslImpl(player, bundleSlot)
        bundleSelectHandlers.forEach { it(bsd) }
    }
    
    
}