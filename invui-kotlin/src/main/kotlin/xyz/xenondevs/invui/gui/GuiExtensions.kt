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

/**
 * Sets the provider containing the background [ItemProvider] for the [Gui] built by this builder.
 * If [background] is not a [MutableProvider] attempting to change the background through other means will throw an exception.
 */
@ExperimentalReactiveApi
fun <G : Gui, B : Gui.Builder<G, B>> B.setBackground(background: Provider<ItemProvider?>): B =
    setBackground(PropertyAdapter(background))

/**
 * Sets the provider containing the frozen state of the [Gui] built by this builder.
 * If [frozen] is not a [MutableProvider] attempting to change the frozen state through other means will throw an exception.
 */
@ExperimentalReactiveApi
fun <G : Gui, B : Gui.Builder<G, B>> B.setFrozen(frozen: Provider<Boolean>): B =
    setFrozen(PropertyAdapter(frozen))

/**
 * Sets the provider containing the setting whether slots of partially obscured embedded inventories
 * should be ignored or not of the [Gui] built by this builder.
 * If the provider is not a [MutableProvider], attempting to change this setting through other means will throw an exception.
 */
@ExperimentalReactiveApi
fun <G : Gui, B : Gui.Builder<G, B>> B.setIgnoreObscuredInventorySlots(ignoreObscuredInventorySlots: Provider<Boolean>): B =
    setIgnoreObscuredInventorySlots(PropertyAdapter(ignoreObscuredInventorySlots))