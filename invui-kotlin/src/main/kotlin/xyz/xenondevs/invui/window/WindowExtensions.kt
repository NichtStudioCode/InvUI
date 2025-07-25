package xyz.xenondevs.invui.window

import net.kyori.adventure.text.Component
import xyz.xenondevs.commons.provider.MutableProvider
import xyz.xenondevs.commons.provider.Provider
import xyz.xenondevs.invui.ExperimentalReactiveApi
import xyz.xenondevs.invui.PropertyAdapter

@ExperimentalReactiveApi
fun <S : Window.Builder<*, *>, T> S.setTitle(provider: Provider<T>, transform: (T) -> Component): S =
    setTitle(provider.map(transform))

@ExperimentalReactiveApi
fun <S : Window.Builder<*, *>> S.setTitle(provider: Provider<Component>): S {
    addModifier { window -> provider.observeWeak(window) { weakWindow -> weakWindow.updateTitle() } }
    setTitleSupplier(provider)
    return this
}

@ExperimentalReactiveApi
fun <S : Window.Builder<*, *>, T> S.setCloseable(provider: Provider<T>, transform: (T) -> Boolean): S =
    setCloseable(provider.map(transform))

@ExperimentalReactiveApi
fun <S : Window.Builder<*, *>> S.setCloseable(closeable: Provider<Boolean>): S {
    setCloseable(PropertyAdapter(closeable))
    return this
}