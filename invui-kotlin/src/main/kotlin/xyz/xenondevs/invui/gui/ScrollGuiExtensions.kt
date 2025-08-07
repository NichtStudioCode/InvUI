package xyz.xenondevs.invui.gui

import xyz.xenondevs.commons.provider.MutableProvider
import xyz.xenondevs.commons.provider.Provider
import xyz.xenondevs.invui.ExperimentalReactiveApi
import xyz.xenondevs.invui.PropertyAdapter

/**
 * Sets the provider containing the current line for the [ScrollGui] built by this builder.
 * If the value of [line] is set to an invalid line, it will be automatically adjusted to the closest valid line.
 */
@ExperimentalReactiveApi
fun <C : Any> ScrollGui.Builder<C>.setLine(line: MutableProvider<Int>): ScrollGui.Builder<C> =
    setLine(PropertyAdapter(line))

/**
 * Sets the provider containing the content of the [ScrollGui] built by this builder to the result
 * of applying [transform] to the value of [provider].
 * (Shortcut for `setContent(provider.map(transform))`)
 */
@ExperimentalReactiveApi
fun <C : Any, T> ScrollGui.Builder<C>.setContent(provider: Provider<T>, transform: (T) -> List<C>): ScrollGui.Builder<C> =
    setContent(provider.map(transform))

/**
 * Sets the provider containing the content of the [ScrollGui] built by this builder.
 * If [content] is not a [MutableProvider], attempting to change the content through other means will throw an exception.
 */
@ExperimentalReactiveApi
fun <C : Any> ScrollGui.Builder<C>.setContent(content: Provider<List<C>>): ScrollGui.Builder<C> =
    setContent(PropertyAdapter(content))