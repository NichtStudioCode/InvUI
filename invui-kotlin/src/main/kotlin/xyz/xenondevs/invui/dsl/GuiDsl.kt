@file:OptIn(ExperimentalReactiveApi::class)

package xyz.xenondevs.invui.dsl

import org.bukkit.entity.Player
import xyz.xenondevs.commons.provider.Provider
import xyz.xenondevs.commons.provider.provider
import xyz.xenondevs.invui.ExperimentalReactiveApi
import xyz.xenondevs.invui.gui.Gui
import xyz.xenondevs.invui.gui.IngredientPreset
import xyz.xenondevs.invui.gui.setBackground
import xyz.xenondevs.invui.gui.setFrozen
import xyz.xenondevs.invui.gui.setIgnoreObscuredInventorySlots
import xyz.xenondevs.invui.item.ItemProvider
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Creates a [Gui] using the DSL.
 *
 * Each string in [structure] represents a row in the GUI, where each character is a slot.
 * Characters are mapped to ingredients (items, inventories, markers, etc.) using the
 * [Char.by][IngredientsDsl.by] infix function inside the [gui] block. Spaces are ignored and
 * can be used for readability.
 *
 * ```
 * val myGui = gui(
 *     "# # # # # # # # #",
 *     "# . . . . . . . #",
 *     "# . . . x . . . #",
 *     "# . . . . . . . #",
 *     "# # # # # # # # #",
 * ) {
 *     '#' by borderItem
 *     'x' by someItem
 *     background by ItemStack(Material.BLACK_STAINED_GLASS_PANE)
 * }
 * ```
 */
@ExperimentalDslApi
inline fun gui(vararg structure: String, gui: GuiDsl.() -> Unit): Gui {
    contract { callsInPlace(gui, InvocationKind.EXACTLY_ONCE) }
    return NormalGuiDslImpl(structure).apply(gui).build()
}

/**
 * Creates a [Gui] using the DSL, inheriting ingredients from the enclosing [IngredientsDsl] scope.
 * Inherited ingredients can be overridden by redefining the same character in the inner [gui] block.
 *
 * This is particularly useful when creating multiple GUIs that share common ingredients:
 * ```
 * ingredients {
 *     '#' by borderItem
 *     'x' by closeButton
 *
 *     val mainGui = gui(
 *         "# # # # # # # # #",
 *         "# . . . . . . . #",
 *         "# # # # x # # # #",
 *     ) {
 *         // '#' and 'x' are inherited
 *     }
 *
 *     val settingsGui = gui(
 *         "# # # # # # # # #",
 *         "# . . . . . . . #",
 *         "# # # # x # # # #",
 *     ) {
 *         // '#' and 'x' are inherited here too
 *     }
 * }
 * ```
 *
 * It also works when nesting GUIs, since [GuiDsl] extends [IngredientsDsl]:
 * ```
 * gui(
 *     "# # # # # # # # #",
 *     "# . . g g g . . #",
 *     "# . . g g g . . #",
 *     "# . . g g g . . #",
 *     "# # # # # # # # #",
 * ) {
 *     '#' by borderItem
 *     'g' by gui(
 *         "# # #",
 *         "# x #",
 *         "# # #",
 *     ) {
 *         // '#' is inherited from the outer gui
 *         'x' by someItem
 *     }
 * }
 * ```
 */
@ExperimentalDslApi
inline fun IngredientsDsl.gui(vararg structure: String, gui: GuiDsl.() -> Unit): Gui {
    contract { callsInPlace(gui, InvocationKind.EXACTLY_ONCE) }
    return NormalGuiDslImpl(structure, (this as IngredientsDslImpl).buildPresets()).apply(gui).build()
}

/**
 * DSL scope available inside [GuiDsl.onBundleSelect] handlers, providing information about
 * a bundle slot selection event on a GUI slot.
 */
@ExperimentalDslApi
interface GuiBundleSelectDsl : BundleSelectDsl {
    
    /** The GUI slot index where the bundle is located. */
    val guiSlot: Int
    
}

/**
 * DSL scope for configuring a [Gui].
 *
 * Extends [IngredientsDsl], so ingredient mappings (e.g. `'x' by someItem`) can be defined directly
 * inside this scope. Additionally, provides GUI-specific properties like the [background] or [frozen]
 * state.
 */
@ExperimentalDslApi
sealed interface GuiDsl : IngredientsDsl {
    
