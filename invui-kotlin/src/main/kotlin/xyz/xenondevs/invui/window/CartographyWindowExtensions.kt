package xyz.xenondevs.invui.window

import xyz.xenondevs.commons.provider.MutableProvider
import xyz.xenondevs.invui.ExperimentalReactiveApi
import xyz.xenondevs.invui.MutablePropertyAdapter

@ExperimentalReactiveApi
fun CartographyWindow.Builder.setIcons(icons: MutableProvider<Set<CartographyWindow.MapIcon>>): CartographyWindow.Builder =
    setIcons(MutablePropertyAdapter(icons))

@ExperimentalReactiveApi
fun CartographyWindow.Builder.setView(view: MutableProvider<CartographyWindow.View>): CartographyWindow.Builder =
    setView(MutablePropertyAdapter(view))