package xyz.xenondevs.invui.window

import xyz.xenondevs.commons.provider.Provider
import xyz.xenondevs.invui.ExperimentalReactiveApi
import xyz.xenondevs.invui.PropertyAdapter

@ExperimentalReactiveApi
fun MerchantWindow.Builder.setTrades(trades: Provider<List<MerchantWindow.Trade>>): MerchantWindow.Builder =
    setTrades(PropertyAdapter(trades))

@ExperimentalReactiveApi
fun <T> MerchantWindow.Builder.setLevel(provider: Provider<T>, transform: (T) -> Int): MerchantWindow.Builder =
    setLevel(provider.map(transform))

@ExperimentalReactiveApi
fun MerchantWindow.Builder.setLevel(level: Provider<Int>): MerchantWindow.Builder =
    setLevel(PropertyAdapter(level))

@ExperimentalReactiveApi
fun <T> MerchantWindow.Builder.setProgress(provider: Provider<T>, transform: (T) -> Double): MerchantWindow.Builder =
    setProgress(provider.map(transform))

@ExperimentalReactiveApi
fun MerchantWindow.Builder.setProgress(progress: Provider<Double>): MerchantWindow.Builder =
    setProgress(PropertyAdapter(progress))

@ExperimentalReactiveApi
fun <T> MerchantWindow.Builder.setRestockMessageEnabled(provider: Provider<T>, transform: (T) -> Boolean): MerchantWindow.Builder =
    setRestockMessageEnabled(provider.map(transform))

@ExperimentalReactiveApi
fun MerchantWindow.Builder.setRestockMessageEnabled(restockMessageEnabled: Provider<Boolean>): MerchantWindow.Builder =
    setRestockMessageEnabled(PropertyAdapter(restockMessageEnabled))

@ExperimentalReactiveApi
fun <T> MerchantWindow.Trade.Builder.setDiscount(provider: Provider<T>, transform: (T) -> Int): MerchantWindow.Trade.Builder =
    setDiscount(provider.map(transform))

@ExperimentalReactiveApi
fun MerchantWindow.Trade.Builder.setDiscount(discount: Provider<Int>): MerchantWindow.Trade.Builder =
    setDiscount(PropertyAdapter(discount))

@ExperimentalReactiveApi
fun <T> MerchantWindow.Trade.Builder.setAvailable(provider: Provider<T>, transform: (T) -> Boolean): MerchantWindow.Trade.Builder =
    setAvailable(provider.map(transform))

@ExperimentalReactiveApi
fun MerchantWindow.Trade.Builder.setAvailable(available: Provider<Boolean>): MerchantWindow.Trade.Builder =
    setAvailable(PropertyAdapter(available))