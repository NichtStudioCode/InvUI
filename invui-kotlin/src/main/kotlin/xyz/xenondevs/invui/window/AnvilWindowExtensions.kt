package xyz.xenondevs.invui.window

import xyz.xenondevs.commons.provider.MutableProvider
import xyz.xenondevs.commons.provider.Provider
import xyz.xenondevs.invui.ExperimentalReactiveApi
import xyz.xenondevs.invui.PropertyAdapter

@Suppress("UNCHECKED_CAST")
@ExperimentalReactiveApi
fun AnvilWindow.Builder.addRenameHandler(handler: MutableProvider<String>): AnvilWindow.Builder =
    addRenameHandler(handler::set)

@ExperimentalReactiveApi
fun <T> AnvilWindow.Builder.setTextFieldAlwaysEnabled(provider: Provider<T>, transform: (T) -> Boolean): AnvilWindow.Builder =
    setTextFieldAlwaysEnabled(provider.map(transform))

@ExperimentalReactiveApi
fun AnvilWindow.Builder.setTextFieldAlwaysEnabled(enabled: Provider<Boolean>): AnvilWindow.Builder =
    setTextFieldAlwaysEnabled(PropertyAdapter(enabled))