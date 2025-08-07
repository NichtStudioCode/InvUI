package xyz.xenondevs.invui.window

import xyz.xenondevs.commons.provider.MutableProvider
import xyz.xenondevs.commons.provider.Provider
import xyz.xenondevs.invui.ExperimentalReactiveApi
import xyz.xenondevs.invui.PropertyAdapter

/**
 * Sets the provider containing the trades of the [MerchantWindow] built by this builder to the result
 * of applying [transform] to the value of [provider].
 * (Shortcut for `setTrades(provider.map(transform))`)
 */
@ExperimentalReactiveApi
fun <T> MerchantWindow.Builder.setTrades(provider: Provider<T>, transform: (T) -> List<MerchantWindow.Trade>): MerchantWindow.Builder =
    setTrades(provider.map(transform))

/**
 * Sets the provider containing the trades of the [MerchantWindow] built by this builder.
 */
@ExperimentalReactiveApi
fun MerchantWindow.Builder.setTrades(trades: Provider<List<MerchantWindow.Trade>>): MerchantWindow.Builder =
    setTrades(PropertyAdapter(trades))

/**
 * Sets the provider containing the [level][MerchantWindow.Builder.setLevel] of the [MerchantWindow] built by this builder
 * to the result of applying [transform] to the value of [provider].
 * (Shortcut for `setLevel(provider.map(transform))`)
 */
@ExperimentalReactiveApi
fun <T> MerchantWindow.Builder.setLevel(provider: Provider<T>, transform: (T) -> Int): MerchantWindow.Builder =
    setLevel(provider.map(transform))

/**
 * Sets the provider containing the [level][MerchantWindow.Builder.setLevel] of the [MerchantWindow] built by this builder.
 * If [level] is not a [MutableProvider], attempting to change the level through other means will throw an exception.
 */
@ExperimentalReactiveApi
fun MerchantWindow.Builder.setLevel(level: Provider<Int>): MerchantWindow.Builder =
    setLevel(PropertyAdapter(level))

/**
 * Sets the provider containing the [progress][MerchantWindow.Builder.setProgress] of the [MerchantWindow] built by this builder
 * to the result of applying [transform] to the value of [provider].
 * (Shortcut for `setProgress(provider.map(transform))`)
 */
@ExperimentalReactiveApi
fun <T> MerchantWindow.Builder.setProgress(provider: Provider<T>, transform: (T) -> Double): MerchantWindow.Builder =
    setProgress(provider.map(transform))

/**
 * Sets the provider containing the [progress][MerchantWindow.Builder.setProgress] of the [MerchantWindow] built by this builder.
 * If [progress] is not a [MutableProvider], attempting to change the progress through other means will throw an exception.
 */
@ExperimentalReactiveApi
fun MerchantWindow.Builder.setProgress(progress: Provider<Double>): MerchantWindow.Builder =
    setProgress(PropertyAdapter(progress))

/**
 * Sets the provider containing whether the [MerchantWindow] built by this builder should show a restock message
 * to the result of applying [transform] to the value of [provider].
 * (Shortcut for `setRestockMessageEnabled(provider.map(transform))`)
 */
@ExperimentalReactiveApi
fun <T> MerchantWindow.Builder.setRestockMessageEnabled(provider: Provider<T>, transform: (T) -> Boolean): MerchantWindow.Builder =
    setRestockMessageEnabled(provider.map(transform))

/**
 * Sets the provider containing whether the [MerchantWindow] built by this builder should show a restock message.
 * If [restockMessageEnabled] is not a [MutableProvider], attempting to change the setting through other means will throw an exception.
 */
@ExperimentalReactiveApi
fun MerchantWindow.Builder.setRestockMessageEnabled(restockMessageEnabled: Provider<Boolean>): MerchantWindow.Builder =
    setRestockMessageEnabled(PropertyAdapter(restockMessageEnabled))

/**
 * Sets the provider containing the [discount][MerchantWindow.Trade.Builder.setDiscount] of the [MerchantWindow.Trade] built by this builder
 * to the result of applying [transform] to the value of [provider].
 * (Shortcut for `setDiscount(provider.map(transform))`)
 */
@ExperimentalReactiveApi
fun <T> MerchantWindow.Trade.Builder.setDiscount(provider: Provider<T>, transform: (T) -> Int): MerchantWindow.Trade.Builder =
    setDiscount(provider.map(transform))

/**
 * Sets the provider containing the [discount][MerchantWindow.Trade.Builder.setDiscount] of the [MerchantWindow.Trade] built by this builder.
 * If [discount] is not a [MutableProvider], attempting to change the discount through other means will throw an exception.
 */
@ExperimentalReactiveApi
fun MerchantWindow.Trade.Builder.setDiscount(discount: Provider<Int>): MerchantWindow.Trade.Builder =
    setDiscount(PropertyAdapter(discount))

/**
 * Sets the provider containing the [discount][MerchantWindow.Trade.Builder.setDiscount] of the [MerchantWindow.Trade] built by this builder
 * to the result of applying [transform] to the value of [provider].
 */
@ExperimentalReactiveApi
fun <T> MerchantWindow.Trade.Builder.setAvailable(provider: Provider<T>, transform: (T) -> Boolean): MerchantWindow.Trade.Builder =
    setAvailable(provider.map(transform))

/**
 * Sets the provider containing the [available][MerchantWindow.Trade.Builder.setAvailable] of the [MerchantWindow.Trade] built by this builder.
 * If [available] is not a [MutableProvider], attempting to change the availability through other
 */
@ExperimentalReactiveApi
fun MerchantWindow.Trade.Builder.setAvailable(available: Provider<Boolean>): MerchantWindow.Trade.Builder =
    setAvailable(PropertyAdapter(available))