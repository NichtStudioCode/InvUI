@file:OptIn(ExperimentalReactiveApi::class)

package xyz.xenondevs.invui.dsl

import org.bukkit.entity.Player
import xyz.xenondevs.commons.provider.Provider
import xyz.xenondevs.commons.provider.mutableProvider
import xyz.xenondevs.commons.provider.provider
import xyz.xenondevs.invui.ExperimentalReactiveApi
import xyz.xenondevs.invui.window.AnvilWindow
import xyz.xenondevs.invui.window.addRenameHandler
import xyz.xenondevs.invui.window.setResultAlwaysValid
import xyz.xenondevs.invui.window.setTextFieldAlwaysEnabled
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Creates an [AnvilWindow] using the DSL.
 *
 * An anvil window displays an anvil GUI with a text input field. The [upperGui][AnvilWindowDsl.upperGui]
 * is a 3x1 GUI representing the three anvil slots (left input, right input, result).
 *
 * ```
 * val myWindow = anvilWindow(player) {
 *     title by "Rename Item"
 *
 *     upperGui by gui("l r o") {
 *         'l' by leftInputItem
 *         'r' by rightInputItem
 *         'o' by resultItem
 *     }
 * }
 * ```
 *
 * @see AnvilWindowDsl
 */
@ExperimentalDslApi
inline fun anvilWindow(viewer: Player, anvilWindow: AnvilWindowDsl.() -> Unit): AnvilWindow {
    contract { callsInPlace(anvilWindow, InvocationKind.EXACTLY_ONCE) }
    return AnvilWindowDslImpl(viewer).apply(anvilWindow).build()
}

/**
 * DSL scope for configuring an [AnvilWindow].
 *
 * Extends [SplitWindowDsl] with anvil-specific properties such as [text] to observe the text field
 * contents.
 *
 * ```
 * anvilWindow(player) {
 *     title by "Enter Name"
 *
 *     upperGui by gui("l r o") {
 *         'l' by inputItem
 *         'r' by materialItem
 *         'o' by resultItem
 *     }
 * }
 * ```
 */
@ExperimentalDslApi
sealed interface AnvilWindowDsl : SplitWindowDsl {
    
    /**
     * A [Provider] that resolves to the built [AnvilWindow] instance.
     *
     * Can be used to obtain a reference to the window after the DSL block finishes and
     * the window is built. Accessing it before the window is built throws an
     * [IllegalStateException].
     */
    override val window: Provider<AnvilWindow>
    
    /**
     * The upper GUI (3x1) representing the three anvil slots: left input, right input,
     * and result.
     *
     * ```
     * upperGui by gui("l r o") {
     *     'l' by leftInputItem
     *     'r' by rightInputItem
     *     'o' by resultItem
     * }
     * ```
     */
    val upperGui: GuiDslProperty
    
    /**
     * A read-only [Provider] that tracks the current text in the anvil's text field,
     * updated as the player types.
     */
    val text: Provider<String>
    
    /**
     * Whether the text field is always enabled, even without an item in the left input slot.
     *
     * Defaults to `true`. Can be set to a static value or bound to a [Provider]:
     * ```
     * textFieldAlwaysEnabled by true
     * ```
     */
    val textFieldAlwaysEnabled: ProviderDslProperty<Boolean>
    
    /**
     * Whether the result slot always allows item pickup, regardless of vanilla anvil rules.
     *
     * Defaults to `false`. Can be set to a static value or bound to a [Provider]:
     * ```
     * resultAlwaysValid by true
     * ```
     */
    val resultAlwaysValid: ProviderDslProperty<Boolean>
    
}

@PublishedApi
@ExperimentalDslApi
internal class AnvilWindowDslImpl(
    viewer: Player
) : AbstractSplitWindowDsl<AnvilWindow, AnvilWindow.Builder>(viewer), AnvilWindowDsl {
    
    private var _textFieldAlwaysEnabled = provider(true)
    private var _resultAlwaysValid = provider(false)
    
    override val upperGui = GuiDslProperty(3, 1)
    override val text = mutableProvider("")
    override val textFieldAlwaysEnabled: ProviderDslProperty<Boolean>
        get() = ProviderDslProperty(::_textFieldAlwaysEnabled)
    override val resultAlwaysValid: ProviderDslProperty<Boolean>
        get() = ProviderDslProperty(::_resultAlwaysValid)
    
    override fun createBuilder() = AnvilWindow.builder()
    
    override fun applyToBuilder(builder: AnvilWindow.Builder) {
        super.applyToBuilder(builder)
        builder.apply {
            setUpperGui(upperGui.value)
            addRenameHandler(text)
            setTextFieldAlwaysEnabled(_textFieldAlwaysEnabled)
            setResultAlwaysValid(_resultAlwaysValid)
        }
    }
    
}