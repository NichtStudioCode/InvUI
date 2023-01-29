@file:Suppress("PackageDirectoryMismatch")

package xyz.xenondevs.invui.window.builder

import xyz.xenondevs.invui.window.Window

fun <W : Window, B : WindowBuilder<W>> WindowType<W, B>.create(builderConsumer: B.() -> Unit): W {
    val builder = builder()
    builder.builderConsumer()
    return builder.build()
}