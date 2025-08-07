package xyz.xenondevs.invui.window

import xyz.xenondevs.commons.provider.MutableProvider
import xyz.xenondevs.invui.ExperimentalReactiveApi
import xyz.xenondevs.invui.PropertyAdapter

/**
 * Sets the provider containing the selected slot of the [StonecutterWindow] built by this builder.
 */
@ExperimentalReactiveApi
fun StonecutterWindow.Builder.setSelectedSlot(selectedSlot: MutableProvider<Int>): StonecutterWindow.Builder =
    setSelectedSlot(PropertyAdapter(selectedSlot))