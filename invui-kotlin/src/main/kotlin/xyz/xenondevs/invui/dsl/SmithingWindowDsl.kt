package xyz.xenondevs.invui.dsl

import org.bukkit.entity.Player
import xyz.xenondevs.commons.provider.Provider
import xyz.xenondevs.invui.dsl.property.GuiDslProperty
import xyz.xenondevs.invui.window.SmithingWindow
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@ExperimentalDslApi
inline fun smithingWindow(viewer: Player, smithingWindow: SmithingWindowDsl.() -> Unit): SmithingWindow {
    contract { callsInPlace(smithingWindow, InvocationKind.EXACTLY_ONCE) }
    return SmithingWindowDslImpl(viewer).apply(smithingWindow).build()
}

@ExperimentalDslApi
sealed interface SmithingWindowDsl : SplitWindowDsl {
    
    override val window: Provider<SmithingWindow>
    
    val upperGui: GuiDslProperty
    
}

@PublishedApi
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