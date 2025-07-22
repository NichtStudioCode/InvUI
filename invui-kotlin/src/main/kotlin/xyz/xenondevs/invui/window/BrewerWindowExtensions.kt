package xyz.xenondevs.invui.window

import xyz.xenondevs.commons.provider.MutableProvider
import xyz.xenondevs.invui.ExperimentalReactiveApi
import xyz.xenondevs.invui.MutablePropertyAdapter

@ExperimentalReactiveApi
fun BrewerWindow.Builder.setBrewProgress(progress: MutableProvider<Double>): BrewerWindow.Builder =
    setBrewProgress(MutablePropertyAdapter(progress))

@ExperimentalReactiveApi
fun BrewerWindow.Builder.setFuelProgress(progress: MutableProvider<Double>): BrewerWindow.Builder =
    setFuelProgress(MutablePropertyAdapter(progress))