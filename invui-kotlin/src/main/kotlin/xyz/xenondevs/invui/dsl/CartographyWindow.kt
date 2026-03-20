@file:OptIn(ExperimentalReactiveApi::class)

package xyz.xenondevs.invui.dsl

import org.bukkit.entity.Player
import xyz.xenondevs.commons.provider.Provider
import xyz.xenondevs.commons.provider.provider
import xyz.xenondevs.invui.ExperimentalReactiveApi
import xyz.xenondevs.invui.dsl.property.GuiDslProperty
import xyz.xenondevs.invui.dsl.property.ProviderDslProperty
import xyz.xenondevs.invui.window.CartographyWindow
import xyz.xenondevs.invui.window.setIcons
import xyz.xenondevs.invui.window.setView

// TODO: map image

/**
 * Creates a [CartographyWindow] using the DSL.
 *
 * A cartography window displays a cartography table GUI with an input area (1x2) and a result
 * slot (1x1), along with a map preview controlled by [icons][CartographyWindowDsl.icons] and
 * [view][CartographyWindowDsl.view].
 *
 * ```
 * val myWindow = cartographyWindow(player) {
 *     title by "Cartography Table"
 *     inputGui by gui("a", "b") {
 *         'a' by mapItem
 *         'b' by paperItem
 *     }
 *     resultGui by gui("r") { 'r' by resultItem }
 *     icons by setOf(myMapIcon)
 * }
 * ```
 *
 * @see CartographyWindowDsl
 */
@ExperimentalDslApi
fun cartographyWindow(viewer: Player, cartographyWindow: CartographyWindowDsl.() -> Unit): CartographyWindow =
    CartographyWindowDslImpl(viewer).apply(cartographyWindow).build()

/**
 * DSL scope for configuring a [CartographyWindow].
 *
 * Extends [SplitWindowDsl] with cartography-specific GUIs ([inputGui], [resultGui]) and
 * map display properties ([icons], [view]).
 *
 * ```
 * cartographyWindow(player) {
 *     title by "Cartography Table"
 *     inputGui by gui("a", "b") {
 *         'a' by mapItem
 *         'b' by paperItem
 *     }
 *     resultGui by gui("r") { 'r' by resultItem }
 *     icons by setOf(myMapIcon)
 * }
 * ```
 */
@ExperimentalDslApi
sealed interface CartographyWindowDsl : SplitWindowDsl {
    
    /**
     * A [Provider] that resolves to the built [CartographyWindow] instance.
     *
     * Can be used to obtain a reference to the window after the DSL block finishes and
     * the window is built. Accessing it before the window is built throws an
     * [IllegalStateException].
     */
    override val window: Provider<CartographyWindow>
    
    /**
     * The input GUI (1x2) for the map and paper/glass pane slots.
     *
     * ```
     * inputGui by gui("a", "b") {
     *     'a' by mapItem
     *     'b' by paperItem
     * }
     * ```
     */
    val inputGui: GuiDslProperty
    
    /**
     * The result GUI (1x1) for the output map.
     *
     * ```
     * resultGui by gui("r") { 'r' by resultItem }
     * ```
     */
    val resultGui: GuiDslProperty
    
    /**
     * The set of map icons to display on the map preview.
     *
     * Defaults to an empty set. Can be set to a static value or bound to a [Provider]:
     * ```
     * icons by setOf(myMapIcon)
     * ```
     */
    val icons: ProviderDslProperty<Set<CartographyWindow.MapIcon>>
    
    /**
     * The map view mode controlling how the map preview is displayed.
     *
     * Defaults to [CartographyWindow.View.NORMAL]. Can be set to a static value or bound to
     * a [Provider]:
     * ```
     * view by CartographyWindow.View.ZOOMED
     * ```
     */
    val view: ProviderDslProperty<CartographyWindow.View>
    
}

@ExperimentalDslApi
internal class CartographyWindowDslImpl(
    viewer: Player
) : AbstractSplitWindowDsl<CartographyWindow, CartographyWindow.Builder>(viewer), CartographyWindowDsl {
    
    private var _icons = provider(emptySet<CartographyWindow.MapIcon>())
    private var _view = provider(CartographyWindow.View.NORMAL)
    
    override val inputGui = GuiDslProperty(1, 2)
    override val resultGui = GuiDslProperty(1, 1)
    override val icons: ProviderDslProperty<Set<CartographyWindow.MapIcon>>
        get() = ProviderDslProperty(::_icons)
    override val view: ProviderDslProperty<CartographyWindow.View>
        get() = ProviderDslProperty(::_view)
    
    override fun createBuilder() = CartographyWindow.builder()
    
    override fun applyToBuilder(builder: CartographyWindow.Builder) {
        super.applyToBuilder(builder)
        builder.apply {
            setInputGui(inputGui.value)
            setResultGui(resultGui.value)
            setIcons(_icons)
            setView(_view)
        }
    }
    
}