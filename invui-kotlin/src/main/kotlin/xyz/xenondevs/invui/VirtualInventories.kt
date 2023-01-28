@file:Suppress("PackageDirectoryMismatch")

package xyz.xenondevs.invui.virtualinventory

import org.bukkit.inventory.ItemStack

/**
 * Gets a copy of [ItemStack] placed on that [slot].
 */
operator fun VirtualInventory.get(slot: Int): ItemStack? = getItemStack(slot)

/**
 * Adds the given [items] to the inventory.
 */
operator fun VirtualInventory.plusAssign(items: Iterable<ItemStack>) {
    items.forEach { addItem(null, it) }
}

/**
 * Adds the given [item] to the inventory.
 */
operator fun VirtualInventory.plusAssign(item: ItemStack) {
    addItem(null, item)
}

/**
 * Checks if the [VirtualInventory] contains an [ItemStack] similar to the given [item].
 */
operator fun VirtualInventory.contains(item: ItemStack) = containsSimilar(item)