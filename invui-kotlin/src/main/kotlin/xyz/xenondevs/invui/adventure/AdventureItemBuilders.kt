@file:Suppress("PackageDirectoryMismatch")

package xyz.xenondevs.invui.item.builder

import net.kyori.adventure.text.Component
import xyz.xenondevs.inventoryaccess.component.AdventureComponentWrapper

/**
 * Sets the display name of the item stack.
 */
fun <T> AbstractItemBuilder<T>.setDisplayName(displayName: Component): T = setDisplayName(AdventureComponentWrapper(displayName))

/**
 * Sets the lore the item stack.
 */
fun <T> AbstractItemBuilder<T>.setLore(lore: List<Component>): T = setLore(lore.map { AdventureComponentWrapper(it) })

/**
 * Adds lore lines to the item stack.
 */
fun <T> AbstractItemBuilder<T>.addLoreLines(vararg components: Component): T = addLoreLines(*components.map { AdventureComponentWrapper(it) }.toTypedArray())