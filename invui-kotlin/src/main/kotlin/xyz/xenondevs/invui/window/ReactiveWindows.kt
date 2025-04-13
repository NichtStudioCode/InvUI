package xyz.xenondevs.invui.window

import net.kyori.adventure.text.Component
import xyz.xenondevs.commons.provider.MutableProvider
import xyz.xenondevs.commons.provider.Provider
import xyz.xenondevs.commons.provider.combinedProvider
import xyz.xenondevs.commons.provider.map
import xyz.xenondevs.invui.ExperimentalReactiveApi

@Suppress("UNCHECKED_CAST")
@ExperimentalReactiveApi
fun <S : Window.Builder<*, *>> S.setTitle(provider: Provider<Component>): S {
    addModifier { window -> provider.observeWeak(window) { weakWindow -> weakWindow.updateTitle() } }
    return setTitleSupplier(provider) as S
}

@Suppress("UNCHECKED_CAST")
@ExperimentalReactiveApi
fun <S : AnvilWindow.Builder> S.addRenameHandler(handler: MutableProvider<String>): S {
    return addRenameHandler(handler::set) as S
}

@ExperimentalReactiveApi
fun <S : StonecutterWindow.Builder> S.setSelectedSlot(provider: MutableProvider<Int>): S {
    addModifier { window ->
        window.selectedSlot = provider.get()
        window.addSelectedSlotChangeHandler { _, to -> provider.set(to) }
        provider.subscribeWeak(window) { weakWindow, slot -> weakWindow.selectedSlot = slot }
    }
    return this
}

@ExperimentalReactiveApi
fun <S : StonecutterWindow.Builder> S.setSelectedSlot(provider: Provider<Int>): S {
    addModifier { window ->
        window.selectedSlot = provider.get()
        provider.subscribeWeak(window) { weakWindow, slot -> weakWindow.selectedSlot = slot }
    }
    return this
}

@ExperimentalReactiveApi
fun <S : MerchantWindow.Builder> S.setTrades(trades: Provider<List<MerchantWindow.Trade>>): S {
    addModifier { window -> trades.observeWeak(window) { window -> window.updateTrades() } }
    setTradesSupplier(trades)
    return this
}

@ExperimentalReactiveApi
fun <S : MerchantWindow.Builder> S.setLevel(level: Provider<Int>): S {
    addModifier { window -> level.observeWeak(window) { window -> window.updateTrades() } }
    setLevelSupplier(level)
    return this
}

@ExperimentalReactiveApi
fun <S : MerchantWindow.Builder> S.setProgress(progress: Provider<Double>): S {
    addModifier { window -> progress.observeWeak(window) { window -> window.updateTrades() } }
    setProgressSupplier(progress)
    return this
}

@ExperimentalReactiveApi
fun <S : MerchantWindow.Builder> S.setRestockMessageEnabled(restockMessageEnabled: Provider<Boolean>): S {
    addModifier { window -> restockMessageEnabled.observeWeak(window) { window -> window.updateTrades() } }
    setRestockMessageEnabledSupplier(restockMessageEnabled)
    return this
}

@ExperimentalReactiveApi
fun <S : MerchantWindow.Trade.Builder> S.setDiscount(discount: Provider<Int>): S {
    addModifier { trade -> discount.observeWeak(trade) { trade -> trade.notifyWindows() } }
    setDiscountSupplier(discount)
    return this
}

@ExperimentalReactiveApi
fun <S : MerchantWindow.Trade.Builder> S.setAvailable(available: Provider<Boolean>): S {
    addModifier { trade -> available.observeWeak(trade) { trade -> trade.notifyWindows() } }
    setAvailableSupplier(available)
    return this
}