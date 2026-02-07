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

@ExperimentalDslApi
fun merchantWindow(viewer: Player, merchantWindow: MerchantWindowDsl.() -> Unit): MerchantWindow =
    MerchantWindowDslImpl(viewer).apply(merchantWindow).build()

@ExperimentalDslApi
fun trade(trade: TradeDsl.() -> Unit): MerchantWindow.Trade =
    TradeDslImpl().apply(trade).build()

@ExperimentalDslApi
sealed interface MerchantWindowDsl : SplitWindowDsl {
    
    val upperGui: GuiDslProperty
    val trades: ProviderDslProperty<List<MerchantWindow.Trade>>
    val selectedTrade: Provider<Int>
    
}

@ExperimentalDslApi
@TradeDslMarker
sealed interface TradeDsl {
    
    val firstInput: ItemDslProperty
    val secondInput: ItemDslProperty
    val result: ItemDslProperty
    val discount: ProviderDslProperty<Int>
    val isAvailable: ProviderDslProperty<Boolean>
    
}

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