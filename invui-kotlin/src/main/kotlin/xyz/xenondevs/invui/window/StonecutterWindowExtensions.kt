package xyz.xenondevs.invui.window

import xyz.xenondevs.commons.provider.MutableProvider
import xyz.xenondevs.invui.ExperimentalReactiveApi
import xyz.xenondevs.invui.MutablePropertyAdapter

@ExperimentalReactiveApi
fun StonecutterWindow.Builder.setSelectedSlot(provider: MutableProvider<Int>): StonecutterWindow.Builder =
    setSelectedSlot(MutablePropertyAdapter(provider))