package xyz.xenondevs.invui.window

import xyz.xenondevs.commons.provider.MutableProvider
import xyz.xenondevs.invui.ExperimentalReactiveApi
import xyz.xenondevs.invui.PropertyAdapter
import xyz.xenondevs.invui.gui.Slot

@ExperimentalReactiveApi
fun CrafterWindow.Builder.setSlot(slot: Int, provider: MutableProvider<Boolean>): CrafterWindow.Builder =
    setSlot(slot, PropertyAdapter(provider))

@ExperimentalReactiveApi
fun CrafterWindow.Builder.setSlot(x: Int, y: Int, provider: MutableProvider<Boolean>): CrafterWindow.Builder =
    setSlot(x, y, PropertyAdapter(provider))

@ExperimentalReactiveApi
fun CrafterWindow.Builder.setSlot(slot: Slot, provider: MutableProvider<Boolean>): CrafterWindow.Builder =
    setSlot(slot, PropertyAdapter(provider))

@ExperimentalReactiveApi
fun CrafterWindow.Builder.setSlots(slots: List<MutableProvider<Boolean>>): CrafterWindow.Builder =
    setSlots(slots.map { PropertyAdapter(it) })