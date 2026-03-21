@file:OptIn(ExperimentalReactiveApi::class)

package xyz.xenondevs.invui.dsl

import org.bukkit.entity.Player
import xyz.xenondevs.commons.provider.Provider
import xyz.xenondevs.commons.provider.provider
import xyz.xenondevs.invui.ExperimentalReactiveApi
import xyz.xenondevs.invui.window.FurnaceWindow
import xyz.xenondevs.invui.window.setBurnProgress
import xyz.xenondevs.invui.window.setCookProgress
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Creates a [FurnaceWindow] using the DSL.
 *
 * A furnace window displays a furnace GUI with an input area (1x2, for the item and fuel slots)
 * and a result slot (1x1), along with progress indicators for cooking and burning.
 *
 * ```
 * val myWindow = furnaceWindow(player) {
 *     title by "Furnace"
 *     inputGui by gui("i", "f") {
 *         'i' by smeltableItem
 *         'f' by fuelItem
 *     }
 *     resultGui by gui("r") { 'r' by resultItem }
 *     cookProgress by 0.5
 *     burnProgress by 0.75
 * }
 * ```
 *
 * @see FurnaceWindowDsl
 */
@ExperimentalDslApi
inline fun furnaceWindow(viewer: Player, furnaceWindow: FurnaceWindowDsl.() -> Unit): FurnaceWindow {
    contract { callsInPlace(furnaceWindow, InvocationKind.EXACTLY_ONCE) }
    return FurnaceWindowDslImpl(viewer).apply(furnaceWindow).build()
}

/**
 * DSL scope for configuring a [FurnaceWindow].
 *
 * Extends [SplitWindowDsl] with furnace-specific GUIs ([inputGui], [resultGui]), progress
 * indicators ([cookProgress], [burnProgress]), and an [onRecipeClick] handler for recipe book
 * interactions.
 *
 * ```
 * furnaceWindow(player) {
 *     title by "Furnace"
 *     inputGui by gui("i", "f") {
 *         'i' by smeltableItem
 *         'f' by fuelItem
 *     }
 *     resultGui by gui("r") { 'r' by resultItem }
 *     cookProgress by 0.5
 *     burnProgress by 0.75
 * }
 * ```
 */
@ExperimentalDslApi
sealed interface FurnaceWindowDsl : SplitWindowDsl {
    
    /**
     * A [Provider] that resolves to the built [FurnaceWindow] instance.
     *
     * Can be used to obtain a reference to the window after the DSL block finishes and
     * the window is built. Accessing it before the window is built throws an
     * [IllegalStateException].
     */
    override val window: Provider<FurnaceWindow>
    
    /**
     * The input GUI (1x2) for the item-to-smelt and fuel slots.
     *
     * ```
     * inputGui by gui("i", "f") {
     *     'i' by smeltableItem
     *     'f' by fuelItem
     * }
     * ```
     */
    val inputGui: GuiDslProperty
    
    /**
     * The result GUI (1x1).
     *
     * ```
     * resultGui by gui("r") { 'r' by resultItem }
     * ```
     */
    val resultGui: GuiDslProperty
    
    /**
     * The cooking progress as a value from `0.0` (not started) to `1.0` (complete).
     *
     * Defaults to `0.0`. Can be set to a static value or bound to a [Provider]:
     * ```
     * cookProgress by 0.5
     * ```
     */
    val cookProgress: ProviderDslProperty<Double>
    
    /**
     * The burn progress as a value from `0.0` (no fuel remaining) to `1.0` (full fuel).
     *
     * Defaults to `0.0`. Can be set to a static value or bound to a [Provider]:
     * ```
     * burnProgress by 0.75
     * ```
     */
    val burnProgress: ProviderDslProperty<Double>
    
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
internal class FurnaceWindowDslImpl(
    viewer: Player
) : AbstractSplitWindowDsl<FurnaceWindow, FurnaceWindow.Builder>(viewer), FurnaceWindowDsl {
    
    private var _cookProgress = provider(0.0)
    private var _burnProgress = provider(0.0)
    
    override val inputGui = GuiDslProperty(1, 2)
    override val resultGui = GuiDslProperty(1, 1)
    override val cookProgress: ProviderDslProperty<Double>
        get() = ProviderDslProperty(::_cookProgress)
    override val burnProgress: ProviderDslProperty<Double>
        get() = ProviderDslProperty(::_burnProgress)
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
            setCookProgress(_cookProgress)
            setBurnProgress(_burnProgress)
            for (handler in recipeClickHandlers) {
                addRecipeClickHandler { RecipeClickDslImpl(it).handler() }
            }
        }
    }
    
}