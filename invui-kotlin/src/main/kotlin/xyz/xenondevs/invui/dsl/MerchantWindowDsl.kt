@file:OptIn(ExperimentalReactiveApi::class)

package xyz.xenondevs.invui.dsl

import org.bukkit.entity.Player
import xyz.xenondevs.commons.provider.Provider
import xyz.xenondevs.commons.provider.mutableProvider
import xyz.xenondevs.commons.provider.provider
import xyz.xenondevs.invui.ExperimentalReactiveApi
import xyz.xenondevs.invui.dsl.property.GuiDslProperty
import xyz.xenondevs.invui.dsl.property.ItemDslProperty
import xyz.xenondevs.invui.dsl.property.ProviderDslProperty
import xyz.xenondevs.invui.window.MerchantWindow
import xyz.xenondevs.invui.window.setAvailable
import xyz.xenondevs.invui.window.setDiscount
import xyz.xenondevs.invui.window.setTrades
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Creates a [MerchantWindow] using the DSL.
 *
 * A merchant window displays a villager trading GUI with a 3x1 upper GUI (two input slots and
 * a result slot), a list of [trades][MerchantWindowDsl.trades], and the currently
 * [selected trade][MerchantWindowDsl.selectedTrade].
 *
 * ```
 * val myWindow = merchantWindow(player) {
 *     title by "Merchant"
 *     trades by listOf(
 *         trade {
 *             firstInput by ItemStack(Material.EMERALD)
 *             result by ItemStack(Material.DIAMOND)
 *         },
 *         trade {
 *             firstInput by ItemStack(Material.EMERALD, 5)
 *             secondInput by ItemStack(Material.GOLD_INGOT)
 *             result by ItemStack(Material.NETHERITE_INGOT)
 *             discount by 1
 *         },
 *     )
 * }
 * ```
 *
 * @see MerchantWindowDsl
 * @see trade
 */
@ExperimentalDslApi
inline fun merchantWindow(viewer: Player, merchantWindow: MerchantWindowDsl.() -> Unit): MerchantWindow {
    contract { callsInPlace(merchantWindow, InvocationKind.EXACTLY_ONCE) }
    return MerchantWindowDslImpl(viewer).apply(merchantWindow).build()
}

/**
 * Creates a [MerchantWindow.Trade] using the DSL.
 *
 * Defines the input and output items for a single trade in a [MerchantWindow].
 *
 * ```
 * val myTrade = trade {
 *     firstInput by ItemStack(Material.EMERALD, 10)
 *     secondInput by ItemStack(Material.BOOK)
 *     result by ItemStack(Material.ENCHANTED_BOOK)
 *     discount by 2
 *     isAvailable by true
 * }
 * ```
 *
 * @see TradeDsl
 */
@ExperimentalDslApi
inline fun trade(trade: TradeDsl.() -> Unit): MerchantWindow.Trade {
    contract { callsInPlace(trade, InvocationKind.EXACTLY_ONCE) }
    return TradeDslImpl().apply(trade).build()
}

/**
 * DSL scope for configuring a [MerchantWindow].
 *
 * Extends [SplitWindowDsl] with merchant-specific properties: [trades] to define available
 * trades and [selectedTrade] to observe which trade the player has selected.
 *
 * ```
 * merchantWindow(player) {
 *     title by "Merchant"
 *     trades by listOf(
 *         trade {
 *             firstInput by ItemStack(Material.EMERALD)
 *             result by ItemStack(Material.DIAMOND)
 *         },
 *     )
 * }
 * ```
 */
@ExperimentalDslApi
sealed interface MerchantWindowDsl : SplitWindowDsl {
    
    /**
     * A [Provider] that resolves to the built [MerchantWindow] instance.
     *
     * Can be used to obtain a reference to the window after the DSL block finishes and
     * the window is built. Accessing it before the window is built throws an
     * [IllegalStateException].
     */
    override val window: Provider<MerchantWindow>
    
    /**
     * The upper GUI (3x1) representing the two input slots and the result slot.
     *
     * ```
     * upperGui by gui("a b r") {
     *     'a' by firstInputItem
     *     'b' by secondInputItem
     *     'r' by resultItem
     * }
     * ```
     */
    val upperGui: GuiDslProperty
    
    /**
     * The list of trades available in the merchant GUI.
     *
     * Defaults to an empty list. Can be set to a static value or bound to a [Provider]:
     * ```
     * trades by listOf(
     *     trade {
     *         firstInput by ItemStack(Material.EMERALD)
     *         result by ItemStack(Material.DIAMOND)
     *     },
     * )
     * ```
     *
     * @see trade
     */
    val trades: ProviderDslProperty<List<MerchantWindow.Trade>>
    
