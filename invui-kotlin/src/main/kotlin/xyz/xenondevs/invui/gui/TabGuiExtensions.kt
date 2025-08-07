package xyz.xenondevs.invui.gui

import xyz.xenondevs.commons.provider.MutableProvider
import xyz.xenondevs.commons.provider.Provider
import xyz.xenondevs.invui.ExperimentalReactiveApi
import xyz.xenondevs.invui.PropertyAdapter

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