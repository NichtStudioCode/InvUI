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

@ExperimentalDslApi
inline fun <G : Gui> tabGui(vararg structure: String, gui: TabGuiDsl<G>.() -> Unit): TabGui {
    contract { callsInPlace(gui, InvocationKind.EXACTLY_ONCE) }
    return TabGuiDslImpl<G>(structure).apply(gui).build()
}

@ExperimentalDslApi
inline fun <G : Gui> IngredientsDsl.tabGui(vararg structure: String, gui: TabGuiDsl<G>.() -> Unit): TabGui {
    contract { callsInPlace(gui, InvocationKind.EXACTLY_ONCE) }
    return TabGuiDslImpl<G>(structure, (this as IngredientsDslImpl).buildPresets()).apply(gui).build()
}

@ExperimentalDslApi
sealed interface TabGuiDsl<G : Gui> : GuiDsl {
    
    val tabs: ProviderDslProperty<List<G?>>
    val tab: MutableProviderDslProperty<Int>
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