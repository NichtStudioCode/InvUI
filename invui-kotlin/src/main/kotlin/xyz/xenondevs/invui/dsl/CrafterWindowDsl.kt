@file:OptIn(ExperimentalReactiveApi::class)

package xyz.xenondevs.invui.dsl

import org.bukkit.entity.Player
import xyz.xenondevs.commons.provider.MutableProvider
import xyz.xenondevs.commons.provider.mutableProvider
import xyz.xenondevs.invui.ExperimentalReactiveApi
import xyz.xenondevs.invui.window.CrafterWindow
import xyz.xenondevs.invui.window.setSlots

@ExperimentalDslApi
fun crafterWindow(viewer: Player, crafterWindow: CrafterWindowDsl.() -> Unit): CrafterWindow =
    CrafterWindowDslImpl(viewer).apply(crafterWindow).build()

@ExperimentalDslApi
sealed interface CrafterWindowDsl : SplitWindowDsl {
    
    val craftingGui: GuiDslProperty
    val resultGui: GuiDslProperty
    val slots: MutableProvider2dArrayDslProperty<Boolean>
    
}

@ExperimentalDslApi
internal class CrafterWindowDslImpl(
    viewer: Player
) : AbstractSplitWindowDsl<CrafterWindow, CrafterWindow.Builder>(viewer), CrafterWindowDsl {
    
    override val craftingGui = GuiDslProperty(3, 3)
    override val resultGui = GuiDslProperty(1, 1)
    override val slots = MutableProvider2dArrayDslProperty(3, 3, false)
    
    override fun createBuilder() = CrafterWindow.builder()
    
    override fun applyToBuilder(builder: CrafterWindow.Builder) {
        super.applyToBuilder(builder)
        builder.apply {
            setCraftingGui(craftingGui.value)
            setResultGui(resultGui.value)
            setSlots(slots.value)
        }
    }
    
}