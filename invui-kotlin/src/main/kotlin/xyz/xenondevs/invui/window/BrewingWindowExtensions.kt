package xyz.xenondevs.invui.window

import xyz.xenondevs.commons.provider.MutableProvider
import xyz.xenondevs.commons.provider.Provider
import xyz.xenondevs.invui.ExperimentalReactiveApi
import xyz.xenondevs.invui.PropertyAdapter

/**
 * Sets the provider containing the [brew progress][BrewingWindow.Builder.setBrewProgress] of the [BrewingWindow] built by this builder
 * to the result of applying [transform] to the value of [provider].
 * (Shortcut for `setBrewProgress(provider.map(transform))`)
 */
@ExperimentalReactiveApi
fun <T> BrewingWindow.Builder.setBrewProgress(provider: Provider<T>, transform: (T) -> Double): BrewingWindow.Builder =
    setBrewProgress(provider.map(transform))

/**
 * Sets the provider containing the [brew progress][BrewingWindow.Builder.setBrewProgress] of the [BrewingWindow] built by this builder.
 * If [brewProgress] is not a [MutableProvider], attempting to change the brew progress through other means will throw an exception.
 */
@ExperimentalReactiveApi
fun BrewingWindow.Builder.setBrewProgress(brewProgress: Provider<Double>): BrewingWindow.Builder =
    setBrewProgress(PropertyAdapter(brewProgress))

/**
 * Sets the provider containing the [fuel progress][BrewingWindow.Builder.setFuelProgress] of the [BrewingWindow] built by this builder
 * to the result of applying [transform] to the value of [provider].
 * (Shortcut for `setFuelProgress(provider.map(transform))`)
 */
@ExperimentalReactiveApi
fun <T> BrewingWindow.Builder.setFuelProgress(provider: Provider<T>, transform: (T) -> Double): BrewingWindow.Builder =
    setFuelProgress(provider.map(transform))

/**
 * Sets the provider containing the [fuel progress][BrewingWindow.Builder.setFuelProgress] of the [BrewingWindow] built by this builder.
 * If [fuelProgress] is not a [MutableProvider], attempting to change the fuel progress through other means will throw an exception.
 */
@ExperimentalReactiveApi
fun BrewingWindow.Builder.setFuelProgress(fuelProgress: Provider<Double>): BrewingWindow.Builder =
    setFuelProgress(PropertyAdapter(fuelProgress))