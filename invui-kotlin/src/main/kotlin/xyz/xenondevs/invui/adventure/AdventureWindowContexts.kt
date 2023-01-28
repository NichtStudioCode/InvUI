@file:Suppress("PackageDirectoryMismatch")

package xyz.xenondevs.invui.window.type.context

import net.kyori.adventure.text.Component
import xyz.xenondevs.inventoryaccess.component.AdventureComponentWrapper

/**
 * Sets the title of the window.
 */
fun AbstractWindowContext<*>.setTitle(title: Component) = setTitle(AdventureComponentWrapper(title))