@file:Suppress("PackageDirectoryMismatch")

package de.studiocode.invui.item.builder

import de.studiocode.inventoryaccess.component.BaseComponentWrapper
import de.studiocode.invui.util.ComponentUtils
import net.md_5.bungee.api.chat.BaseComponent

/**
 * Sets the lore of the item stack.
 */
fun <T> BaseItemBuilder<T>.setLore(lore: List<Array<BaseComponent>>): T = setLore(lore.map { BaseComponentWrapper(ComponentUtils.withoutPreFormatting(*it)) })