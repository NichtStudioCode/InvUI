@file:OptIn(ExperimentalReactiveApi::class)

package xyz.xenondevs.invui.dsl

import xyz.xenondevs.commons.provider.Provider
import xyz.xenondevs.commons.provider.mutableProvider
import xyz.xenondevs.invui.ExperimentalReactiveApi
import xyz.xenondevs.invui.dsl.property.MutableProviderDslProperty
import xyz.xenondevs.invui.dsl.property.ProviderDslProperty
import xyz.xenondevs.invui.gui.Gui
import xyz.xenondevs.invui.gui.IngredientPreset
import xyz.xenondevs.invui.gui.ScrollGui
import xyz.xenondevs.invui.gui.setContent
import xyz.xenondevs.invui.gui.setLine
import xyz.xenondevs.invui.inventory.Inventory
import xyz.xenondevs.invui.item.Item

@ExperimentalDslApi
sealed interface ScrollGuiDsl<C : Any> : GuiDsl {
    
    val content: ProviderDslProperty<List<C>>
    val line: MutableProviderDslProperty<Int>
    val lineCount: Provider<Int>
    
}

@ExperimentalDslApi
internal abstract class ScrollGuiDslImpl<C : Any>(
    structure: Array<out String>,
    presets: List<IngredientPreset>
) : GuiDslImpl<ScrollGui<C>, ScrollGui.Builder<C>>(structure, presets), ScrollGuiDsl<C> {
    
    override val content = ProviderDslProperty(emptyList<C>())
    override val line = MutableProviderDslProperty(0)
    override val lineCount = mutableProvider(0)
    
    override fun applyToBuilder(builder: ScrollGui.Builder<C>) {
        super.applyToBuilder(builder)
        builder.apply {
            setContent(content.delegate)
            setLine(line.delegate)
            
            addModifier { gui ->
                lineCount.set(gui.lineCount)
                gui.addLineCountChangeHandler { _, l -> lineCount.set(l) }
            }
        }
    }
    
    internal class Items(
        structure: Array<out String>,
        presets: List<IngredientPreset> = emptyList()
    ) : ScrollGuiDslImpl<Item>(structure, presets) {
        override fun createBuilder() = ScrollGui.itemsBuilder()
    }
    
    internal class Guis(
        structure: Array<out String>,
        presets: List<IngredientPreset> = emptyList()
    ) : ScrollGuiDslImpl<Gui>(structure, presets) {
        override fun createBuilder() = ScrollGui.guisBuilder()
    }
    
    internal class Inventories(
        structure: Array<out String>,
        presets: List<IngredientPreset> = emptyList()
    ) : ScrollGuiDslImpl<Inventory>(structure, presets) {
        override fun createBuilder() = ScrollGui.inventoriesBuilder()
    }
    
}