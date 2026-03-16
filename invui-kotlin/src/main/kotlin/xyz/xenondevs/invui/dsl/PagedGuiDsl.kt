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
import xyz.xenondevs.invui.gui.PagedGui
import xyz.xenondevs.invui.gui.SlotElement
import xyz.xenondevs.invui.gui.pageCountProvider
import xyz.xenondevs.invui.gui.setContent
import xyz.xenondevs.invui.gui.setPage
import xyz.xenondevs.invui.inventory.Inventory
import xyz.xenondevs.invui.item.Item
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@ExperimentalDslApi
inline fun pagedItemsGui(vararg structure: String, gui: PagedGuiDsl<Item>.() -> Unit): PagedGui<Item> {
    contract { callsInPlace(gui, InvocationKind.EXACTLY_ONCE) }
    return PagedGuiDslImpl.Items(structure).apply(gui).build()
}

@ExperimentalDslApi
inline fun IngredientsDsl.pagedItemsGui(vararg structure: String, gui: PagedGuiDsl<Item>.() -> Unit): PagedGui<Item> {
    contract { callsInPlace(gui, InvocationKind.EXACTLY_ONCE) }
    return PagedGuiDslImpl.Items(structure, (this as IngredientsDslImpl).buildPresets()).apply(gui).build()
}

@ExperimentalDslApi
inline fun pagedSlotElementsGui(vararg structure: String, gui: PagedGuiDsl<SlotElement>.() -> Unit): PagedGui<SlotElement> {
    contract { callsInPlace(gui, InvocationKind.EXACTLY_ONCE) }
    return PagedGuiDslImpl.SlotElements(structure).apply(gui).build()
}

@ExperimentalDslApi
inline fun IngredientsDsl.pagedSlotElementsGui(vararg structure: String, gui: PagedGuiDsl<SlotElement>.() -> Unit): PagedGui<SlotElement> {
    contract { callsInPlace(gui, InvocationKind.EXACTLY_ONCE) }
    return PagedGuiDslImpl.SlotElements(structure, (this as IngredientsDslImpl).buildPresets()).apply(gui).build()
}

@ExperimentalDslApi
inline fun pagedGuisGui(vararg structure: String, gui: PagedGuiDsl<Gui>.() -> Unit): PagedGui<Gui> {
    contract { callsInPlace(gui, InvocationKind.EXACTLY_ONCE) }
    return PagedGuiDslImpl.Guis(structure).apply(gui).build()
}

@ExperimentalDslApi
inline fun IngredientsDsl.pagedGuisGui(vararg structure: String, gui: PagedGuiDsl<Gui>.() -> Unit): PagedGui<Gui> {
    contract { callsInPlace(gui, InvocationKind.EXACTLY_ONCE) }
    return PagedGuiDslImpl.Guis(structure, (this as IngredientsDslImpl).buildPresets()).apply(gui).build()
}

@ExperimentalDslApi
inline fun pagedInventoriesGui(vararg structure: String, gui: PagedGuiDsl<Inventory>.() -> Unit): PagedGui<Inventory> {
    contract { callsInPlace(gui, InvocationKind.EXACTLY_ONCE) }
    return PagedGuiDslImpl.Inventories(structure).apply(gui).build()
}

@ExperimentalDslApi
inline fun IngredientsDsl.pagedInventoriesGui(vararg structure: String, gui: PagedGuiDsl<Inventory>.() -> Unit): PagedGui<Inventory> {
    contract { callsInPlace(gui, InvocationKind.EXACTLY_ONCE) }
    return PagedGuiDslImpl.Inventories(structure, (this as IngredientsDslImpl).buildPresets()).apply(gui).build()
}

@ExperimentalDslApi
sealed interface PagedGuiDsl<C : Any> : GuiDsl {
    
    override val gui: Provider<PagedGui<C>>
    
    val content: ProviderDslProperty<List<C>>
    val page: MutableProviderDslProperty<Int>
    val pageCount: Provider<Int>
    
}

@PublishedApi
@ExperimentalDslApi
internal abstract class PagedGuiDslImpl<C : Any>(
    structure: Array<out String>,
    presets: List<IngredientPreset>
) : GuiDslImpl<PagedGui<C>, PagedGui.Builder<C>>(structure, presets), PagedGuiDsl<C> {
    
    private val internalPageCount = mutableProvider { provider(0) }
    
    private var _content = provider(emptyList<C>())
    private var _page = mutableProvider(0)
    
    override val content: ProviderDslProperty<List<C>>
        get() = ProviderDslProperty(::_content)
    override val page: MutableProviderDslProperty<Int>
        get() = MutableProviderDslProperty(::_page)
    override val pageCount = internalPageCount.flatten()
    
    override fun applyToBuilder(builder: PagedGui.Builder<C>) {
        super.applyToBuilder(builder)
        builder.apply {
            setContent(_content)
            setPage(_page)
            addModifier { internalPageCount.set(it.pageCountProvider) }
        }
    }
    
    @PublishedApi
    internal class Items(
        structure: Array<out String>,
        presets: List<IngredientPreset> = emptyList()
    ) : PagedGuiDslImpl<Item>(structure, presets) {
        override fun createBuilder() = PagedGui.itemsBuilder()
    }

    @PublishedApi
    internal class SlotElements(
        structure: Array<out String>,
        presets: List<IngredientPreset> = emptyList()
    ) : PagedGuiDslImpl<SlotElement>(structure, presets) {
        override fun createBuilder() = PagedGui.slotElementsBuilder()
    }
    
    @PublishedApi
    internal class Guis(
        structure: Array<out String>,
        presets: List<IngredientPreset> = emptyList()
    ) : PagedGuiDslImpl<Gui>(structure, presets) {
        override fun createBuilder() = PagedGui.guisBuilder()
    }
    
    @PublishedApi
    internal class Inventories(
        structure: Array<out String>,
        presets: List<IngredientPreset> = emptyList()
    ) : PagedGuiDslImpl<Inventory>(structure, presets) {
        override fun createBuilder() = PagedGui.inventoriesBuilder()
    }
    
}