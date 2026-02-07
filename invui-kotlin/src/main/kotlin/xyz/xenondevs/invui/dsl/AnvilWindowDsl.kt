@file:OptIn(ExperimentalReactiveApi::class)

package xyz.xenondevs.invui.dsl

import org.bukkit.entity.Player
import xyz.xenondevs.commons.provider.Provider
import xyz.xenondevs.commons.provider.mutableProvider
import xyz.xenondevs.commons.provider.provider
import xyz.xenondevs.invui.ExperimentalReactiveApi
import xyz.xenondevs.invui.dsl.property.GuiDslProperty
import xyz.xenondevs.invui.dsl.property.ProviderDslProperty
import xyz.xenondevs.invui.window.AnvilWindow
import xyz.xenondevs.invui.window.addRenameHandler
import xyz.xenondevs.invui.window.setResultAlwaysValid
import xyz.xenondevs.invui.window.setTextFieldAlwaysEnabled

@ExperimentalDslApi
fun anvilWindow(viewer: Player, anvilWindow: AnvilWindowDsl.() -> Unit): AnvilWindow =
    AnvilWindowDslImpl(viewer).apply(anvilWindow).build()

@ExperimentalDslApi
sealed interface AnvilWindowDsl : SplitWindowDsl {
    
    val upperGui: GuiDslProperty
    val text: Provider<String>
    val textFieldAlwaysEnabled: ProviderDslProperty<Boolean>
    val resultAlwaysValid: ProviderDslProperty<Boolean>
    
}

@ExperimentalDslApi
internal class AnvilWindowDslImpl(
    viewer: Player
) : AbstractSplitWindowDsl<AnvilWindow, AnvilWindow.Builder>(viewer), AnvilWindowDsl {
    
    private var _textFieldAlwaysEnabled = provider(true)
    private var _resultAlwaysValid = provider(false)
    
    override val upperGui = GuiDslProperty(3, 1)
    override val text = mutableProvider("")
    override val textFieldAlwaysEnabled: ProviderDslProperty<Boolean>
        get() = ProviderDslProperty(::_textFieldAlwaysEnabled)
    override val resultAlwaysValid: ProviderDslProperty<Boolean>
        get() = ProviderDslProperty(::_resultAlwaysValid)
    
    override fun createBuilder() = AnvilWindow.builder()
    
    override fun applyToBuilder(builder: AnvilWindow.Builder) {
        super.applyToBuilder(builder)
        builder.apply {
            setUpperGui(upperGui.value)
            addRenameHandler(text)
            setTextFieldAlwaysEnabled(_textFieldAlwaysEnabled)
            setResultAlwaysValid(_resultAlwaysValid)
        }
    }
    
}