@file:OptIn(ExperimentalReactiveApi::class)

package xyz.xenondevs.invui.dsl

import xyz.xenondevs.invui.ExperimentalReactiveApi
import xyz.xenondevs.invui.dsl.property.MutableProviderDslProperty
import xyz.xenondevs.invui.dsl.property.ProviderDslProperty
import xyz.xenondevs.invui.gui.Gui
import xyz.xenondevs.invui.gui.IngredientPreset
import xyz.xenondevs.invui.gui.TabGui
import xyz.xenondevs.invui.gui.setTab
import xyz.xenondevs.invui.gui.setTabs

@ExperimentalDslApi
fun tabGui(vararg structure: String, gui: TabGuiDsl.() -> Unit): TabGui =
    TabGuiDslImpl(structure).apply(gui).build()

@ExperimentalDslApi
sealed interface TabGuiDsl : GuiDsl {
    
    val tabs: ProviderDslProperty<List<Gui?>>
    val tab: MutableProviderDslProperty<Int>
    
}

@ExperimentalDslApi
internal class TabGuiDslImpl(
    structure: Array<out String>,
    presets: List<IngredientPreset> = emptyList()
) : GuiDslImpl<TabGui, TabGui.Builder>(structure, presets), TabGuiDsl {
    
    override val tabs = ProviderDslProperty(emptyList<Gui?>())
    override val tab = MutableProviderDslProperty(-1)
    
    override fun createBuilder() = TabGui.builder()
    
    override fun applyToBuilder(builder: TabGui.Builder) {
        super.applyToBuilder(builder)
        builder.setTabs(tabs)
        builder.setTab(tab)
    }
    
}