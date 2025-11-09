package xyz.xenondevs.invui.gui

import xyz.xenondevs.commons.provider.MutableProvider
import xyz.xenondevs.commons.provider.Provider
import xyz.xenondevs.commons.provider.getOrNull
import xyz.xenondevs.invui.ExperimentalReactiveApi
import xyz.xenondevs.invui.PropertyAdapter
import xyz.xenondevs.invui.toProvider
import java.lang.ref.WeakReference

/**
 * Sets the provider containing the current tab for the [TabGui] built by this builder.
 * If the value of [tab] is set to an invalid tab, it will be automatically adjusted to the closest valid tab.
 */
@ExperimentalReactiveApi
fun TabGui.Builder.setTab(tab: MutableProvider<Int>): TabGui.Builder =
    setTab(PropertyAdapter(tab))

/**
 * Sets the provider containing the tabs of the [TabGui] built by this builder to the result
 * of applying [transform] to the value of [provider].
 * (Shortcut for `setTabs(provider.map(transform))`)
 */
@ExperimentalReactiveApi
fun <T> TabGui.Builder.setTabs(provider: Provider<T>, transform: (T) -> List<Gui?>): TabGui.Builder =
    setTabs(provider.map(transform))

/**
 * Sets the tabs containing the tabs of the [TabGui] built by this builder.
 * If [tabs] is not a [MutableProvider], attempting to change the tabs through other means will throw an exception.
 */
@ExperimentalReactiveApi
fun TabGui.Builder.setTabs(tabs: Provider<List<Gui?>>): TabGui.Builder =
    setTabs(PropertyAdapter(tabs))

/**
 * A provider containing the tabs of this [TabGui].
 *
 * - If the tabs were defined through a [MutableProvider], the same instance is returned.
 * - If the tabs were defined through a [Provider], a non-mutable [MutableProvider]
 * that throws on mutation attempts is returned.
 * - Otherwise, each invocation returns a new instance of a [MutableProvider] that is [weakly][WeakReference]
 *  linked to the [TabGui.tabsProperty].
 */
val TabGui.tabsProvider: MutableProvider<List<Gui?>>
    get() = tabsProperty.toProvider()

/**
 * A provider containing the currently selected tab index of this [TabGui].
 *
 * - If the tab was defined through a [MutableProvider], the same instance is returned.
 * - If the tab was defined through a [Provider], a non-mutable [MutableProvider]
 * that throws on mutation attempts is returned.
 * - Otherwise, each invocation returns a new instance of a [MutableProvider] that is [weakly][WeakReference]
 *  linked to the [TabGui.tabProperty].
 */
val TabGui.tabProvider: MutableProvider<Int>
    get() = tabProperty.toProvider()

/**
 * A provider containing the currently active tab [Gui] of this [TabGui],
 * or null if the current tab index is invalid.
 * 
 * Equivalent to `tabsProvider.getOrNull(tabProvider)`.
 */
val TabGui.activeTabProvider: Provider<Gui?>
    get() = tabsProvider.getOrNull(tabProvider)