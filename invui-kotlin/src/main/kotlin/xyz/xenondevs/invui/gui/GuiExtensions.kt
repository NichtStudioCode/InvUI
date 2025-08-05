package xyz.xenondevs.invui.gui

import xyz.xenondevs.commons.provider.MutableProvider
import xyz.xenondevs.commons.provider.Provider
import xyz.xenondevs.invui.ExperimentalReactiveApi
import xyz.xenondevs.invui.PropertyAdapter
import xyz.xenondevs.invui.item.Item
import xyz.xenondevs.invui.item.ItemProvider

/**
 * Gets the [SlotElement] on [slot].
 */
operator fun Gui.get(slot: Int): SlotElement? = getSlotElement(slot)

/**
 * Gets the [SlotElement] at [x], [y].
 */
operator fun Gui.get(x: Int, y: Int): SlotElement? = getSlotElement(x, y)

/**
 * Sets the [SlotElement] on [slot] to [element].
 */
operator fun Gui.set(slot: Int, element: SlotElement?) = setSlotElement(slot, element)

/**
 * Sets the [SlotElement] at [x], [y] to [element].
 */
operator fun Gui.set(x: Int, y: Int, element: SlotElement?) = setSlotElement(x, y, element)

/**
 * Sets the [SlotElement] on [slot] to [item].
 */
operator fun Gui.set(slot: Int, item: Item?) = setItem(slot, item)

/**
 * Sets the [SlotElement] at [x], [y] to [item].
 */
operator fun Gui.set(x: Int, y: Int, item: Item?) = setItem(x, y, item)

/**
 * Adds [elements] to the gui.
 */
@JvmName("plusAssignSlotElements")
operator fun Gui.plusAssign(elements: Iterable<SlotElement>) = elements.forEach { addSlotElements(it) }

/**
 * Adds [items] to the gui.
 */
@JvmName("plusAssignItems")
operator fun Gui.plusAssign(items: Iterable<Item>) = items.forEach { addItems(it) }

@ExperimentalReactiveApi
fun <G : Gui, B : Gui.Builder<G, B>> B.setBackground(provider: Provider<ItemProvider?>): B =
    setBackground(PropertyAdapter(provider))

@ExperimentalReactiveApi
fun <G : Gui, B : Gui.Builder<G, B>> B.setFrozen(provider: Provider<Boolean>): B =
    setFrozen(PropertyAdapter(provider))

@ExperimentalReactiveApi
fun <G : Gui, B : Gui.Builder<G, B>> B.setIgnoreObscuredInventorySlots(provider: Provider<Boolean>): B =
    setIgnoreObscuredInventorySlots(PropertyAdapter(provider))