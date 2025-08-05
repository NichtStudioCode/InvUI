package xyz.xenondevs.invui.gui

import xyz.xenondevs.commons.provider.MutableProvider
import xyz.xenondevs.commons.provider.Provider
import xyz.xenondevs.invui.ExperimentalReactiveApi
import xyz.xenondevs.invui.PropertyAdapter

@ExperimentalReactiveApi
fun <C : Any> ScrollGui.Builder<C>.setLine(provider: MutableProvider<Int>): ScrollGui.Builder<C> =
    setLine(PropertyAdapter(provider))

@ExperimentalReactiveApi
fun <C : Any, T> ScrollGui.Builder<C>.setContent(provider: Provider<T>, transform: (T) -> List<C>): ScrollGui.Builder<C> =
    setContent(provider.map(transform))

@ExperimentalReactiveApi
fun <C : Any> ScrollGui.Builder<C>.setContent(provider: Provider<List<C>>): ScrollGui.Builder<C> =
    setContent(PropertyAdapter(provider))