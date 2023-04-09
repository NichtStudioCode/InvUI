@file:Suppress("PackageDirectoryMismatch")

package xyz.xenondevs.invui.inventory

import org.bukkit.inventory.ItemStack

/**
 * Gets a copy of [ItemStack] placed on that [slot].
 */
operator fun Inventory.get(slot: Int): ItemStack? = getItem(slot)

/**
 * Adds the given [items] to the inventory.
 */
operator fun Inventory.plusAssign(items: Iterable<ItemStack>) {
    items.forEach { addItem(null, it) }
}

/**
 * Adds the given [item] to the inventory.
 */
operator fun Inventory.plusAssign(item: ItemStack) {
    addItem(null, item)
}

/**
 * Checks if the [Inventory] contains an [ItemStack] similar to the given [item].
 */
operator fun Inventory.contains(item: ItemStack) = containsSimilar(item)