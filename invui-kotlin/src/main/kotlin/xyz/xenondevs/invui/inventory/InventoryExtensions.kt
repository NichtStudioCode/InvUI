package xyz.xenondevs.invui.inventory

import org.bukkit.inventory.ItemStack
import org.jetbrains.annotations.ApiStatus
import xyz.xenondevs.invui.item.ItemProvider
import java.util.function.Function

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
operator fun Inventory.contains(item: ItemStack): Boolean =
    containsSimilar(item)

/**
 * Creates an [ObscuredInventory] of only the slots in the given [range].
 */
operator fun Inventory.get(range: IntRange): ObscuredInventory =
    ObscuredInventory(this) { it !in range }

/**
 * Sets the visualizer of this inventory.
 */
@ApiStatus.Experimental
fun Inventory.setVisualizer(visualizer: ((ItemStack?) -> ItemProvider?)?) {
    this.visualizer = if (visualizer != null) Function { itemStack -> visualizer(itemStack) } else null
}