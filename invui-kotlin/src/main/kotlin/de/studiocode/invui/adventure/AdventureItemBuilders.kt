@file:Suppress("PackageDirectoryMismatch")

package de.studiocode.invui.item.builder

import de.studiocode.inventoryaccess.component.AdventureComponentWrapper
import net.kyori.adventure.text.Component

/**
 * Sets the display name of the item stack.
 */
fun <T> BaseItemBuilder<T>.setDisplayName(displayName: Component): T = setDisplayName(AdventureComponentWrapper(displayName))

/**
 * Sets the lore the item stack.
 */
fun <T> BaseItemBuilder<T>.setLore(lore: List<Component>): T = setLore(lore.map { AdventureComponentWrapper(it) })

/**
 * Adds lore lines to the item stack.
 */
fun <T> BaseItemBuilder<T>.addLoreLines(vararg components: Component): T = addLoreLines(*components.map { AdventureComponentWrapper(it) }.toTypedArray())