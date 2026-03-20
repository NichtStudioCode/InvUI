@file:OptIn(ExperimentalReactiveApi::class)

package xyz.xenondevs.invui.dsl

import org.bukkit.entity.Player
import xyz.xenondevs.commons.provider.Provider
import xyz.xenondevs.invui.ExperimentalReactiveApi
import xyz.xenondevs.invui.dsl.property.GuiDslProperty
import xyz.xenondevs.invui.dsl.property.MutableProvider2dArrayDslProperty
import xyz.xenondevs.invui.window.CrafterWindow
import xyz.xenondevs.invui.window.setSlots
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Creates a [CrafterWindow] using the DSL.
 *
 * A crafter window displays a crafter block GUI with a 3x3 crafting grid and a result slot (1x1).
 * Individual crafting slots can be enabled or disabled via the [slots][CrafterWindowDsl.slots]
 * property.
 *
 * ```
 * val myWindow = crafterWindow(player) {
 *     title by "Crafter"
 *     craftingGui by gui(
 *         "a b c",
 *         "d e f",
 *         "g h i",
 *     ) {
 *         'a' by ingredient1
 *         // ...
 *     }
 *     resultGui by gui("r") { 'r' by resultItem }
 *     slots[1, 1] = false // disable center slot
 * }
 * ```
 *
 * @see CrafterWindowDsl
 */
@ExperimentalDslApi
inline fun crafterWindow(viewer: Player, crafterWindow: CrafterWindowDsl.() -> Unit): CrafterWindow {
    contract { callsInPlace(crafterWindow, InvocationKind.EXACTLY_ONCE) }
    return CrafterWindowDslImpl(viewer).apply(crafterWindow).build()
}

/**
 * DSL scope for configuring a [CrafterWindow].
 *
 * Extends [SplitWindowDsl] with a 3x3 [craftingGui], a [resultGui], and a [slots] property
 * to control which crafting slots are enabled.
 *
 * ```
 * crafterWindow(player) {
 *     title by "Crafter"
 *     craftingGui by gui(
 *         "a b c",
 *         "d e f",
 *         "g h i",
 *     ) {
 *         'a' by ingredient1
 *         // ...
 *     }
 *     resultGui by gui("r") { 'r' by resultItem }
 *     slots[0, 0] = true
 *     slots[1, 1] = false
 * }
 * ```
 */
@ExperimentalDslApi
sealed interface CrafterWindowDsl : SplitWindowDsl {
    
    /**
     * A [Provider] that resolves to the built [CrafterWindow] instance.
     *
     * Can be used to obtain a reference to the window after the DSL block finishes and
     * the window is built. Accessing it before the window is built throws an
     * [IllegalStateException].
     */
    override val window: Provider<CrafterWindow>
    
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
     * A 3x3 grid of booleans controlling which crafting slots are enabled.
     * All slots default to `false` (disabled). Use the indexing operator to enable or disable
     * individual slots:
     *
     * ```
     * slots[0, 0] = true  // enable top-left slot
     * slots[1, 1] = false // disable center slot
     * ```
     */
    val slots: MutableProvider2dArrayDslProperty<Boolean>
    
}

@PublishedApi
@ExperimentalDslApi
internal class CrafterWindowDslImpl(
    viewer: Player
) : AbstractSplitWindowDsl<CrafterWindow, CrafterWindow.Builder>(viewer), CrafterWindowDsl {
    
    override val craftingGui = GuiDslProperty(3, 3)
    override val resultGui = GuiDslProperty(1, 1)
    override val slots = MutableProvider2dArrayDslProperty(3, 3, false)
    
    override fun createBuilder() = CrafterWindow.builder()
    
    override fun applyToBuilder(builder: CrafterWindow.Builder) {
        super.applyToBuilder(builder)
        builder.apply {
            setCraftingGui(craftingGui.value)
            setResultGui(resultGui.value)
            setSlots(slots.value)
        }
    }
    
}