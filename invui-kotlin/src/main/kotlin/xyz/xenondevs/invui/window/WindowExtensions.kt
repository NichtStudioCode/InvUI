package xyz.xenondevs.invui.window

import net.kyori.adventure.text.Component
import xyz.xenondevs.commons.provider.MutableProvider
import xyz.xenondevs.commons.provider.Provider
import xyz.xenondevs.invui.ExperimentalReactiveApi
import xyz.xenondevs.invui.PropertyAdapter

/**
 * Sets the provider containing the title of the [Window] built by this builder.
 */
@ExperimentalReactiveApi
fun <S : Window.Builder<*, *>> S.setTitle(title: Provider<Component>): S {
    addModifier { window -> title.observeWeak(window) { weakWindow -> weakWindow.updateTitle() } }
    setTitleSupplier(title)
    return this
}

/**
 * Sets the provider containing the setting whether the [Window] built by this builder is closeable.
 * If [closeable] is not a [MutableProvider], attempting to change the setting through other means will throw an exception.
 */
@ExperimentalReactiveApi
fun <S : Window.Builder<*, *>> S.setCloseable(closeable: Provider<Boolean>): S {
    setCloseable(PropertyAdapter(closeable))
    return this
}

/**
 * Sets the provider containing the server state of the [Window] built by this builder.
 */
@ExperimentalReactiveApi
fun <S : Window.Builder<*, *>> S.setWindowState(windowState: MutableProvider<Int>): S {
    setWindowState(PropertyAdapter(windowState))
    return this
}