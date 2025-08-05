package xyz.xenondevs.invui.gui

import xyz.xenondevs.commons.provider.MutableProvider
import xyz.xenondevs.commons.provider.Provider
import xyz.xenondevs.invui.ExperimentalReactiveApi
import xyz.xenondevs.invui.PropertyAdapter

@ExperimentalReactiveApi
fun <C : Any> PagedGui.Builder<C>.setPage(provider: MutableProvider<Int>): PagedGui.Builder<C> =
    setPage(PropertyAdapter(provider))

@ExperimentalReactiveApi
fun <C : Any, T> PagedGui.Builder<C>.setContent(provider: Provider<T>, transform: (T) -> List<C>): PagedGui.Builder<C> =
    setContent(provider.map(transform))

@ExperimentalReactiveApi
fun <C : Any> PagedGui.Builder<C>.setContent(provider: Provider<List<C>>): PagedGui.Builder<C> =
    setContent(PropertyAdapter(provider))