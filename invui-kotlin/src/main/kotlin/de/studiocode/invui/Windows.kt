@file:Suppress("PackageDirectoryMismatch")

package de.studiocode.invui.window.type

import de.studiocode.invui.window.Window
import de.studiocode.invui.window.type.context.WindowContext

fun <W: Window, C : WindowContext> WindowType<W, C>.create(contextConsumer: C.() -> Unit): W {
    val ctx = createContext()
    ctx.contextConsumer()
    return createWindow(ctx)
}

fun main() {
    WindowType.NORMAL.create {  }
}