    /**
     * A [Provider] that resolves to the built [Gui] instance.
     *
     * Can be used to obtain a reference to the GUI after the DSL block finishes and
     * the GUI is built. Accessing it before the GUI is built throws an [IllegalStateException].
     */
    val gui: Provider<Gui>
    
    /**
     * The background [ItemProvider] displayed in empty slots of the GUI.
     *
     * Defaults to `null` (no background). Can be set to a static value or bound to a [Provider]:
     * ```
     * background by ItemStack(Material.GRAY_STAINED_GLASS_PANE)
     * ```
     *
     * @see itemProvider
     */
    val background: ProviderDslProperty<ItemProvider?>
    
    /**
     * Whether the GUI is frozen, preventing all player interactions with slots.
     *
     * Defaults to `false`. Can be set to a static value or bound to a [Provider]:
     * ```
     * frozen by true
     * ```
     */
    val frozen: ProviderDslProperty<Boolean>
    
    /**
     * Whether to ignore inventory slots that are visually obscured (e.g. by another GUI layered on top).
     *
     * Defaults to `true`. Can be set to a static value or bound to a [Provider]:
     * ```
     * ignoreObscuredInventorySlots by false
     * ```
     */
    val ignoreObscuredInventorySlots: ProviderDslProperty<Boolean>

    /**
     * Registers a bundle selection handler that is called when a player selects a slot
     * in a bundle's tooltip on a GUI slot. Multiple handlers can be registered and will all be
     * called in order.
     *
     * ```
     * onBundleSelect {
     *     player.sendMessage("Selected bundle slot $bundleSlot at gui slot $guiSlot")
     * }
     * ```
     *
     * @see GuiBundleSelectDsl
     */
    fun onBundleSelect(handler: GuiBundleSelectDsl.() -> Unit)
    
}

@ExperimentalDslApi
internal class GuiBundleSelectDslImpl(
    override val player: Player,
    override val guiSlot: Int,
    override val bundleSlot: Int
) : GuiBundleSelectDsl

@PublishedApi
@ExperimentalDslApi
internal abstract class GuiDslImpl<G : Gui, B : Gui.Builder<G, B>>(
    private val structure: Array<out String>,
    presets: List<IngredientPreset>
) : IngredientsDslImpl(presets), GuiDsl {
    
    private lateinit var _gui: G
    override val gui: Provider<G> = provider {
        check(::_gui.isInitialized) { "Gui cannot be accessed before it is built" }
        _gui
    }
    
    private var _background = provider<ItemProvider?>(null)
    private var _frozen = provider(false)
    private var _ignoreObscuredInventorySlots = provider(true)
    
    override val background: ProviderDslProperty<ItemProvider?>
        get() = ProviderDslProperty(::_background)
    override val frozen: ProviderDslProperty<Boolean>
        get() = ProviderDslProperty(::_frozen)
    override val ignoreObscuredInventorySlots: ProviderDslProperty<Boolean>
        get() = ProviderDslProperty(::_ignoreObscuredInventorySlots)
    
    private val bundleSelectHandlers = mutableListOf<GuiBundleSelectDsl.() -> Unit>()
    
    override fun onBundleSelect(handler: GuiBundleSelectDsl.() -> Unit) {
        bundleSelectHandlers += handler
    }
    
    fun build(): G {
        val gui = createBuilder().apply(::applyToBuilder).build()
        _gui = gui
        return gui
    }
    
    open fun applyToBuilder(builder: B) {
        builder.apply {
            setStructure(*structure)
            for (preset in presets) {
                applyPreset(preset)
            }
            applyPreset(ingredients.build())
            
            for (handler in bundleSelectHandlers) {
                addBundleSelectHandler { player, guiSlot, bundleSlot ->
                    GuiBundleSelectDslImpl(player, guiSlot, bundleSlot).handler()
                }
            }
            
            setBackground(_background)
            setFrozen(_frozen)
            setIgnoreObscuredInventorySlots(_ignoreObscuredInventorySlots)
        }
    }
    
    abstract fun createBuilder(): B
    
}

@PublishedApi
@ExperimentalDslApi
internal class NormalGuiDslImpl<B : Gui.Builder<Gui, B>>(
    structure: Array<out String>,
    presets: List<IngredientPreset> = emptyList()
) : GuiDslImpl<Gui, B>(structure, presets) {
    
    @Suppress("UNCHECKED_CAST")
    override fun createBuilder(): B = Gui.builder() as B
    
}