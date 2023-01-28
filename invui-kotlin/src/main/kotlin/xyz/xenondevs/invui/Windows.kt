@file:Suppress("PackageDirectoryMismatch")

package xyz.xenondevs.invui.window.type

import xyz.xenondevs.invui.window.Window
import xyz.xenondevs.invui.window.builder.WindowBuilder
import xyz.xenondevs.invui.window.builder.WindowType

fun <W : Window, B : WindowBuilder<W>> WindowType<W, B>.create(builderConsumer: B.() -> Unit): W {
    val builder = builder()
    builder.builderConsumer()
    return builder.build()
}