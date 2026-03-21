package xyz.xenondevs.invui.window

import xyz.xenondevs.commons.provider.MutableProvider
import xyz.xenondevs.commons.provider.Provider
import xyz.xenondevs.invui.ExperimentalReactiveApi
import xyz.xenondevs.invui.PropertyAdapter

/**
 * Sets the provider containing the icons on the map of the [CartographyWindow] built by this builder.
 * If [icons] is not a [MutableProvider], attempting to change the icons through other means will throw an exception.
 */
@ExperimentalReactiveApi
fun CartographyWindow.Builder.setIcons(icons: Provider<Set<CartographyWindow.MapIcon>>): CartographyWindow.Builder =
    setIcons(PropertyAdapter(icons))

/**
 * Sets the provider containing the view of the [CartographyWindow] built by this builder.
 * If [view] is not a [MutableProvider], attempting to change the view through other means will throw an exception.
 */
@ExperimentalReactiveApi
fun CartographyWindow.Builder.setView(view: Provider<CartographyWindow.View>): CartographyWindow.Builder =
    setView(PropertyAdapter(view))