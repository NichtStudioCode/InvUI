@file:OptIn(ExperimentalReactiveApi::class)

package xyz.xenondevs.invui.dsl

import org.bukkit.entity.Player
import xyz.xenondevs.commons.provider.Provider
import xyz.xenondevs.invui.ExperimentalReactiveApi
import xyz.xenondevs.invui.dsl.property.GuiDslProperty
import xyz.xenondevs.invui.dsl.property.MutableProvider2dArrayDslProperty
import xyz.xenondevs.invui.window.CrafterWindow
import xyz.xenondevs.invui.window.setSlots
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@ExperimentalDslApi
inline fun crafterWindow(viewer: Player, crafterWindow: CrafterWindowDsl.() -> Unit): CrafterWindow {
    contract { callsInPlace(crafterWindow, InvocationKind.EXACTLY_ONCE) }
    return CrafterWindowDslImpl(viewer).apply(crafterWindow).build()
}

@ExperimentalDslApi
sealed interface CrafterWindowDsl : SplitWindowDsl {
    
    override val window: Provider<CrafterWindow>
    
    val craftingGui: GuiDslProperty
    val resultGui: GuiDslProperty
    val slots: MutableProvider2dArrayDslProperty<Boolean>
    
}

@PublishedApi
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