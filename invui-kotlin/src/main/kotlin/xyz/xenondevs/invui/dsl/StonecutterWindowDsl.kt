@file:OptIn(ExperimentalReactiveApi::class)

package xyz.xenondevs.invui.dsl

import org.bukkit.entity.Player
import xyz.xenondevs.invui.ExperimentalReactiveApi
import xyz.xenondevs.invui.dsl.property.GuiDslProperty
import xyz.xenondevs.invui.dsl.property.MutableProviderDslProperty
import xyz.xenondevs.invui.window.StonecutterWindow
import xyz.xenondevs.invui.window.setSelectedSlot

@ExperimentalDslApi
fun stonecutterWindow(viewer: Player, stonecutterWindow: StonecutterWindowDsl.() -> Unit): StonecutterWindow =
    StonecutterWindowDslImpl(viewer).apply(stonecutterWindow).build()

@ExperimentalDslApi
sealed interface StonecutterWindowDsl : SplitWindowDsl {
    
    val upperGui: GuiDslProperty
    val buttonsGui: GuiDslProperty
    val selectedSlot: MutableProviderDslProperty<Int>
    
}

@ExperimentalDslApi
internal class StonecutterWindowDslImpl(
    viewer: Player
) : AbstractSplitWindowDsl<StonecutterWindow, StonecutterWindow.Builder>(viewer), StonecutterWindowDsl {
    
    override val upperGui = GuiDslProperty(2, 1)
    override val buttonsGui = GuiDslProperty(4, 0, arbitraryHeight = true)
    override val selectedSlot = MutableProviderDslProperty(-1)
    
    override fun createBuilder() = StonecutterWindow.builder()
    
    override fun applyToBuilder(builder: StonecutterWindow.Builder) {
        super.applyToBuilder(builder)
        builder.apply {
            setUpperGui(upperGui.value)
            setButtonsGui(buttonsGui.value)
            setSelectedSlot(selectedSlot.delegate)
        }
    }
    
}