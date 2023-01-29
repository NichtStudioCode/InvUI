@file:Suppress("PackageDirectoryMismatch")

package xyz.xenondevs.invui.gui.builder

import xyz.xenondevs.invui.gui.Gui

fun <G : Gui, B : GuiBuilder<G>> GuiType<G, B>.create(builderConsumer: B.() -> Unit): G {
    val builder = builder()
    builder.builderConsumer()
    return builder.build()
}