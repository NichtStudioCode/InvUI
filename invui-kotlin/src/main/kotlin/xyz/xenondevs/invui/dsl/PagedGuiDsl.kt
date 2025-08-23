@file:OptIn(ExperimentalReactiveApi::class)

package xyz.xenondevs.invui.dsl

import xyz.xenondevs.commons.provider.Provider
import xyz.xenondevs.commons.provider.mutableProvider
import xyz.xenondevs.invui.ExperimentalReactiveApi
import xyz.xenondevs.invui.dsl.property.MutableProviderDslProperty
import xyz.xenondevs.invui.dsl.property.ProviderDslProperty
import xyz.xenondevs.invui.gui.Gui
import xyz.xenondevs.invui.gui.IngredientPreset
import xyz.xenondevs.invui.gui.PagedGui
import xyz.xenondevs.invui.gui.setContent
import xyz.xenondevs.invui.gui.setPage
import xyz.xenondevs.invui.inventory.Inventory
import xyz.xenondevs.invui.item.Item

@ExperimentalDslApi
fun pagedItemsGui(vararg structure: String, gui: PagedGuiDsl<Item>.() -> Unit): PagedGui<Item> =
    PagedGuiDslImpl.Items(structure).apply(gui).build()

@ExperimentalDslApi
fun pagedGuisGui(vararg structure: String, gui: PagedGuiDsl<Gui>.() -> Unit): PagedGui<Gui> =
    PagedGuiDslImpl.Guis(structure).apply(gui).build()

@ExperimentalDslApi
fun pagedInventoriesGui(vararg structure: String, gui: PagedGuiDsl<Inventory>.() -> Unit): PagedGui<Inventory> =
    PagedGuiDslImpl.Inventories(structure).apply(gui).build()

@ExperimentalDslApi
sealed interface PagedGuiDsl<C : Any> : GuiDsl {
    
    val content: ProviderDslProperty<List<C>>
    val page: MutableProviderDslProperty<Int>
    val pageCount: Provider<Int>
    
}

@ExperimentalDslApi
internal abstract class PagedGuiDslImpl<C : Any>(
    structure: Array<out String>,
    presets: List<IngredientPreset>
) : GuiDslImpl<PagedGui<C>, PagedGui.Builder<C>>(structure, presets), PagedGuiDsl<C> {
    
    override val content = ProviderDslProperty(emptyList<C>())
    override val page = MutableProviderDslProperty(0)
    override val pageCount = mutableProvider(0)
    
    override fun applyToBuilder(builder: PagedGui.Builder<C>) {
        super.applyToBuilder(builder)
        builder.apply { 
            setContent(content.delegate)
            setPage(page.delegate)
            
            addModifier { gui ->
                pageCount.set(gui.pageCount)
                gui.addPageCountChangeHandler { _, p -> pageCount.set(p) }
            }
        }
    }
    
    internal class Items(
        structure: Array<out String>,
        presets: List<IngredientPreset> = emptyList()
    ) : PagedGuiDslImpl<Item>(structure, presets) {
        override fun createBuilder() = PagedGui.itemsBuilder()
    }
    
    internal class Guis(
        structure: Array<out String>,
        presets: List<IngredientPreset> = emptyList()
    ) : PagedGuiDslImpl<Gui>(structure, presets) {
        override fun createBuilder() = PagedGui.guisBuilder()
    }
    
    internal class Inventories(
        structure: Array<out String>,
        presets: List<IngredientPreset> = emptyList()
    ) : PagedGuiDslImpl<Inventory>(structure, presets) {
        override fun createBuilder() = PagedGui.inventoriesBuilder()
    }
    
}