@file:OptIn(ExperimentalReactiveApi::class)

package xyz.xenondevs.invui.dsl

import xyz.xenondevs.commons.provider.Provider
import xyz.xenondevs.commons.provider.flatten
import xyz.xenondevs.commons.provider.mutableProvider
import xyz.xenondevs.commons.provider.provider
import xyz.xenondevs.invui.ExperimentalReactiveApi
import xyz.xenondevs.invui.dsl.property.MutableProviderDslProperty
import xyz.xenondevs.invui.dsl.property.ProviderDslProperty
import xyz.xenondevs.invui.gui.Gui
import xyz.xenondevs.invui.gui.IngredientPreset
import xyz.xenondevs.invui.gui.TabGui
import xyz.xenondevs.invui.gui.activeTabProvider
import xyz.xenondevs.invui.gui.setTab
import xyz.xenondevs.invui.gui.setTabs
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Creates a [TabGui] using the DSL.
 *
 * Each string in [structure] represents a row in the GUI, where each character is a slot.
 * Characters are mapped to ingredients (items, inventories, markers, etc.) using the
 * [Char.by][IngredientsDsl.by] infix function inside the [gui] block. Spaces are ignored and
 * can be used for readability.
 *
 * The type parameter [G] constrains the type of [Gui] used for individual tabs.
 *
 * ```
 * val myGui = tabGui<Gui>(
 *     "x x x x x",
 *     "x x x x x",
 *     "x x x x x",
 * ) {
 *     'x' by Markers.CONTENT_LIST_SLOT_HORIZONTAL
 *     tabs by listOf(homeTab, settingsTab, null) // null tabs are disabled
 *     tab by 0
 * }
 * ```
 *
 * @see TabGuiDsl
 */
@ExperimentalDslApi
inline fun <G : Gui> tabGui(vararg structure: String, gui: TabGuiDsl<G>.() -> Unit): TabGui {
    contract { callsInPlace(gui, InvocationKind.EXACTLY_ONCE) }
    return TabGuiDslImpl<G>(structure).apply(gui).build()
}

/**
 * Creates a [TabGui] using the DSL, inheriting ingredients from the enclosing
 * [IngredientsDsl] scope. Inherited ingredients can be overridden by redefining the same character
 * in the inner [gui] block.
 *
 * The type parameter [G] constrains the type of [Gui] used for individual tabs.
 *
 * ```
 * ingredients {
 *     '#' by borderItem
 *
 *     val myGui = tabGui<Gui>(
 *         "# # # # # # # # #",
 *         "# x x x x x x x #",
 *         "# x x x x x x x #",
 *         "# # # # # # # # #",
 *     ) {
 *         // '#' is inherited
 *         'x' by Markers.CONTENT_LIST_SLOT_HORIZONTAL
 *         tabs by listOf(homeTab, settingsTab)
 *         tab by 0
 *     }
 * }
 * ```
 *
 * @see TabGuiDsl
 */
@ExperimentalDslApi
inline fun <G : Gui> IngredientsDsl.tabGui(vararg structure: String, gui: TabGuiDsl<G>.() -> Unit): TabGui {
    contract { callsInPlace(gui, InvocationKind.EXACTLY_ONCE) }
    return TabGuiDslImpl<G>(structure, (this as IngredientsDslImpl).buildPresets()).apply(gui).build()
}

/**
 * DSL scope for configuring a [TabGui].
 *
 * Extends [GuiDsl] with tab-specific properties: [tabs] to define the available tabs,
 * [tab] to control which tab is selected, and [activeTab] to observe the currently displayed tab.
 *
 * ```
 * val myGui = tabGui<Gui>(
 *     "x x x x x",
 *     "x x x x x",
 *     "x x x x x",
 * ) {
 *     'x' by Markers.CONTENT_LIST_SLOT_HORIZONTAL
 *     tabs by listOf(homeTab, settingsTab, null)
 *     tab by 0
 * }
 * ```
 *
 * @param G The [Gui] subtype used for individual tabs.
 */
@ExperimentalDslApi
sealed interface TabGuiDsl<G : Gui> : GuiDsl {
    
    /**
     * A [Provider] that resolves to the built [TabGui] instance.
     *
     * Can be used to obtain a reference to the GUI after the DSL block finishes and
     * the GUI is built. Accessing it before the GUI is built throws an [IllegalStateException].
     */
    override val gui: Provider<TabGui>
    
    /**
     * The list of tabs. Individual entries can be `null` to represent disabled/unavailable tabs.
     *
     * Defaults to an empty list. Can be set to a static value or bound to a [Provider]:
     * ```
     * tabs by listOf(homeTab, settingsTab, null)
     * ```
     */
    val tabs: ProviderDslProperty<List<G?>>
    
    /**
     * The index of the currently selected tab (zero-based).
     *
     * Defaults to `-1` (no tab selected). Can be set to a static value or bound to a
     * [MutableProvider][xyz.xenondevs.commons.provider.MutableProvider]:
     * ```
     * tab by 0
     * ```
     */
    val tab: MutableProviderDslProperty<Int>
    
    /**
     * The currently active tab [Gui], or `null` if no tab is selected or the selected tab is
     * disabled. This is a read-only [Provider] that updates automatically as [tab] changes.
     */
    val activeTab: Provider<G?>
    
}

@PublishedApi
@ExperimentalDslApi
internal class TabGuiDslImpl<G : Gui>(
    structure: Array<out String>,
    presets: List<IngredientPreset> = emptyList()
) : GuiDslImpl<TabGui, TabGui.Builder>(structure, presets), TabGuiDsl<G> {
    
    private val internalActiveTab = mutableProvider { provider<G?>(null) }
    
    private var _tabs = provider(emptyList<G?>())
    private var _tab = mutableProvider(-1)
    
    override val tabs: ProviderDslProperty<List<G?>>
        get() = ProviderDslProperty(::_tabs)
    override val tab: MutableProviderDslProperty<Int>
        get() = MutableProviderDslProperty(::_tab)
    override val activeTab = internalActiveTab.flatten()
    
    override fun createBuilder() = TabGui.builder()
    
    override fun applyToBuilder(builder: TabGui.Builder) {
        super.applyToBuilder(builder)
        builder.setTabs(_tabs)
        builder.setTab(_tab)
        builder.addModifier { gui ->
            @Suppress("UNCHECKED_CAST")
            internalActiveTab.set(gui.activeTabProvider as Provider<G?>)
        }
    }
    
}