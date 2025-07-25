package xyz.xenondevs.invui.window

import xyz.xenondevs.commons.provider.Provider
import xyz.xenondevs.invui.ExperimentalReactiveApi
import xyz.xenondevs.invui.PropertyAdapter

@ExperimentalReactiveApi
fun <T> BrewerWindow.Builder.setBrewProgress(provider: Provider<T>, transform: (T) -> Double): BrewerWindow.Builder =
    setBrewProgress(provider.map(transform))

@ExperimentalReactiveApi
fun BrewerWindow.Builder.setBrewProgress(progress: Provider<Double>): BrewerWindow.Builder =
    setBrewProgress(PropertyAdapter(progress))

@ExperimentalReactiveApi
fun <T> BrewerWindow.Builder.setFuelProgress(provider: Provider<T>, transform: (T) -> Double): BrewerWindow.Builder =
    setFuelProgress(provider.map(transform))

@ExperimentalReactiveApi
fun BrewerWindow.Builder.setFuelProgress(progress: Provider<Double>): BrewerWindow.Builder =
    setFuelProgress(PropertyAdapter(progress))