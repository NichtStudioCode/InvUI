@file:OptIn(ExperimentalReactiveApi::class)

package xyz.xenondevs.invui.dsl

import org.bukkit.entity.Player
import xyz.xenondevs.commons.provider.Provider
import xyz.xenondevs.commons.provider.mutableProvider
import xyz.xenondevs.invui.ExperimentalReactiveApi
import xyz.xenondevs.invui.dsl.property.GuiDslProperty
import xyz.xenondevs.invui.dsl.property.MutableProviderDslProperty
import xyz.xenondevs.invui.window.StonecutterWindow
import xyz.xenondevs.invui.window.setSelectedSlot
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Creates a [StonecutterWindow] using the DSL.
 *
 * A stonecutter window displays a stonecutter GUI with a 2x1 upper GUI (input and result slots)
 * and a [buttonsGui][StonecutterWindowDsl.buttonsGui] for the recipe selection buttons.
 *
 * ```
 * val myWindow = stonecutterWindow(player) {
 *     title by "Stonecutter"
 *     upperGui by gui("i r") {
 *         'i' by inputItem
 *         'r' by resultItem
 *     }
 *     buttonsGui by gui(
 *         "a b c d",
 *     ) {
 *         'a' by recipeButton1
 *         'b' by recipeButton2
 *         'c' by recipeButton3
 *         'd' by recipeButton4
 *     }
 *     selectedSlot by 0
 * }
 * ```
 *
 * @see StonecutterWindowDsl
 */
@ExperimentalDslApi
inline fun stonecutterWindow(viewer: Player, stonecutterWindow: StonecutterWindowDsl.() -> Unit): StonecutterWindow {
    contract { callsInPlace(stonecutterWindow, InvocationKind.EXACTLY_ONCE) }
    return StonecutterWindowDslImpl(viewer).apply(stonecutterWindow).build()
}

/**
 * DSL scope for configuring a [StonecutterWindow].
 *
 * Extends [SplitWindowDsl] with a 2x1 [upperGui] (input and result), a [buttonsGui] for recipe
 * selection buttons, and a [selectedSlot] to control which recipe is selected.
 *
 * ```
 * stonecutterWindow(player) {
 *     title by "Stonecutter"
 *     upperGui by gui("i r") {
 *         'i' by inputItem
 *         'r' by resultItem
 *     }
 *     buttonsGui by gui("a b c d") {
 *         'a' by recipeButton1
 *         'b' by recipeButton2
 *         'c' by recipeButton3
 *         'd' by recipeButton4
 *     }
 *     selectedSlot by 0
 * }
 * ```
 */
@ExperimentalDslApi
sealed interface StonecutterWindowDsl : SplitWindowDsl {
    
    /**
     * A [Provider] that resolves to the built [StonecutterWindow] instance.
     *
     * Can be used to obtain a reference to the window after the DSL block finishes and
     * the window is built. Accessing it before the window is built throws an
     * [IllegalStateException].
     */
    override val window: Provider<StonecutterWindow>
    
    /**
     * The upper GUI (2x1) for the input and result slots.
     *
     * ```
     * upperGui by gui("i r") {
     *     'i' by inputItem
     *     'r' by resultItem
     * }
     * ```
     */
    val upperGui: GuiDslProperty
    
    /**
     * The buttons GUI for recipe selection. Has a fixed width of 4 but arbitrary height,
     * which is automatically determined by the number of recipes.
     *
     * ```
     * buttonsGui by gui("a b c d") {
     *     'a' by recipeButton1
     *     'b' by recipeButton2
     *     'c' by recipeButton3
     *     'd' by recipeButton4
     * }
     * ```
     */
    val buttonsGui: GuiDslProperty
    
    /**
     * The index of the currently selected recipe button (zero-based), or `-1` if none is
     * selected.
     *
     * Defaults to `-1` (no slot selected).
     * 
     * ```
     * selectedSlot by 0
     * ```
     */
    val selectedSlot: MutableProviderDslProperty<Int>
    
}

@PublishedApi
@ExperimentalDslApi
internal class StonecutterWindowDslImpl(
    viewer: Player
) : AbstractSplitWindowDsl<StonecutterWindow, StonecutterWindow.Builder>(viewer), StonecutterWindowDsl {
    
    private var _selectedSlot = mutableProvider(-1)
    
    override val upperGui = GuiDslProperty(2, 1)
    override val buttonsGui = GuiDslProperty(4, 0, arbitraryHeight = true)
    override val selectedSlot: MutableProviderDslProperty<Int>
        get() = MutableProviderDslProperty(::_selectedSlot)
    
    override fun createBuilder() = StonecutterWindow.builder()
    
    override fun applyToBuilder(builder: StonecutterWindow.Builder) {
        super.applyToBuilder(builder)
        builder.apply {
            setUpperGui(upperGui.value)
            setButtonsGui(buttonsGui.value)
            setSelectedSlot(_selectedSlot)
        }
    }
    
}