package xyz.xenondevs.invui.window

import xyz.xenondevs.commons.provider.MutableProvider
import xyz.xenondevs.invui.ExperimentalReactiveApi
import xyz.xenondevs.invui.MutablePropertyAdapter
import xyz.xenondevs.invui.gui.Slot

@ExperimentalReactiveApi
fun CrafterWindow.Builder.setSlot(slot: Int, provider: MutableProvider<Boolean>): CrafterWindow.Builder =
    setSlot(slot, MutablePropertyAdapter(provider))

@ExperimentalReactiveApi
fun CrafterWindow.Builder.setSlot(x: Int, y: Int, provider: MutableProvider<Boolean>): CrafterWindow.Builder =
    setSlot(x, y, MutablePropertyAdapter(provider))

@ExperimentalReactiveApi
fun CrafterWindow.Builder.setSlot(slot: Slot, provider: MutableProvider<Boolean>): CrafterWindow.Builder =
    setSlot(slot, MutablePropertyAdapter(provider))

@ExperimentalReactiveApi
fun CrafterWindow.Builder.setSlots(slots: List<MutableProvider<Boolean>>): CrafterWindow.Builder =
    setSlots(slots.map { MutablePropertyAdapter(it) })