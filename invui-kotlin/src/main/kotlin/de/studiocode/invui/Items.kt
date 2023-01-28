@file:Suppress("PackageDirectoryMismatch")

package de.studiocode.invui.item

/**
 * Calls [Item.notifyWindows] for all items in this [Iterable].
 */
fun Iterable<Item>.notifyWindows() = forEach { it.notifyWindows() }