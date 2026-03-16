package xyz.xenondevs.invui.dsl

import org.bukkit.entity.Player
import xyz.xenondevs.commons.provider.Provider
import xyz.xenondevs.invui.dsl.property.GuiDslProperty
import xyz.xenondevs.invui.window.GrindstoneWindow
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@ExperimentalDslApi
inline fun grindstoneWindow(viewer: Player, grindstoneWindow: GrindstoneWindowDsl.() -> Unit): GrindstoneWindow {
    contract { callsInPlace(grindstoneWindow, InvocationKind.EXACTLY_ONCE) }
    return GrindstoneWindowDslImpl(viewer).apply(grindstoneWindow).build()
}

@ExperimentalDslApi
sealed interface GrindstoneWindowDsl : SplitWindowDsl {
    
    override val window: Provider<GrindstoneWindow>
    
    val inputGui: GuiDslProperty
    val resultGui: GuiDslProperty
    
}

@PublishedApi
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