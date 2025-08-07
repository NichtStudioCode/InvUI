package xyz.xenondevs.invui.window

import xyz.xenondevs.commons.provider.MutableProvider
import xyz.xenondevs.commons.provider.Provider
import xyz.xenondevs.invui.ExperimentalReactiveApi
import xyz.xenondevs.invui.PropertyAdapter

/**
 * Sets the provider containing the [cook progress][FurnaceWindow.Builder.setCookProgress] of the [FurnaceWindow] built by this builder
 * to the result of applying [transform] to the value of [provider].
 * (Shortcut for `setCookProgress(provider.map(transform))`)
 */
@ExperimentalReactiveApi
fun <T> FurnaceWindow.Builder.setCookProgress(provider: Provider<T>, transform: (T) -> Double): FurnaceWindow.Builder =
    setCookProgress(provider.map(transform))

/**
 * Sets the provider containing the [cook progress][FurnaceWindow.Builder.setCookProgress] of the [FurnaceWindow] built by this builder.
 * If [cookProgress] is not a [MutableProvider], attempting to change the cook progress through other means will throw an exception.
 */
@ExperimentalReactiveApi
fun FurnaceWindow.Builder.setCookProgress(cookProgress: Provider<Double>): FurnaceWindow.Builder =
    setCookProgress(PropertyAdapter(cookProgress))

/**
 * Sets the provider containing the [burn progress][FurnaceWindow.Builder.setBurnProgress] of the [FurnaceWindow] built by this builder
 * to the result of applying [transform] to the value of [provider].
 * (Shortcut for `setBurnProgress(provider.map(transform))`)
 */
@ExperimentalReactiveApi
fun <T> FurnaceWindow.Builder.setBurnProgress(provider: Provider<T>, transform: (T) -> Double): FurnaceWindow.Builder =
    setBurnProgress(provider.map(transform))

/**
 * Sets the provider containing the [burn progress][FurnaceWindow.Builder.setBurnProgress] of the [FurnaceWindow] built by this builder.
 * If [burnProgress] is not a [MutableProvider], attempting to change the burn progress through other means will throw an exception.
 */
@ExperimentalReactiveApi
fun FurnaceWindow.Builder.setBurnProgress(burnProgress: Provider<Double>): FurnaceWindow.Builder =
    setBurnProgress(PropertyAdapter(burnProgress))