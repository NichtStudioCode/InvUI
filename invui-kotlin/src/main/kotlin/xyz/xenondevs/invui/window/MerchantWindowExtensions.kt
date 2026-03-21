package xyz.xenondevs.invui.window

import xyz.xenondevs.commons.provider.MutableProvider
import xyz.xenondevs.commons.provider.Provider
import xyz.xenondevs.invui.ExperimentalReactiveApi
import xyz.xenondevs.invui.PropertyAdapter

/**
 * Sets the provider containing the trades of the [MerchantWindow] built by this builder.
 */
@ExperimentalReactiveApi
fun MerchantWindow.Builder.setTrades(trades: Provider<List<MerchantWindow.Trade>>): MerchantWindow.Builder =
    setTrades(PropertyAdapter(trades))

/**
 * Sets the provider containing the [level][MerchantWindow.Builder.setLevel] of the [MerchantWindow] built by this builder.
 * If [level] is not a [MutableProvider], attempting to change the level through other means will throw an exception.
 */
@ExperimentalReactiveApi
fun MerchantWindow.Builder.setLevel(level: Provider<Int>): MerchantWindow.Builder =
    setLevel(PropertyAdapter(level))

/**
 * Sets the provider containing the [progress][MerchantWindow.Builder.setProgress] of the [MerchantWindow] built by this builder.
 * If [progress] is not a [MutableProvider], attempting to change the progress through other means will throw an exception.
 */
@ExperimentalReactiveApi
fun MerchantWindow.Builder.setProgress(progress: Provider<Double>): MerchantWindow.Builder =
    setProgress(PropertyAdapter(progress))

/**
 * Sets the provider containing whether the [MerchantWindow] built by this builder should show a restock message.
 * If [restockMessageEnabled] is not a [MutableProvider], attempting to change the setting through other means will throw an exception.
 */
@ExperimentalReactiveApi
fun MerchantWindow.Builder.setRestockMessageEnabled(restockMessageEnabled: Provider<Boolean>): MerchantWindow.Builder =
    setRestockMessageEnabled(PropertyAdapter(restockMessageEnabled))

/**
 * Sets the provider containing the [discount][MerchantWindow.Trade.Builder.setDiscount] of the [MerchantWindow.Trade] built by this builder.
 * If [discount] is not a [MutableProvider], attempting to change the discount through other means will throw an exception.
 */
@ExperimentalReactiveApi
fun MerchantWindow.Trade.Builder.setDiscount(discount: Provider<Int>): MerchantWindow.Trade.Builder =
    setDiscount(PropertyAdapter(discount))

/**
 * Sets the provider containing the [available][MerchantWindow.Trade.Builder.setAvailable] of the [MerchantWindow.Trade] built by this builder.
 * If [available] is not a [MutableProvider], attempting to change the availability through other
 */
@ExperimentalReactiveApi
fun MerchantWindow.Trade.Builder.setAvailable(available: Provider<Boolean>): MerchantWindow.Trade.Builder =
    setAvailable(PropertyAdapter(available))