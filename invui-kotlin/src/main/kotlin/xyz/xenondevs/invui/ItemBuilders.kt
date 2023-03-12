@file:Suppress("PackageDirectoryMismatch")

package xyz.xenondevs.invui.item.builder

import net.md_5.bungee.api.chat.BaseComponent
import xyz.xenondevs.inventoryaccess.component.BungeeComponentWrapper

/**
 * Sets the lore of the item stack.
 */
fun <T : AbstractItemBuilder<T>> T.setLore(lore: List<Array<BaseComponent>>): T = setLore(lore.map { BungeeComponentWrapper(it) })