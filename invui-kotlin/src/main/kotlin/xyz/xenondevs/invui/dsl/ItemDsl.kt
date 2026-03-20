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

/**
 * Creates an [Item] using the DSL.
 *
 * The item's appearance is configured via [itemProvider][ItemDsl.itemProvider], and click/bundle
 * interactions are handled via [onClick][ItemDsl.onClick] and
 * [onBundleSelect][ItemDsl.onBundleSelect].
 *
 * ```
 * val myItem = item {
 *     itemProvider by itemProvider(ItemType.DIAMOND) {
 *         name by "<aqua>Click me!"
 *         lore by listOf("<gray>Left or right click")
 *     }
 *
 *     onClick {
 *         if (clickType.isLeftClick) {
 *             player.sendMessage("Left clicked!")
 *         }
 *     }
 * }
 * ```
 *
 * @see ItemDsl
 */
@ExperimentalDslApi
inline fun item(item: ItemDsl.() -> Unit): Item {
    contract { callsInPlace(item, InvocationKind.EXACTLY_ONCE) }
    return ItemDslImpl().apply(item).build()
}

/**
 * DSL scope available inside [ItemDsl.onClick] handlers, providing information about the click event.
 *
 * ```
 * onClick {
 *     when {
 *         clickType.isLeftClick -> player.sendMessage("Left click!")
 *         clickType.isRightClick -> player.sendMessage("Right click!")
 *     }
 * }
 * ```
 */
@ItemDslMarker
@ExperimentalDslApi
sealed interface ClickDsl {
    
    /** The type of click performed (left, right, shift, etc.). */
    val clickType: ClickType
    
    /** The player who clicked. */
    val player: Player
    
    /** The hotbar button pressed, or `-1` if no hotbar button was involved. */
    val hotbarButton: Int
    
}

/**
 * DSL scope available inside [ItemDsl.onBundleSelect] handlers, providing information about
 * a bundle slot selection event.
 */
@ItemDslMarker
@ExperimentalDslApi
sealed interface BundleSelectDsl {
    
    /** The player who selected the bundle slot. */
    val player: Player
    
    /** The index of the selected slot within the bundle. */
    val bundleSlot: Int
    
}

/**
 * DSL scope for configuring an [Item].
 *
 * Use [itemProvider] to define the item's appearance and [onClick] / [onBundleSelect] to handle
 * interactions. 
 *
 * ```
 * val myItem = item {
 *     itemProvider by itemProvider(ItemType.COMPASS) {
 *         name by "<yellow>Navigation"
 *     }
 *
 *     onClick {
 *         player.sendMessage("You clicked the compass!")
 *     }
 * }
 * ```
 */
@ItemDslMarker
@ExperimentalDslApi
sealed interface ItemDsl {
    
    /**
     * The [ItemProvider] that determines the item's appearance.
     *
     * Defaults to [ItemProvider.EMPTY]. Can be set to a static value or bound to a [Provider]:
     * ```
     * itemProvider by ItemStack(Material.DIAMOND)
     * ```
     *
     * Or use the [itemProvider DSL][itemProvider] for reactive item configuration:
     * ```
     * itemProvider by itemProvider(ItemType.DIAMOND_SWORD) {
     *     name by "<red>Fire Sword"
     * }
     * ```
     *
     * @see itemProvider
     */
    val itemProvider: ProviderDslProperty<ItemProvider>
    
    /**
     * Registers a click handler that is called when a player clicks this item.
     * Multiple handlers can be registered and will all be called in order.
     *
     * ```
     * onClick {
     *     if (clickType.isLeftClick) {
     *         player.sendMessage("Left clicked!")
     *     }
     * }
     * ```
     *
     * @see ClickDsl
     */
    fun onClick(handler: ClickDsl.() -> Unit)
    
    /**
     * Registers a bundle selection handler that is called when a player selects a slot
     * in this item's bundle tooltip. Multiple handlers can be registered and will all be
     * called in order.
     *
     * ```
     * onBundleSelect {
     *     player.sendMessage("Selected bundle slot $bundleSlot")
     * }
     * ```
     *
     * @see BundleSelectDsl
     */
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