    /**
     * A read-only [Provider] that tracks the index of the currently selected trade (zero-based),
     * or `-1` if no trade is selected. Updates automatically as the player selects trades.
     */
    val selectedTrade: Provider<Int>
    
}

/**
 * DSL scope for configuring a [MerchantWindow.Trade].
 *
 * Defines the input items, result, discount, and availability of a single trade.
 *
 * ```
 * val myTrade = trade {
 *     firstInput by ItemStack(Material.EMERALD, 10)
 *     secondInput by ItemStack(Material.BOOK)
 *     result by ItemStack(Material.ENCHANTED_BOOK)
 *     discount by 2
 *     isAvailable by false
 * }
 * ```
 */
@ExperimentalDslApi
@TradeDslMarker
sealed interface TradeDsl {
    
    /**
     * The first (required) input item for the trade.
     *
     * Can be set to an [Item][xyz.xenondevs.invui.item.Item],
     * [ItemProvider][xyz.xenondevs.invui.item.ItemProvider], [ItemStack], or a [Provider] of these:
     * ```
     * firstInput by ItemStack(Material.EMERALD, 10)
     * ```
     */
    val firstInput: ItemDslProperty
    
    /**
     * The optional second input item for the trade.
     *
     * Defaults to [Item.EMPTY][xyz.xenondevs.invui.item.Item.EMPTY]. Can be set to an
     * [Item][xyz.xenondevs.invui.item.Item], [ItemProvider][xyz.xenondevs.invui.item.ItemProvider],
     * [ItemStack], or a [Provider] of these:
     * ```
     * secondInput by ItemStack(Material.BOOK)
     * ```
     */
    val secondInput: ItemDslProperty
    
    /**
     * The result item of the trade.
     *
     * Can be set to an [Item][xyz.xenondevs.invui.item.Item],
     * [ItemProvider][xyz.xenondevs.invui.item.ItemProvider], [ItemStack], or a [Provider] of these:
     * ```
     * result by ItemStack(Material.ENCHANTED_BOOK)
     * ```
     */
    val result: ItemDslProperty
    
    /**
     * The discount applied to the first input's amount. For example, a discount of `2` means
     * the trade requires 2 fewer of the first input item.
     *
     * Defaults to `0`. Can be set to a static value or bound to a [Provider]:
     * ```
     * discount by 2
     * ```
     */
    val discount: ProviderDslProperty<Int>
    
    /**
     * Whether this trade is available for the player to use.
     *
     * Defaults to `true`. Can be set to a static value or bound to a [Provider]:
     * ```
     * isAvailable by false
     * ```
     */
    val isAvailable: ProviderDslProperty<Boolean>
    
}

@PublishedApi
@ExperimentalDslApi
internal class MerchantWindowDslImpl(
    viewer: Player
) : AbstractSplitWindowDsl<MerchantWindow, MerchantWindow.Builder>(viewer), MerchantWindowDsl {
    
    private var _trades = provider(emptyList<MerchantWindow.Trade>())
    
    override val upperGui = GuiDslProperty(3, 1)
    override val trades: ProviderDslProperty<List<MerchantWindow.Trade>>
        get() = ProviderDslProperty(::_trades)
    override val selectedTrade = mutableProvider(-1)
    
    override fun createBuilder() = MerchantWindow.builder()
    
    override fun applyToBuilder(builder: MerchantWindow.Builder) {
        super.applyToBuilder(builder)
        builder.apply {
            setUpperGui(upperGui.value)
            setTrades(_trades)
            addTradeSelectHandler { _, trade -> selectedTrade.set(trade) }
        }
    }
    
}

@PublishedApi
@ExperimentalDslApi
internal class TradeDslImpl : TradeDsl {
    
    private var _discount = provider(0)
    private var _isAvailable = provider(true)
    
    override val firstInput = ItemDslProperty()
    override val secondInput = ItemDslProperty()
    override val result = ItemDslProperty()
    override val discount: ProviderDslProperty<Int>
        get() = ProviderDslProperty(::_discount)
    override val isAvailable: ProviderDslProperty<Boolean>
        get() = ProviderDslProperty(::_isAvailable)
    
    fun build() = MerchantWindow.Trade.builder()
        .setFirstInput(firstInput.value)
        .setSecondInput(secondInput.value)
        .setResult(result.value)
        .setDiscount(_discount)
        .setAvailable(_isAvailable)
        .build()
    
}