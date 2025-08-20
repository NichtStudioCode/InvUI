package xyz.xenondevs.invui.dsl

import org.bukkit.entity.Player
import xyz.xenondevs.invui.dsl.property.GuiDslProperty
import xyz.xenondevs.invui.window.SmithingWindow

@ExperimentalDslApi
fun smithingWindow(viewer: Player, smithingWindow: SmithingWindowDsl.() -> Unit): SmithingWindow =
    SmithingWindowDslImpl(viewer).apply(smithingWindow).build()

@ExperimentalDslApi
sealed interface SmithingWindowDsl : SplitWindowDsl {
    
    val upperGui: GuiDslProperty
    
}

@ExperimentalDslApi
internal class SmithingWindowDslImpl(
    viewer: Player
) : AbstractSplitWindowDsl<SmithingWindow, SmithingWindow.Builder>(viewer), SmithingWindowDsl {
    
    override val upperGui = GuiDslProperty(4, 1)
    
    override fun createBuilder() = SmithingWindow.builder()
    
    override fun applyToBuilder(builder: SmithingWindow.Builder) {
        super.applyToBuilder(builder)
        builder.apply {
            setUpperGui(upperGui.value)
        }
    }
    
}