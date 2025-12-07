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
import xyz.xenondevs.invui.gui.ScrollGui
import xyz.xenondevs.invui.gui.lineCountProvider
import xyz.xenondevs.invui.gui.maxLineProvider
import xyz.xenondevs.invui.gui.setContent
import xyz.xenondevs.invui.gui.setLine
import xyz.xenondevs.invui.inventory.Inventory
import xyz.xenondevs.invui.item.Item

@ExperimentalDslApi
fun scrollItemsGui(vararg structure: String, gui: ScrollGuiDsl<Item>.() -> Unit): ScrollGui<Item> =
    ScrollGuiDslImpl.Items(structure).apply(gui).build()

@ExperimentalDslApi
fun scrollGuisGui(vararg structure: String, gui: ScrollGuiDsl<Gui>.() -> Unit): ScrollGui<Gui> =
    ScrollGuiDslImpl.Guis(structure).apply(gui).build()

@ExperimentalDslApi
fun scrollInventoriesGui(vararg structure: String, gui: ScrollGuiDsl<Inventory>.() -> Unit): ScrollGui<Inventory> =
    ScrollGuiDslImpl.Inventories(structure).apply(gui).build()

@ExperimentalDslApi
sealed interface ScrollGuiDsl<C : Any> : GuiDsl {
    
    val content: ProviderDslProperty<List<C>>
    val line: MutableProviderDslProperty<Int>
    val lineCount: Provider<Int>
    val maxLine: Provider<Int>
    
}

@ExperimentalDslApi
internal abstract class ScrollGuiDslImpl<C : Any>(
    structure: Array<out String>,
    presets: List<IngredientPreset>
) : GuiDslImpl<ScrollGui<C>, ScrollGui.Builder<C>>(structure, presets), ScrollGuiDsl<C> {
    
    private val internalLineCount = mutableProvider { provider(0) }
    private val internalMaxLine = mutableProvider { provider(0) }
    
    override val content = ProviderDslProperty(emptyList<C>())
    override val line = MutableProviderDslProperty(0)
    override val lineCount = internalLineCount.flatten()
    override val maxLine = internalMaxLine.flatten()
    
    override fun applyToBuilder(builder: ScrollGui.Builder<C>) {
        super.applyToBuilder(builder)
        builder.apply {
            setContent(content)
            setLine(line)
            
            addModifier { gui ->
                internalLineCount.set(gui.lineCountProvider)
                internalMaxLine.set(gui.maxLineProvider)
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