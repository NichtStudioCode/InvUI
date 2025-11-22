package xyz.xenondevs.invui.gui

import xyz.xenondevs.commons.provider.MutableProvider
import xyz.xenondevs.commons.provider.Provider
import xyz.xenondevs.invui.ExperimentalReactiveApi
import xyz.xenondevs.invui.PropertyAdapter
import xyz.xenondevs.invui.toProvider
import java.lang.ref.WeakReference

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

/**
 * A provider containing the content of this [ScrollGui].
 *
 * - If the content was defined through a [MutableProvider], the same instance is returned.
 * - If the content was defined through a [Provider], a non-mutable [MutableProvider]
 * that throws on mutation attempts is returned.
 * - Otherwise, each invocation returns a new instance of a [MutableProvider] that is [weakly][WeakReference]
 *  linked to the [ScrollGui.contentProperty].
 */
@ExperimentalReactiveApi
val <C : Any> ScrollGui<C>.contentProvider: MutableProvider<List<C>>
    get() = contentProperty.toProvider()

/**
 * A provider containing currently selected line of this [ScrollGui].
 *
 * - If the line was defined through a [MutableProvider], the same instance is returned.
 * - If the line was defined through a [Provider], a non-mutable [MutableProvider]
 * that throws on mutation attempts is returned.
 * - Otherwise, each invocation returns a new instance of a [MutableProvider] that is [weakly][WeakReference]
 *  linked to the [ScrollGui.lineProperty].
 */
@ExperimentalReactiveApi
val ScrollGui<*>.lineProvider: MutableProvider<Int>
    get() = lineProperty.toProvider()

/**
 * A provider containing the line count of this [ScrollGui].
 *
 * Each invocation returns a new instance of a [MutableProvider] that is [weakly][WeakReference]
 * linked to the [ScrollGui.getLineCountProperty].
 */
@ExperimentalReactiveApi
val ScrollGui<*>.lineCountProvider: Provider<Int>
    get() = lineCountProperty.toProvider()

/**
 * A provider containing the max line index of this [ScrollGui].
 *
 * Each invocation returns a new instance of a [MutableProvider] that is [weakly][WeakReference]
 * linked to the [ScrollGui.getMaxLineProperty].
 */
@ExperimentalReactiveApi
val ScrollGui<*>.maxLineProvider: Provider<Int>
    get() = maxLineProperty.toProvider()