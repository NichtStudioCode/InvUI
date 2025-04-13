package xyz.xenondevs.invui.gui

import xyz.xenondevs.commons.provider.MutableProvider
import xyz.xenondevs.commons.provider.Provider
import xyz.xenondevs.commons.provider.combinedProvider
import xyz.xenondevs.commons.provider.map
import xyz.xenondevs.invui.ExperimentalReactiveApi
import xyz.xenondevs.invui.MutablePropertyAdapter
import xyz.xenondevs.invui.PropertyAdapter

@ExperimentalReactiveApi
fun <C : Any> ScrollGui.Builder<C>.setLine(provider: MutableProvider<Int>): ScrollGui.Builder<C> {
    setLine(MutablePropertyAdapter(provider))
    return this
}

@ExperimentalReactiveApi
fun <C : Any> ScrollGui<C>.setLine(provider: MutableProvider<Int>) {
    setLine(MutablePropertyAdapter(provider))
}

@ExperimentalReactiveApi
fun <C : Any> ScrollGui.Builder<C>.setContent(provider: Provider<List<C>>): ScrollGui.Builder<C> {
    setContent(PropertyAdapter(provider))
    return this
}

@ExperimentalReactiveApi
fun <C : Any> ScrollGui<C>.setContent(provider: Provider<List<C>>) {
    setContent(PropertyAdapter(provider))
}