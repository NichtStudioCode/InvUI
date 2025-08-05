@file:OptIn(ExperimentalReactiveApi::class)

package xyz.xenondevs.invui.dsl

import net.kyori.adventure.key.Key
import org.bukkit.entity.Player
import xyz.xenondevs.invui.ExperimentalReactiveApi
import xyz.xenondevs.invui.window.CraftingWindow

@ExperimentalDslApi
fun craftingWindow(viewer: Player, craftingWindow: CraftingWindowDsl.() -> Unit): CraftingWindow =
    CraftingWindowDslImpl(viewer).apply(craftingWindow).build()

@ExperimentalDslApi
sealed interface CraftingWindowDsl : SplitWindowDsl {
    
    val craftingGui: GuiDslProperty
    val resultGui: GuiDslProperty
    
    fun onRecipeClick(handler: (recipeKey: Key) -> Unit)
    
}

@ExperimentalDslApi
internal class CraftingWindowDslImpl(
    viewer: Player
) : AbstractSplitWindowDsl<CraftingWindow, CraftingWindow.Builder>(viewer), CraftingWindowDsl {
    
    override val craftingGui = GuiDslProperty(3, 3)
    override val resultGui = GuiDslProperty(1, 1)
    private val recipeClickHandlers = mutableListOf<(Key) -> Unit>()
    
    override fun onRecipeClick(handler: (Key) -> Unit) {
        recipeClickHandlers += handler
    }
    
    override fun createBuilder() = CraftingWindow.builder()
    
    override fun applyToBuilder(builder: CraftingWindow.Builder) {
        super.applyToBuilder(builder)
        builder.apply {
            setCraftingGui(craftingGui.value)
            setResultGui(resultGui.value)
            recipeClickHandlers.forEach { addRecipeClickHandler(it) }
        }
    }
    
}