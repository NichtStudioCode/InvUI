@file:Suppress("PackageDirectoryMismatch")

package xyz.xenondevs.invui.item.builder

import net.md_5.bungee.api.chat.BaseComponent
import xyz.xenondevs.inventoryaccess.component.BaseComponentWrapper
import xyz.xenondevs.invui.util.ComponentUtils

/**
 * Sets the lore of the item stack.
 */
fun <T> AbstractItemBuilder<T>.setLore(lore: List<Array<BaseComponent>>): T = setLore(lore.map { BaseComponentWrapper(ComponentUtils.withoutPreFormatting(*it)) })