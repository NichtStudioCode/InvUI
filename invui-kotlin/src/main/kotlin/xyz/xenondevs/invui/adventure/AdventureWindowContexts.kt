@file:Suppress("PackageDirectoryMismatch")

package xyz.xenondevs.invui.window.type.context

import net.kyori.adventure.text.Component
import org.jetbrains.annotations.Contract
import xyz.xenondevs.inventoryaccess.component.AdventureComponentWrapper
import xyz.xenondevs.invui.window.Window

/**
 * Sets the title of the window.
 *
 * @param title the new title
 * @return This Window Builder
 */
@Contract("_ -> this")
fun <B : Window.Builder<*, B>> B.setTitle(title: Component): B = setTitle(AdventureComponentWrapper(title))