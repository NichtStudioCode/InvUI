package xyz.xenondevs.invui.window

import xyz.xenondevs.commons.provider.Provider
import xyz.xenondevs.invui.ExperimentalReactiveApi
import xyz.xenondevs.invui.PropertyAdapter

@ExperimentalReactiveApi
fun <T> BrewingWindow.Builder.setBrewProgress(provider: Provider<T>, transform: (T) -> Double): BrewingWindow.Builder =
    setBrewProgress(provider.map(transform))

@ExperimentalReactiveApi
fun BrewingWindow.Builder.setBrewProgress(progress: Provider<Double>): BrewingWindow.Builder =
    setBrewProgress(PropertyAdapter(progress))

@ExperimentalReactiveApi
fun <T> BrewingWindow.Builder.setFuelProgress(provider: Provider<T>, transform: (T) -> Double): BrewingWindow.Builder =
    setFuelProgress(provider.map(transform))

@ExperimentalReactiveApi
fun BrewingWindow.Builder.setFuelProgress(progress: Provider<Double>): BrewingWindow.Builder =
    setFuelProgress(PropertyAdapter(progress))