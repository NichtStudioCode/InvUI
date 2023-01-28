@file:Suppress("PackageDirectoryMismatch")

package de.studiocode.invui.window.type.context

import de.studiocode.inventoryaccess.component.AdventureComponentWrapper
import net.kyori.adventure.text.Component

/**
 * Sets the title of the window.
 */
fun AbstractWindowContext<*>.setTitle(title: Component) = setTitle(AdventureComponentWrapper(title))