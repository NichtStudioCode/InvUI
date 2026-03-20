package xyz.xenondevs.invui.dsl

import org.bukkit.entity.Player
import xyz.xenondevs.commons.provider.Provider
import xyz.xenondevs.invui.dsl.property.GuiDslProperty
import xyz.xenondevs.invui.window.GrindstoneWindow
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Creates a [GrindstoneWindow] using the DSL.
 *
 * A grindstone window displays a grindstone GUI with an input area (1x2) for the two input
 * items and a result slot (1x1).
 *
 * ```
 * val myWindow = grindstoneWindow(player) {
 *     title by "Grindstone"
 *     inputGui by gui("a", "b") {
 *         'a' by inputItem1
 *         'b' by inputItem2
 *     }
 *     resultGui by gui("r") { 'r' by resultItem }
 * }
 * ```
 *
 * @see GrindstoneWindowDsl
 */
@ExperimentalDslApi
inline fun grindstoneWindow(viewer: Player, grindstoneWindow: GrindstoneWindowDsl.() -> Unit): GrindstoneWindow {
    contract { callsInPlace(grindstoneWindow, InvocationKind.EXACTLY_ONCE) }
    return GrindstoneWindowDslImpl(viewer).apply(grindstoneWindow).build()
}

/**
 * DSL scope for configuring a [GrindstoneWindow].
 *
 * Extends [SplitWindowDsl] with grindstone-specific GUIs ([inputGui], [resultGui]).
 *
 * ```
 * grindstoneWindow(player) {
 *     title by "Grindstone"
 *     inputGui by gui("a", "b") {
 *         'a' by inputItem1
 *         'b' by inputItem2
 *     }
 *     resultGui by gui("r") { 'r' by resultItem }
 * }
 * ```
 */
@ExperimentalDslApi
sealed interface GrindstoneWindowDsl : SplitWindowDsl {
    
    /**
     * A [Provider] that resolves to the built [GrindstoneWindow] instance.
     *
     * Can be used to obtain a reference to the window after the DSL block finishes and
     * the window is built. Accessing it before the window is built throws an
     * [IllegalStateException].
     */
    override val window: Provider<GrindstoneWindow>
    
    /**
     * The input GUI (1x2) for the two grindstone input slots.
     *
     * ```
     * inputGui by gui("a", "b") {
     *     'a' by inputItem1
     *     'b' by inputItem2
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
    
}

@PublishedApi
@ExperimentalDslApi
internal class GrindstoneWindowDslImpl(
    viewer: Player
) : AbstractSplitWindowDsl<GrindstoneWindow, GrindstoneWindow.Builder>(viewer), GrindstoneWindowDsl {
    
    override val inputGui = GuiDslProperty(1, 2)
    override val resultGui = GuiDslProperty(1, 1)
    
    override fun createBuilder() = GrindstoneWindow.builder()
    
    override fun applyToBuilder(builder: GrindstoneWindow.Builder) {
        super.applyToBuilder(builder)
        builder.apply {
            setInputGui(inputGui.value)
            setResultGui(resultGui.value)
        }
    }
    
}