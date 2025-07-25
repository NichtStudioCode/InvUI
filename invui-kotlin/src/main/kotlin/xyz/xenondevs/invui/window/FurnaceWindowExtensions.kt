package xyz.xenondevs.invui.window

import xyz.xenondevs.commons.provider.Provider
import xyz.xenondevs.invui.ExperimentalReactiveApi
import xyz.xenondevs.invui.PropertyAdapter

@ExperimentalReactiveApi
fun <T> FurnaceWindow.Builder.setCookProgress(provider: Provider<T>, transform: (T) -> Double): FurnaceWindow.Builder =
    setCookProgress(provider.map(transform))

@ExperimentalReactiveApi
fun FurnaceWindow.Builder.setCookProgress(progress: Provider<Double>): FurnaceWindow.Builder =
    setCookProgress(PropertyAdapter(progress))

@ExperimentalReactiveApi
fun <T> FurnaceWindow.Builder.setBurnProgress(provider: Provider<T>, transform: (T) -> Double): FurnaceWindow.Builder =
    setBurnProgress(provider.map(transform))

@ExperimentalReactiveApi
fun FurnaceWindow.Builder.setBurnProgress(progress: Provider<Double>): FurnaceWindow.Builder =
    setBurnProgress(PropertyAdapter(progress))