@file:OptIn(ExperimentalReactiveApi::class)

package xyz.xenondevs.invui.dsl

import org.bukkit.entity.Player
import xyz.xenondevs.commons.provider.Provider
import xyz.xenondevs.commons.provider.provider
import xyz.xenondevs.invui.ExperimentalReactiveApi
import xyz.xenondevs.invui.dsl.property.GuiDslProperty
import xyz.xenondevs.invui.dsl.property.ProviderDslProperty
import xyz.xenondevs.invui.window.BrewingWindow
import xyz.xenondevs.invui.window.setBrewProgress
import xyz.xenondevs.invui.window.setFuelProgress
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@ExperimentalDslApi
inline fun brewingWindow(viewer: Player, brewingWindow: BrewingWindowDsl.() -> Unit): BrewingWindow {
    contract { callsInPlace(brewingWindow, InvocationKind.EXACTLY_ONCE) }
    return BrewingWindowDslImpl(viewer).apply(brewingWindow).build()
}

@ExperimentalDslApi
sealed interface BrewingWindowDsl : SplitWindowDsl {
    
    override val window: Provider<BrewingWindow>
    
    val inputGui: GuiDslProperty
    val fuelGui: GuiDslProperty
    val resultGui: GuiDslProperty
    val brewProgress: ProviderDslProperty<Double>
    val fuelProgress: ProviderDslProperty<Double>
    
}

@PublishedApi
@ExperimentalDslApi
internal class BrewingWindowDslImpl(
    viewer: Player
) : AbstractSplitWindowDsl<BrewingWindow, BrewingWindow.Builder>(viewer), BrewingWindowDsl {
    
    private var _brewProgress = provider(0.0)
    private var _fuelProgress = provider(0.0)
    
    override val inputGui = GuiDslProperty(1, 1)
    override val fuelGui = GuiDslProperty(1, 1)
    override val resultGui = GuiDslProperty(3, 1)
    override val brewProgress: ProviderDslProperty<Double>
        get() = ProviderDslProperty(::_brewProgress)
    override val fuelProgress: ProviderDslProperty<Double>
        get() = ProviderDslProperty(::_fuelProgress)
    
    override fun createBuilder() = BrewingWindow.builder()
    
    override fun applyToBuilder(builder: BrewingWindow.Builder) {
        super.applyToBuilder(builder)
        builder.apply {
            setInputGui(inputGui.value)
            setFuelGui(fuelGui.value)
            setResultGui(resultGui.value)
            setBrewProgress(_brewProgress)
            setFuelProgress(_fuelProgress)
        }
    }
    
}