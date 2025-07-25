package xyz.xenondevs.invui.window

import net.kyori.adventure.text.Component
import xyz.xenondevs.commons.provider.MutableProvider
import xyz.xenondevs.commons.provider.Provider
import xyz.xenondevs.invui.ExperimentalReactiveApi

@ExperimentalReactiveApi
fun <S : Window.Builder<*, *>, T> S.setTitle(provider: Provider<T>, transform: (T) -> Component): S =
    setTitle(provider.map(transform))

@Suppress("UNCHECKED_CAST")
@ExperimentalReactiveApi
fun <S : Window.Builder<*, *>> S.setTitle(provider: Provider<Component>): S {
    addModifier { window -> provider.observeWeak(window) { weakWindow -> weakWindow.updateTitle() } }
    return setTitleSupplier(provider) as S
}