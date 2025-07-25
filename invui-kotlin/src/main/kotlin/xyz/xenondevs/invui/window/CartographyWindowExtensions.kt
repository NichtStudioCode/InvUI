package xyz.xenondevs.invui.window

import xyz.xenondevs.commons.provider.Provider
import xyz.xenondevs.invui.ExperimentalReactiveApi
import xyz.xenondevs.invui.PropertyAdapter

@ExperimentalReactiveApi
fun <T> CartographyWindow.Builder.setIcons(provider: Provider<T>, transform: (T) -> Set<CartographyWindow.MapIcon>): CartographyWindow.Builder =
    setIcons(provider.map(transform))

@ExperimentalReactiveApi
fun CartographyWindow.Builder.setIcons(icons: Provider<Set<CartographyWindow.MapIcon>>): CartographyWindow.Builder =
    setIcons(PropertyAdapter(icons))

@ExperimentalReactiveApi
fun <T> CartographyWindow.Builder.setView(provider: Provider<T>, transform: (T) -> CartographyWindow.View): CartographyWindow.Builder =
    setView(provider.map(transform))

@ExperimentalReactiveApi
fun CartographyWindow.Builder.setView(view: Provider<CartographyWindow.View>): CartographyWindow.Builder =
    setView(PropertyAdapter(view))