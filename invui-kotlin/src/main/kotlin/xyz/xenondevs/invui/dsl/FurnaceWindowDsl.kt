@file:OptIn(ExperimentalReactiveApi::class)

package xyz.xenondevs.invui.dsl

import org.bukkit.entity.Player
import xyz.xenondevs.invui.ExperimentalReactiveApi
import xyz.xenondevs.invui.dsl.property.GuiDslProperty
import xyz.xenondevs.invui.dsl.property.ProviderDslProperty
import xyz.xenondevs.invui.window.FurnaceWindow
import xyz.xenondevs.invui.window.setBurnProgress
import xyz.xenondevs.invui.window.setCookProgress

@ExperimentalDslApi
fun furnaceWindow(viewer: Player, furnaceWindow: FurnaceWindowDsl.() -> Unit): FurnaceWindow =
    FurnaceWindowDslImpl(viewer).apply(furnaceWindow).build()

@ExperimentalDslApi
sealed interface FurnaceWindowDsl : SplitWindowDsl {
    
    val inputGui: GuiDslProperty
    val resultGui: GuiDslProperty
    val cookProgress: ProviderDslProperty<Double>
    val burnProgress: ProviderDslProperty<Double>
    
    fun onRecipeClick(handler: RecipeClickDsl.() -> Unit)
    
}

@ExperimentalDslApi
internal class FurnaceWindowDslImpl(
    viewer: Player
) : AbstractSplitWindowDsl<FurnaceWindow, FurnaceWindow.Builder>(viewer), FurnaceWindowDsl {
    
    override val inputGui = GuiDslProperty(1, 2)
    override val resultGui = GuiDslProperty(1, 1)
    override val cookProgress = ProviderDslProperty(0.0)
    override val burnProgress = ProviderDslProperty(0.0)
    private val recipeClickHandlers = mutableListOf<RecipeClickDsl.() -> Unit>()
    
    override fun onRecipeClick(handler: RecipeClickDsl.() -> Unit) {
        recipeClickHandlers += handler
    }
    
    override fun createBuilder() = FurnaceWindow.builder()
    
    override fun applyToBuilder(builder: FurnaceWindow.Builder) {
        super.applyToBuilder(builder)
        builder.apply {
            setInputGui(inputGui.value)
            setResultGui(resultGui.value)
            setCookProgress(cookProgress)
            setBurnProgress(burnProgress)
            for (handler in recipeClickHandlers) {
                addRecipeClickHandler { RecipeClickDslImpl(it).handler() }
            }
        }
    }
    
}