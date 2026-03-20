@file:OptIn(ExperimentalReactiveApi::class)

package xyz.xenondevs.invui.dsl

import net.kyori.adventure.key.Key
import org.bukkit.entity.Player
import xyz.xenondevs.commons.provider.Provider
import xyz.xenondevs.invui.ExperimentalReactiveApi
import xyz.xenondevs.invui.dsl.property.GuiDslProperty
import xyz.xenondevs.invui.window.CraftingWindow
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Creates a [CraftingWindow] using the DSL.
 *
 * A crafting window displays a crafting table GUI with a 3x3 crafting grid and a result slot (1x1).
 * Recipe book clicks can be handled via [onRecipeClick][CraftingWindowDsl.onRecipeClick].
 *
 * ```
 * val myWindow = craftingWindow(player) {
 *     title by "Crafting Table"
 *     craftingGui by gui(
 *         "a b c",
 *         "d e f",
 *         "g h i",
 *     ) {
 *         'a' by ingredient1
 *         // ...
 *     }
 *     resultGui by gui("r") { 'r' by resultItem }
 *
 *     onRecipeClick {
 *         // handle recipe book click for recipeKey
 *     }
 * }
 * ```
 *
 * @see CraftingWindowDsl
 */
@ExperimentalDslApi
inline fun craftingWindow(viewer: Player, craftingWindow: CraftingWindowDsl.() -> Unit): CraftingWindow {
    contract { callsInPlace(craftingWindow, InvocationKind.EXACTLY_ONCE) }
    return CraftingWindowDslImpl(viewer).apply(craftingWindow).build()
}

/**
 * DSL scope available inside [CraftingWindowDsl.onRecipeClick] and [FurnaceWindowDsl.onRecipeClick]
 * handlers, providing the recipe key that was clicked in the recipe book.
 *
 * ```
 * onRecipeClick {
 *     player.sendMessage("Clicked recipe: $recipeKey")
 * }
 * ```
 */
@WindowDslMarker
@ExperimentalDslApi
interface RecipeClickDsl {
    
    /** The [Key] identifying the recipe that was clicked in the recipe book. */
    val recipeKey: Key
    
}

/**
 * DSL scope for configuring a [CraftingWindow].
 *
 * Extends [SplitWindowDsl] with a 3x3 [craftingGui], a [resultGui], and an [onRecipeClick]
 * handler for recipe book interactions.
 *
 * ```
 * craftingWindow(player) {
 *     title by "Crafting Table"
 *     craftingGui by gui(
 *         "a b c",
 *         "d e f",
 *         "g h i",
 *     ) {
 *         'a' by ingredient1
 *         // ...
 *     }
 *     resultGui by gui("r") { 'r' by resultItem }
 * }
 * ```
 */
@ExperimentalDslApi
sealed interface CraftingWindowDsl : SplitWindowDsl {
    
    /**
     * A [Provider] that resolves to the built [CraftingWindow] instance.
     *
     * Can be used to obtain a reference to the window after the DSL block finishes and
     * the window is built. Accessing it before the window is built throws an
     * [IllegalStateException].
     */
    override val window: Provider<CraftingWindow>
    
    /**
     * The crafting grid GUI (3x3).
     *
     * ```
     * craftingGui by gui(
     *     "a b c",
     *     "d e f",
     *     "g h i",
     * ) {
     *     'a' by ingredient1
     *     // ...
     * }
     * ```
     */
    val craftingGui: GuiDslProperty
    
    /**
     * The result GUI (1x1).
     *
     * ```
     * resultGui by gui("r") { 'r' by resultItem }
     * ```
     */
    val resultGui: GuiDslProperty
    
    /**
     * Registers a handler that is called when the player clicks a recipe in the recipe book.
     * Multiple handlers can be registered and will all be called in order.
     *
     * ```
     * onRecipeClick {
     *     // recipeKey is the clicked recipe's key
     * }
     * ```
     *
     * @see RecipeClickDsl
     */
    fun onRecipeClick(handler: RecipeClickDsl.() -> Unit)
    
}

@PublishedApi
@ExperimentalDslApi
internal class CraftingWindowDslImpl(
    viewer: Player
) : AbstractSplitWindowDsl<CraftingWindow, CraftingWindow.Builder>(viewer), CraftingWindowDsl {
    
    override val craftingGui = GuiDslProperty(3, 3)
    override val resultGui = GuiDslProperty(1, 1)
    private val recipeClickHandlers = mutableListOf<RecipeClickDsl.() -> Unit>()
    
    override fun onRecipeClick(handler: RecipeClickDsl.() -> Unit) {
        recipeClickHandlers += handler
    }
    
    override fun createBuilder() = CraftingWindow.builder()
    
    override fun applyToBuilder(builder: CraftingWindow.Builder) {
        super.applyToBuilder(builder)
        builder.apply {
            setCraftingGui(craftingGui.value)
            setResultGui(resultGui.value)
            for (handler in recipeClickHandlers) {
                addRecipeClickHandler { RecipeClickDslImpl(it).handler() }
            }
        }
    }
    
}

@ExperimentalDslApi
internal class RecipeClickDslImpl(override val recipeKey: Key) : RecipeClickDsl