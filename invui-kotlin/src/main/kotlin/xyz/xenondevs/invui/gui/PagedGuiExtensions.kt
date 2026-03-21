package xyz.xenondevs.invui.gui

import xyz.xenondevs.commons.provider.MutableProvider
import xyz.xenondevs.commons.provider.Provider
import xyz.xenondevs.invui.ExperimentalReactiveApi
import xyz.xenondevs.invui.PropertyAdapter
import xyz.xenondevs.invui.toProvider
import java.lang.ref.WeakReference

/**
 * Sets the provider containing the current page for the [PagedGui] built by this builder.
 * If the value of [page] is set to an invalid page, it will be automatically adjusted to the closest valid page.
 */
@ExperimentalReactiveApi
fun <C : Any> PagedGui.Builder<C>.setPage(page: MutableProvider<Int>): PagedGui.Builder<C> =
    setPage(PropertyAdapter(page))

/**
 * Sets the provider containing the content of the [PagedGui] built by this builder.
 * If [content] is not a [MutableProvider], attempting to change the content through other means will throw an exception.
 */
@ExperimentalReactiveApi
fun <C : Any> PagedGui.Builder<C>.setContent(content: Provider<List<C>>): PagedGui.Builder<C> =
    setContent(PropertyAdapter(content))

/**
 * A provider containing the content of this [PagedGui].
 * 
 * - If the content was defined through a [Provider], the same instance is returned.
 * - Otherwise, each invocation returns a new instance of a [Provider] that is [weakly][WeakReference]
 *  linked to the [PagedGui.contentProperty].
 */
@ExperimentalReactiveApi
val <C : Any> PagedGui<C>.contentProvider: Provider<List<C>>
    get() = contentProperty.toProvider()

/**
 * A provider containing currently selected page of this [PagedGui].
 *
 * - If the page was defined through a [Provider], the same instance is returned.
 * - Otherwise, each invocation returns a new instance of a [Provider] that is [weakly][WeakReference]
 *  linked to the [PagedGui.pageProperty].
 */
@ExperimentalReactiveApi
val PagedGui<*>.pageProvider: Provider<Int>
    get() = pageProperty.toProvider()

/**
 * A provider containing the page count of this [PagedGui].
 *
 * Each invocation returns a new instance of a [Provider] that is [weakly][WeakReference]
 * linked to the [PagedGui.getPageCountProperty].
 */
@ExperimentalReactiveApi
val PagedGui<*>.pageCountProvider: Provider<Int>
    get() = pageCountProperty.toProvider()