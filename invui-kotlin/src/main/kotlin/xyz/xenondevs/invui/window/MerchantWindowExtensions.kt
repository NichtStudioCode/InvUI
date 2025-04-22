package xyz.xenondevs.invui.window

import xyz.xenondevs.commons.provider.Provider
import xyz.xenondevs.invui.ExperimentalReactiveApi
import xyz.xenondevs.invui.PropertyAdapter

@ExperimentalReactiveApi
fun MerchantWindow.Builder.setTrades(trades: Provider<List<MerchantWindow.Trade>>): MerchantWindow.Builder =
    setTrades(PropertyAdapter(trades))

@ExperimentalReactiveApi
fun MerchantWindow.Builder.setLevel(level: Provider<Int>): MerchantWindow.Builder =
    setLevel(PropertyAdapter(level))

@ExperimentalReactiveApi
fun MerchantWindow.Builder.setProgress(progress: Provider<Double>): MerchantWindow.Builder =
    setProgress(PropertyAdapter(progress))

@ExperimentalReactiveApi
fun MerchantWindow.Builder.setRestockMessageEnabled(restockMessageEnabled: Provider<Boolean>): MerchantWindow.Builder =
    setRestockMessageEnabled(PropertyAdapter(restockMessageEnabled))

@ExperimentalReactiveApi
fun MerchantWindow.Trade.Builder.setDiscount(discount: Provider<Int>): MerchantWindow.Trade.Builder =
    setDiscount(PropertyAdapter(discount))

@ExperimentalReactiveApi
fun MerchantWindow.Trade.Builder.setAvailable(available: Provider<Boolean>): MerchantWindow.Trade.Builder =
    setAvailable(PropertyAdapter(available))