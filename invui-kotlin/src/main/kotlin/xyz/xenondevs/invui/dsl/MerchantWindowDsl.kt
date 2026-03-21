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
import xyz.xenondevs.invui.window.setLevel
import xyz.xenondevs.invui.window.setProgress
import xyz.xenondevs.invui.window.setRestockMessageEnabled
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
 * Extends [SplitWindowDsl] with merchant-specific properties like [trades] to define available
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
     * The merchant's level, displayed after the [title][WindowDsl.title] using the translation
     * `merchant.level.<level>`. The following levels exist:
     * 1 (Novice), 2 (Apprentice), 3 (Journeyman), 4 (Expert), 5 (Master).
     *
     * If set to `<= 0`, no level name and an always-empty progress bar will be displayed.
     * If set to `> 5`, no level name and no progress bar will be displayed.
     *
     * Defaults to `0`. Can be set to a static value or bound to a [Provider]:
     * ```
     * level by 3 // Journeyman
     * ```
     */
    val level: ProviderDslProperty<Int>

    /**
     * The progress of the merchant's experience bar, from `0.0` to `1.0`.
     * If set to any value `< 0`, the progress bar and the merchant level name will be hidden.
     *
     * Defaults to `-1.0` (hidden). Can be set to a static value or bound to a [Provider]:
     * ```
     * progress by 0.5
     * ```
     */
    val progress: ProviderDslProperty<Double>

    /**
     * Whether the message "Villagers restock up to two times per day" is displayed when hovering
     * over the arrow of disabled trades.
     *
     * Defaults to `false`. Can be set to a static value or bound to a [Provider]:
     * ```
     * restockMessageEnabled by true
     * ```
     */
    val restockMessageEnabled: ProviderDslProperty<Boolean>
    
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
    private var _level = provider(0)
    private var _progress = provider(-1.0)
    private var _restockMessageEnabled = provider(false)
    
    override val upperGui = GuiDslProperty(3, 1)
    override val trades: ProviderDslProperty<List<MerchantWindow.Trade>>
        get() = ProviderDslProperty(::_trades)
    override val level: ProviderDslProperty<Int>
        get() = ProviderDslProperty(::_level)
    override val progress: ProviderDslProperty<Double>
        get() = ProviderDslProperty(::_progress)
    override val restockMessageEnabled: ProviderDslProperty<Boolean>
        get() = ProviderDslProperty(::_restockMessageEnabled)
    override val selectedTrade = mutableProvider(-1)
    
    override fun createBuilder() = MerchantWindow.builder()
    
    override fun applyToBuilder(builder: MerchantWindow.Builder) {
        super.applyToBuilder(builder)
        builder.apply {
            setUpperGui(upperGui.value)
            setTrades(_trades)
            setLevel(_level)
            setProgress(_progress)
            setRestockMessageEnabled(_restockMessageEnabled)
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