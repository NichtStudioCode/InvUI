@file:OptIn(ExperimentalReactiveApi::class)

package xyz.xenondevs.invui.dsl

import org.bukkit.entity.Player
import xyz.xenondevs.commons.provider.mutableProvider
import xyz.xenondevs.invui.ExperimentalReactiveApi
import xyz.xenondevs.invui.dsl.property.GuiDslProperty
import xyz.xenondevs.invui.dsl.property.MutableProviderDslProperty
import xyz.xenondevs.invui.window.StonecutterWindow
import xyz.xenondevs.invui.window.setSelectedSlot
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@ExperimentalDslApi
inline fun stonecutterWindow(viewer: Player, stonecutterWindow: StonecutterWindowDsl.() -> Unit): StonecutterWindow {
    contract { callsInPlace(stonecutterWindow, InvocationKind.EXACTLY_ONCE) }
    return StonecutterWindowDslImpl(viewer).apply(stonecutterWindow).build()
}

@ExperimentalDslApi
sealed interface StonecutterWindowDsl : SplitWindowDsl {
    
    val upperGui: GuiDslProperty
    val buttonsGui: GuiDslProperty
    val selectedSlot: MutableProviderDslProperty<Int>
    
}

@PublishedApi
@ExperimentalDslApi
internal class StonecutterWindowDslImpl(
    viewer: Player
) : AbstractSplitWindowDsl<StonecutterWindow, StonecutterWindow.Builder>(viewer), StonecutterWindowDsl {
    
    private var _selectedSlot = mutableProvider(-1)
    
    override val upperGui = GuiDslProperty(2, 1)
    override val buttonsGui = GuiDslProperty(4, 0, arbitraryHeight = true)
    override val selectedSlot: MutableProviderDslProperty<Int>
        get() = MutableProviderDslProperty(::_selectedSlot)
    
    override fun createBuilder() = StonecutterWindow.builder()
    
    override fun applyToBuilder(builder: StonecutterWindow.Builder) {
        super.applyToBuilder(builder)
        builder.apply {
            setUpperGui(upperGui.value)
            setButtonsGui(buttonsGui.value)
            setSelectedSlot(_selectedSlot)
        }
    }
    
}