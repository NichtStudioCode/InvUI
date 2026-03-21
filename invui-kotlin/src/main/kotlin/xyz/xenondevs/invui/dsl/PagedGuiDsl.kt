@file:OptIn(ExperimentalReactiveApi::class)

package xyz.xenondevs.invui.dsl

import xyz.xenondevs.commons.provider.Provider
import xyz.xenondevs.commons.provider.flatten
import xyz.xenondevs.commons.provider.mutableProvider
import xyz.xenondevs.commons.provider.provider
import xyz.xenondevs.invui.ExperimentalReactiveApi
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

/**
 * Creates a [PagedGui] of [Item]s using the DSL.
 *
 * Each string in [structure] represents a row in the GUI, where each character is a slot.
 * Characters are mapped to ingredients (items, inventories, markers, etc.) using the
 * [Char.by][IngredientsDsl.by] infix function inside the [gui] block. Spaces are ignored and
 * can be used for readability.
 *
 * ```
 * val myGui = pagedItemsGui(
 *     "x x x x x",
 *     "x x x x x",
 *     "x x x x x",
 * ) {
 *     'x' by Markers.CONTENT_LIST_SLOT_HORIZONTAL
 *     content by listOf(itemA, itemB, itemC, /* ... */)
 * }
 * ```
 *
 * @see PagedGuiDsl
 */
@ExperimentalDslApi
inline fun pagedItemsGui(vararg structure: String, gui: PagedGuiDsl<Item>.() -> Unit): PagedGui<Item> {
    contract { callsInPlace(gui, InvocationKind.EXACTLY_ONCE) }
    return PagedGuiDslImpl.Items(structure).apply(gui).build()
}

/**
 * Creates a [PagedGui] of [Item]s using the DSL, inheriting ingredients from the
 * enclosing [IngredientsDsl] scope. Inherited ingredients can be overridden by redefining the same
 * character in the inner [gui] block.
 *
 * ```
 * ingredients {
 *     '#' by borderItem
 *
 *     val myGui = pagedItemsGui(
 *         "# # # # # # # # #",
 *         "# x x x x x x x #",
 *         "# x x x x x x x #",
 *         "# # # # # # # # #",
 *     ) {
 *         // '#' is inherited
 *         'x' by Markers.CONTENT_LIST_SLOT_HORIZONTAL
 *         content by items
 *     }
 * }
 * ```
 *
 * @see PagedGuiDsl
 */
@ExperimentalDslApi
inline fun IngredientsDsl.pagedItemsGui(vararg structure: String, gui: PagedGuiDsl<Item>.() -> Unit): PagedGui<Item> {
    contract { callsInPlace(gui, InvocationKind.EXACTLY_ONCE) }
    return PagedGuiDslImpl.Items(structure, (this as IngredientsDslImpl).buildPresets()).apply(gui).build()
}

/**
 * Creates a [PagedGui] of [SlotElement]s using the DSL.
 *
 * Each string in [structure] represents a row in the GUI, where each character is a slot.
 * Characters are mapped to ingredients (items, inventories, markers, etc.) using the
 * [Char.by][IngredientsDsl.by] infix function inside the [gui] block. Spaces are ignored and
 * can be used for readability.
 *
 * ```
 * val myGui = pagedSlotElementsGui(
 *     "x x x x x",
 *     "x x x x x",
 *     "x x x x x",
 * ) {
 *     'x' by Markers.CONTENT_LIST_SLOT_HORIZONTAL
 *     content by slotElements
 * }
 * ```
 *
 * @see PagedGuiDsl
 */
@ExperimentalDslApi
inline fun pagedSlotElementsGui(vararg structure: String, gui: PagedGuiDsl<SlotElement>.() -> Unit): PagedGui<SlotElement> {
    contract { callsInPlace(gui, InvocationKind.EXACTLY_ONCE) }
    return PagedGuiDslImpl.SlotElements(structure).apply(gui).build()
}

/**
 * Creates a [PagedGui] of [SlotElement]s using the DSL, inheriting ingredients from the
 * enclosing [IngredientsDsl] scope. Inherited ingredients can be overridden by redefining the same
 * character in the inner [gui] block.
 *
 * ```
 * ingredients {
 *     '#' by borderItem
 *
 *     val myGui = pagedSlotElementsGui(
 *         "# # # # # # # # #",
 *         "# x x x x x x x #",
 *         "# x x x x x x x #",
 *         "# # # # # # # # #",
 *     ) {
 *         // '#' is inherited
 *         'x' by Markers.CONTENT_LIST_SLOT_HORIZONTAL
 *         content by slotElements
 *     }
 * }
 * ```
 *
 * @see PagedGuiDsl
 */
@ExperimentalDslApi
inline fun IngredientsDsl.pagedSlotElementsGui(vararg structure: String, gui: PagedGuiDsl<SlotElement>.() -> Unit): PagedGui<SlotElement> {
    contract { callsInPlace(gui, InvocationKind.EXACTLY_ONCE) }
    return PagedGuiDslImpl.SlotElements(structure, (this as IngredientsDslImpl).buildPresets()).apply(gui).build()
}

/**
 * Creates a [PagedGui] of [Gui]s using the DSL.
 *
 * Each string in [structure] represents a row in the GUI, where each character is a slot.
 * Characters are mapped to ingredients (items, inventories, markers, etc.) using the
 * [Char.by][IngredientsDsl.by] infix function inside the [gui] block. Spaces are ignored and
 * can be used for readability.
 *
 * ```
 * val myGui = pagedGuisGui(
 *     "x x x x x",
 *     "x x x x x",
 *     "x x x x x",
 * ) {
 *     'x' by Markers.CONTENT_LIST_SLOT_HORIZONTAL
 *     content by guis
 * }
 * ```
 *
 * @see PagedGuiDsl
 */
@ExperimentalDslApi
inline fun pagedGuisGui(vararg structure: String, gui: PagedGuiDsl<Gui>.() -> Unit): PagedGui<Gui> {
    contract { callsInPlace(gui, InvocationKind.EXACTLY_ONCE) }
    return PagedGuiDslImpl.Guis(structure).apply(gui).build()
}

/**
 * Creates a [PagedGui] of [Gui]s using the DSL, inheriting ingredients from the
 * enclosing [IngredientsDsl] scope. Inherited ingredients can be overridden by redefining the same
 * character in the inner [gui] block.
 *
 * ```
 * ingredients {
 *     '#' by borderItem
 *
 *     val myGui = pagedGuisGui(
 *         "# # # # # # # # #",
 *         "# x x x x x x x #",
 *         "# x x x x x x x #",
 *         "# # # # # # # # #",
 *     ) {
 *         // '#' is inherited
 *         'x' by Markers.CONTENT_LIST_SLOT_HORIZONTAL
 *         content by guis
 *     }
 * }
 * ```
 *
 * @see PagedGuiDsl
 */
@ExperimentalDslApi
inline fun IngredientsDsl.pagedGuisGui(vararg structure: String, gui: PagedGuiDsl<Gui>.() -> Unit): PagedGui<Gui> {
    contract { callsInPlace(gui, InvocationKind.EXACTLY_ONCE) }
    return PagedGuiDslImpl.Guis(structure, (this as IngredientsDslImpl).buildPresets()).apply(gui).build()
}

/**
 * Creates a [PagedGui] of [Inventory]s using the DSL.
 *
 * Each string in [structure] represents a row in the GUI, where each character is a slot.
 * Characters are mapped to ingredients (items, inventories, markers, etc.) using the
 * [Char.by][IngredientsDsl.by] infix function inside the [gui] block. Spaces are ignored and
 * can be used for readability.
 *
 * ```
 * val myGui = pagedInventoriesGui(
 *     "x x x x x",
 *     "x x x x x",
 *     "x x x x x",
 * ) {
 *     'x' by Markers.CONTENT_LIST_SLOT_HORIZONTAL
 *     content by inventories
 * }
 * ```
 *
 * @see PagedGuiDsl
 */
@ExperimentalDslApi
inline fun pagedInventoriesGui(vararg structure: String, gui: PagedGuiDsl<Inventory>.() -> Unit): PagedGui<Inventory> {
    contract { callsInPlace(gui, InvocationKind.EXACTLY_ONCE) }
    return PagedGuiDslImpl.Inventories(structure).apply(gui).build()
}

/**
 * Creates a [PagedGui] of [Inventory]s using the DSL, inheriting ingredients from the
 * enclosing [IngredientsDsl] scope. Inherited ingredients can be overridden by redefining the same
 * character in the inner [gui] block.
 *
 * ```
 * ingredients {
 *     '#' by borderItem
 *
 *     val myGui = pagedInventoriesGui(
 *         "# # # # # # # # #",
 *         "# x x x x x x x #",
 *         "# x x x x x x x #",
 *         "# # # # # # # # #",
 *     ) {
 *         // '#' is inherited
 *         'x' by Markers.CONTENT_LIST_SLOT_HORIZONTAL
 *         content by inventories
 *     }
 * }
 * ```
 *
 * @see PagedGuiDsl
 */
@ExperimentalDslApi
inline fun IngredientsDsl.pagedInventoriesGui(vararg structure: String, gui: PagedGuiDsl<Inventory>.() -> Unit): PagedGui<Inventory> {
    contract { callsInPlace(gui, InvocationKind.EXACTLY_ONCE) }
    return PagedGuiDslImpl.Inventories(structure, (this as IngredientsDslImpl).buildPresets()).apply(gui).build()
}

/**
 * DSL scope for configuring a [PagedGui].
 *
 * Extends [GuiDsl] with pagination-specific properties: [content] to define what is paged through,
 * [page] to control the current page, and [pageCount] to observe the total number of pages.
 *
 * ```
 * val myGui = pagedItemsGui(
 *     "x x x x x",
 *     "x x x x x",
 *     "x x x x x",
 * ) {
 *     'x' by Markers.CONTENT_LIST_SLOT_HORIZONTAL
 *     content by items
 *     page by 0
 * }
 * ```
 *
 * @param C The content type being paged (e.g. [Item], [SlotElement], [Gui], [Inventory]).
 */
@ExperimentalDslApi
sealed interface PagedGuiDsl<C : Any> : GuiDsl {
    
    /**
     * A [Provider] that resolves to the built [PagedGui] instance.
     *
     * Can be used to obtain a reference to the GUI after the DSL block finishes and
     * the GUI is built. Accessing it before the GUI is built throws an [IllegalStateException].
     */
    override val gui: Provider<PagedGui<C>>
    
    /**
     * The list of content elements to page through.
     *
     * Defaults to an empty list. Can be set to a static value or bound to a [Provider]:
     * ```
     * content by listOf(itemA, itemB, itemC)
     * ```
     */
    val content: ProviderDslProperty<List<C>>
    
    /**
     * The current page index (zero-based).
     *
     * Defaults to `0`. Can be set to a static value or bound to a [MutableProvider][xyz.xenondevs.commons.provider.MutableProvider]:
     * ```
     * page by 0
     * ```
     */
    val page: MutableProviderDslProperty<Int>
    
    /**
     * The total number of pages. This is a read-only [Provider] that updates automatically
     * as [content] changes.
     */
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