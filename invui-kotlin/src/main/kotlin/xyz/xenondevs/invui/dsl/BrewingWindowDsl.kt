@file:OptIn(ExperimentalReactiveApi::class)

package xyz.xenondevs.invui.dsl

import org.bukkit.entity.Player
import xyz.xenondevs.invui.ExperimentalReactiveApi
import xyz.xenondevs.invui.dsl.property.GuiDslProperty
import xyz.xenondevs.invui.dsl.property.ProviderDslProperty
import xyz.xenondevs.invui.window.BrewingWindow
import xyz.xenondevs.invui.window.setBrewProgress
import xyz.xenondevs.invui.window.setFuelProgress

@ExperimentalDslApi
fun brewingWindow(viewer: Player, brewingWindow: BrewingWindowDsl.() -> Unit): BrewingWindow =
    BrewingWindowDslImpl(viewer).apply(brewingWindow).build()

@ExperimentalDslApi
sealed interface BrewingWindowDsl : SplitWindowDsl {
    
    val inputGui: GuiDslProperty
    val fuelGui: GuiDslProperty
    val resultGui: GuiDslProperty
    val brewProgress: ProviderDslProperty<Double>
    val fuelProgress: ProviderDslProperty<Double>
    
}

@ExperimentalDslApi
internal class BrewingWindowDslImpl(
    viewer: Player
) : AbstractSplitWindowDsl<BrewingWindow, BrewingWindow.Builder>(viewer), BrewingWindowDsl {
    
    override val inputGui = GuiDslProperty(1, 1)
    override val fuelGui = GuiDslProperty(1, 1)
    override val resultGui = GuiDslProperty(3, 1)
    override val brewProgress = ProviderDslProperty(0.0)
    override val fuelProgress = ProviderDslProperty(0.0)
    
    override fun createBuilder() = BrewingWindow.builder()
    
    override fun applyToBuilder(builder: BrewingWindow.Builder) {
        super.applyToBuilder(builder)
        builder.apply { 
            setInputGui(inputGui.value)
            setFuelGui(fuelGui.value)
            setResultGui(resultGui.value)
            setBrewProgress(brewProgress)
            setFuelProgress(fuelProgress)
        }
    }
    
}