@file:Suppress("PackageDirectoryMismatch")

package xyz.xenondevs.invui.window.type

import xyz.xenondevs.invui.window.Window
import xyz.xenondevs.invui.window.type.context.WindowContext

fun <W : Window, C : WindowContext> WindowType<W, C>.create(contextConsumer: C.() -> Unit): W {
    val ctx = createContext()
    ctx.contextConsumer()
    return createWindow(ctx)
}