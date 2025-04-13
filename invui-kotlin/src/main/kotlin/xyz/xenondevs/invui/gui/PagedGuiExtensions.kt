package xyz.xenondevs.invui.gui

import xyz.xenondevs.commons.provider.MutableProvider
import xyz.xenondevs.commons.provider.Provider
import xyz.xenondevs.commons.provider.combinedProvider
import xyz.xenondevs.commons.provider.map
import xyz.xenondevs.invui.ExperimentalReactiveApi
import xyz.xenondevs.invui.MutablePropertyAdapter
import xyz.xenondevs.invui.PropertyAdapter

@ExperimentalReactiveApi
fun <C : Any> PagedGui.Builder<C>.setPage(provider: MutableProvider<Int>): PagedGui.Builder<C> {
    setPage(MutablePropertyAdapter(provider))
    return this
}

@ExperimentalReactiveApi
fun <C : Any> PagedGui<C>.setPage(provider: MutableProvider<Int>) {
    setPage(MutablePropertyAdapter(provider))
}

@ExperimentalReactiveApi
fun <C : Any> PagedGui.Builder<C>.setContent(provider: Provider<List<C>>): PagedGui.Builder<C> {
    setContent(PropertyAdapter(provider))
    return this
}

@ExperimentalReactiveApi
fun <C : Any> PagedGui<C>.setContent(provider: Provider<List<C>>) {
    setContent(PropertyAdapter(provider))
}