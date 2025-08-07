package xyz.xenondevs.invui.window

import xyz.xenondevs.commons.provider.MutableProvider
import xyz.xenondevs.commons.provider.Provider
import xyz.xenondevs.invui.ExperimentalReactiveApi
import xyz.xenondevs.invui.PropertyAdapter

/**
 * Adds [handler] as a rename handler for the [AnvilWindow] built by this builder,
 * meaning the anvil text field will be written into [handler].
 */
@ExperimentalReactiveApi
fun AnvilWindow.Builder.addRenameHandler(handler: MutableProvider<String>): AnvilWindow.Builder =
    addRenameHandler(handler::set)

/**
 * Sets the provider containing the setting whether the text field of the [AnvilWindow] built by this builder is always enabled
 * to the result of applying [transform] to the value of [provider].
 * (Shortcut for `setTextFieldAlwaysEnabled(provider.map(transform))`)
 */
@ExperimentalReactiveApi
fun <T> AnvilWindow.Builder.setTextFieldAlwaysEnabled(provider: Provider<T>, transform: (T) -> Boolean): AnvilWindow.Builder =
    setTextFieldAlwaysEnabled(provider.map(transform))

/**
 * Sets the provider containing the setting whether the text field of the [AnvilWindow] built by this builder is always enabled.
 * If [textFieldAlwaysEnabled] is not a [MutableProvider], attempting to change the setting through other means will throw an exception.
 */
@ExperimentalReactiveApi
fun AnvilWindow.Builder.setTextFieldAlwaysEnabled(textFieldAlwaysEnabled: Provider<Boolean>): AnvilWindow.Builder =
    setTextFieldAlwaysEnabled(PropertyAdapter(textFieldAlwaysEnabled))

/**
 * Sets the provider containing the setting whether the result of the [AnvilWindow] built by this builder is always valid
 * to the result of applying [transform] to the value of [provider].
 * (Shortcut for `setResultAlwaysValid(provider.map(transform))`)
 */
@ExperimentalReactiveApi
fun <T> AnvilWindow.Builder.setResultAlwaysValid(provider: Provider<T>, transform: (T) -> Boolean): AnvilWindow.Builder =
    setResultAlwaysValid(provider.map(transform))

/**
 * Sets the provider containing the setting whether the result of the [AnvilWindow] built by this builder is always valid.
 * If [resultAlwaysValid] is not a [MutableProvider], attempting to change the setting through other means will throw an exception.
 */
@ExperimentalReactiveApi
fun AnvilWindow.Builder.setResultAlwaysValid(resultAlwaysValid: Provider<Boolean>): AnvilWindow.Builder =
    setResultAlwaysValid(PropertyAdapter(resultAlwaysValid))