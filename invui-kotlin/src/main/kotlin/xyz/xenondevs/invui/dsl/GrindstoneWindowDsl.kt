package xyz.xenondevs.invui.dsl

import org.bukkit.entity.Player
import xyz.xenondevs.invui.dsl.property.GuiDslProperty
import xyz.xenondevs.invui.window.GrindstoneWindow

@ExperimentalDslApi
fun grindstoneWindow(viewer: Player, grindstoneWindow: GrindstoneWindowDsl.() -> Unit): GrindstoneWindow =
    GrindstoneWindowDslImpl(viewer).apply(grindstoneWindow).build()

@ExperimentalDslApi
sealed interface GrindstoneWindowDsl : SplitWindowDsl {
    
    val inputGui: GuiDslProperty
    val resultGui: GuiDslProperty
    
}

@ExperimentalDslApi
internal class GrindstoneWindowDslImpl(
    viewer: Player
) : AbstractSplitWindowDsl<GrindstoneWindow, GrindstoneWindow.Builder>(viewer), GrindstoneWindowDsl {
    
    override val inputGui = GuiDslProperty(1, 2)
    override val resultGui = GuiDslProperty(1, 1)
    
    override fun createBuilder() = GrindstoneWindow.builder()
    
    override fun applyToBuilder(builder: GrindstoneWindow.Builder) {
        super.applyToBuilder(builder)
        builder.apply {
            setInputGui(inputGui.value)
            setResultGui(resultGui.value)
        }
    }
    
}