package xyz.xenondevs.invui.window

import xyz.xenondevs.commons.provider.MutableProvider
import xyz.xenondevs.invui.ExperimentalReactiveApi
import xyz.xenondevs.invui.PropertyAdapter
import xyz.xenondevs.invui.gui.Slot

/**
 * Sets the provider containing the disabled state of [slot] in the [CrafterWindow] built by this builder.
 */
@ExperimentalReactiveApi
fun CrafterWindow.Builder.setSlot(slot: Int, state: MutableProvider<Boolean>): CrafterWindow.Builder =
    setSlot(slot, PropertyAdapter(state))

/**
 * Sets the provider containing the disabled state of the slot at [x], [y] in the [CrafterWindow] built by this builder.
 */
@ExperimentalReactiveApi
fun CrafterWindow.Builder.setSlot(x: Int, y: Int, state: MutableProvider<Boolean>): CrafterWindow.Builder =
    setSlot(x, y, PropertyAdapter(state))

/**
 * Sets the provider containing the disabled state of [slot] in the [CrafterWindow] built by this builder.
 */
@ExperimentalReactiveApi
fun CrafterWindow.Builder.setSlot(slot: Slot, state: MutableProvider<Boolean>): CrafterWindow.Builder =
    setSlot(slot, PropertyAdapter(state))

/**
 * Sets the providers containing the disabled states of all nine slots in the [CrafterWindow] built by this builder.
 */
@ExperimentalReactiveApi
fun CrafterWindow.Builder.setSlots(states: List<MutableProvider<Boolean>>): CrafterWindow.Builder =
    setSlots(states.map { PropertyAdapter(it) })