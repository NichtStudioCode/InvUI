package xyz.xenondevs.invui.window

import xyz.xenondevs.commons.provider.MutableProvider
import xyz.xenondevs.invui.ExperimentalReactiveApi
import xyz.xenondevs.invui.PropertyAdapter

@ExperimentalReactiveApi
fun StonecutterWindow.Builder.setSelectedSlot(provider: MutableProvider<Int>): StonecutterWindow.Builder =
    setSelectedSlot(PropertyAdapter(provider))