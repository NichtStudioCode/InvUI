package xyz.xenondevs.invui.window

import xyz.xenondevs.commons.provider.MutableProvider
import xyz.xenondevs.invui.ExperimentalReactiveApi
import xyz.xenondevs.invui.MutablePropertyAdapter

@ExperimentalReactiveApi
fun FurnaceWindow.Builder.setCookProgress(progress: MutableProvider<Double>): FurnaceWindow.Builder =
    setCookProgress(MutablePropertyAdapter(progress))

@ExperimentalReactiveApi
fun FurnaceWindow.Builder.setBurnProgress(progress: MutableProvider<Double>): FurnaceWindow.Builder =
    setBurnProgress(MutablePropertyAdapter(progress))