@file:Suppress("PackageDirectoryMismatch")

package xyz.xenondevs.invui.window

import net.kyori.adventure.text.Component
import xyz.xenondevs.inventoryaccess.component.AdventureComponentWrapper

fun Window.changeTitle(title: Component) {
    changeTitle(AdventureComponentWrapper(title))
}