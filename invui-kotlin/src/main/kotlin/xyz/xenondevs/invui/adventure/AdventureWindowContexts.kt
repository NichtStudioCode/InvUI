@file:Suppress("PackageDirectoryMismatch")

package xyz.xenondevs.invui.window.type.context

import net.kyori.adventure.text.Component
import xyz.xenondevs.inventoryaccess.component.AdventureComponentWrapper
import xyz.xenondevs.invui.window.builder.AbstractWindowBuilder

/**
 * Sets the title of the window.
 */
fun AbstractWindowBuilder<*, *, *>.setTitle(title: Component) = setTitle(AdventureComponentWrapper(title))