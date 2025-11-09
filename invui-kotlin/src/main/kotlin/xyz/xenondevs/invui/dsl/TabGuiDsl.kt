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

@ExperimentalDslApi
fun <G : Gui> tabGui(vararg structure: String, gui: TabGuiDsl<G>.() -> Unit): TabGui =
    TabGuiDslImpl<G>(structure).apply(gui).build()

@ExperimentalDslApi
sealed interface TabGuiDsl<G : Gui> : GuiDsl {
    
    val tabs: ProviderDslProperty<List<G?>>
    val tab: MutableProviderDslProperty<Int>
    val activeTab: Provider<G?>
    
}

@ExperimentalDslApi
internal class TabGuiDslImpl<G: Gui>(
    structure: Array<out String>,
    presets: List<IngredientPreset> = emptyList()
) : GuiDslImpl<TabGui, TabGui.Builder>(structure, presets), TabGuiDsl<G> {
    
    private val internalActiveTab = mutableProvider { provider<G?>(null) }
    
    override val tabs = ProviderDslProperty(emptyList<G?>())
    override val tab = MutableProviderDslProperty(-1)
    override val activeTab = internalActiveTab.flatten()
    
    override fun createBuilder() = TabGui.builder()
    
    override fun applyToBuilder(builder: TabGui.Builder) {
        super.applyToBuilder(builder)
        builder.setTabs(tabs)
        builder.setTab(tab)
        builder.addModifier { gui ->
            @Suppress("UNCHECKED_CAST")
            internalActiveTab.set(gui.activeTabProvider as Provider<G?>)
        }
    }
    
}