@file:OptIn(ExperimentalReactiveApi::class)

package xyz.xenondevs.invui.dsl

import org.bukkit.entity.Player
import xyz.xenondevs.commons.provider.Provider
import xyz.xenondevs.commons.provider.provider
import xyz.xenondevs.invui.ExperimentalReactiveApi
import xyz.xenondevs.invui.window.BrewingWindow
import xyz.xenondevs.invui.window.setBrewProgress
import xyz.xenondevs.invui.window.setFuelProgress
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Creates a [BrewingWindow] using the DSL.
 *
 * A brewing window displays a brewing stand GUI with separate areas for the ingredient input
 * (1x1), fuel (1x1), and result bottles (3x1).
 *
 * ```
 * val myWindow = brewingWindow(player) {
 *     title by "Brewing Stand"
 *     inputGui by gui("i") { 'i' by ingredientItem }
 *     fuelGui by gui("f") { 'f' by fuelItem }
 *     resultGui by gui("a b c") {
 *         'a' by bottleItem1
 *         'b' by bottleItem2
 *         'c' by bottleItem3
 *     }
 *     brewProgress by 0.5
 *     fuelProgress by 0.75
 * }
 * ```
 *
 * @see BrewingWindowDsl
 */
@ExperimentalDslApi
inline fun brewingWindow(viewer: Player, brewingWindow: BrewingWindowDsl.() -> Unit): BrewingWindow {
    contract { callsInPlace(brewingWindow, InvocationKind.EXACTLY_ONCE) }
    return BrewingWindowDslImpl(viewer).apply(brewingWindow).build()
}

/**
 * DSL scope for configuring a [BrewingWindow].
 *
 * Extends [SplitWindowDsl] with brewing-specific GUIs ([inputGui], [fuelGui], [resultGui])
 * and progress indicators ([brewProgress], [fuelProgress]).
 *
 * ```
 * brewingWindow(player) {
 *     title by "Brewing Stand"
 *     inputGui by gui("i") { 'i' by ingredientItem }
 *     fuelGui by gui("f") { 'f' by fuelItem }
 *     resultGui by gui("a b c") {
 *         'a' by bottleItem1
 *         'b' by bottleItem2
 *         'c' by bottleItem3
 *     }
 *     brewProgress by 0.5
 *     fuelProgress by 0.75
 * }
 * ```
 */
@ExperimentalDslApi
sealed interface BrewingWindowDsl : SplitWindowDsl {
    
    /**
     * A [Provider] that resolves to the built [BrewingWindow] instance.
     *
     * Can be used to obtain a reference to the window after the DSL block finishes and
     * the window is built. Accessing it before the window is built throws an
     * [IllegalStateException].
     */
    override val window: Provider<BrewingWindow>
    
    /**
     * The ingredient input GUI (1x1).
     *
     * ```
     * inputGui by gui("i") { 'i' by ingredientItem }
     * ```
     */
    val inputGui: GuiDslProperty
    
    /**
     * The fuel GUI (1x1).
     *
     * ```
     * fuelGui by gui("f") { 'f' by fuelItem }
     * ```
     */
    val fuelGui: GuiDslProperty
    
    /**
     * The result bottles GUI (3x1).
     *
     * ```
     * resultGui by gui("a b c") {
     *     'a' by bottleItem1
     *     'b' by bottleItem2
     *     'c' by bottleItem3
     * }
     * ```
     */
    val resultGui: GuiDslProperty
    
    /**
     * The brewing progress as a value from `0.0` (not started) to `1.0` (complete).
     *
     * Defaults to `0.0`. Can be set to a static value or bound to a [Provider]:
     * ```
     * brewProgress by 0.5
     * ```
     */
    val brewProgress: ProviderDslProperty<Double>
    
    /**
     * The fuel progress as a value from `0.0` (empty) to `1.0` (full).
     *
     * Defaults to `0.0`. Can be set to a static value or bound to a [Provider]:
     * ```
     * fuelProgress by 0.75
     * ```
     */
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