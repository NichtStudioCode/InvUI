@file:Suppress("PackageDirectoryMismatch")

package de.studiocode.invui.gui

import de.studiocode.invui.item.Item

/**
 * Gets the [SlotElement] placed on that slot.
 * 
 * @param slot The slot index
 * @return The [SlotElement] placed on that slot or null if there is none
 */
operator fun Gui.get(slot: Int): SlotElement? = getSlotElement(slot)

/**
 * Gets the [SlotElement] placed on these coordinates.
 * 
 * @param x The x coordinate of the slot
 * @param y The y coordinate of the slot
 * @return The [SlotElement] placed on that slot or null if there is none
 */
operator fun Gui.get(x: Int, y: Int): SlotElement? = getSlotElement(x, y)

/**
 * Sets the [SlotElement] on that slot.
 *
 * @param slot The slot index
 * @param element The [SlotElement] to set or null to remove the current one
 */
operator fun Gui.set(slot: Int, element: SlotElement?) = setSlotElement(slot, element)

/**
 * Sets the [SlotElement] on these coordinates.
 * 
 * @param x The x coordinate of the slot
 * @param y The y coordinate of the slot
 * @param element The [SlotElement] to set or null to remove the current one
 */
operator fun Gui.set(x: Int, y: Int, element: SlotElement?) = setSlotElement(x, y, element)

/**
 * Sets the [Item] on that slot.
 * 
 * @param slot The slot index
 * @param item The [Item] to set or null to remove the current one
 */
operator fun Gui.set(slot: Int, item: Item?) = setItem(slot, item)

/**
 * Sets the [Item] on these coordinates.
 * 
 * @param x The x coordinate of the slot
 * @param y The y coordinate of the slot
 * @param item The [Item] to set or null to remove the current one
 */
operator fun Gui.set(x: Int, y: Int, item: Item?) = setItem(x, y, item)

/**
 * Adds the given [elements].
 * 
 * @param elements The [SlotElements][SlotElement] to add.
 */
@JvmName("plusAssignSlotElements")
operator fun Gui.plusAssign(elements: Iterable<SlotElement>) = elements.forEach { addSlotElements(it) }

/**
 * Adds the given [items].
 * 
 * @param items The [Items][Item] to add.
 */
@JvmName("plusAssignItems")
operator fun Gui.plusAssign(items: Iterable<Item>) = items.forEach { addItems(it) }