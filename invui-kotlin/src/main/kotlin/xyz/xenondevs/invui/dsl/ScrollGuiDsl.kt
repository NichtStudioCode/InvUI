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
import xyz.xenondevs.invui.gui.SlotElement
import xyz.xenondevs.invui.gui.lineCountProvider
import xyz.xenondevs.invui.gui.maxLineProvider
import xyz.xenondevs.invui.gui.setContent
import xyz.xenondevs.invui.gui.setLine
import xyz.xenondevs.invui.inventory.Inventory
import xyz.xenondevs.invui.item.Item
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Creates a [ScrollGui] of [Item]s using the DSL.
 *
 * Each string in [structure] represents a row in the GUI, where each character is a slot.
 * Characters are mapped to ingredients (items, inventories, markers, etc.) using the
 * [Char.by][IngredientsDsl.by] infix function inside the [gui] block. Spaces are ignored and
 * can be used for readability.
 *
 * ```
 * val myGui = scrollItemsGui(
 *     "x x x x x",
 *     "x x x x x",
 *     "x x x x x",
 * ) {
 *     'x' by Markers.CONTENT_LIST_SLOT_HORIZONTAL
 *     content by listOf(itemA, itemB, itemC, /* ... */)
 * }
 * ```
 *
 * @see ScrollGuiDsl
 */
@ExperimentalDslApi
inline fun scrollItemsGui(vararg structure: String, gui: ScrollGuiDsl<Item>.() -> Unit): ScrollGui<Item> {
    contract { callsInPlace(gui, InvocationKind.EXACTLY_ONCE) }
    return ScrollGuiDslImpl.Items(structure).apply(gui).build()
}

/**
 * Creates a [ScrollGui] of [Item]s using the DSL, inheriting ingredients from the
 * enclosing [IngredientsDsl] scope. Inherited ingredients can be overridden by redefining the same
 * character in the inner [gui] block.
 *
 * ```
 * ingredients {
 *     '#' by borderItem
 *
 *     val myGui = scrollItemsGui(
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
 * @see ScrollGuiDsl
 */
@ExperimentalDslApi
inline fun IngredientsDsl.scrollItemsGui(vararg structure: String, gui: ScrollGuiDsl<Item>.() -> Unit): ScrollGui<Item> {
    contract { callsInPlace(gui, InvocationKind.EXACTLY_ONCE) }
    return ScrollGuiDslImpl.Items(structure, (this as IngredientsDslImpl).buildPresets()).apply(gui).build()
}

/**
 * Creates a [ScrollGui] of [SlotElement]s using the DSL.
 *
 * Each string in [structure] represents a row in the GUI, where each character is a slot.
 * Characters are mapped to ingredients (items, inventories, markers, etc.) using the
 * [Char.by][IngredientsDsl.by] infix function inside the [gui] block. Spaces are ignored and
 * can be used for readability.
 *
 * ```
 * val myGui = scrollSlotElementsGui(
 *     "x x x x x",
 *     "x x x x x",
 *     "x x x x x",
 * ) {
 *     'x' by Markers.CONTENT_LIST_SLOT_HORIZONTAL
 *     content by slotElements
 * }
 * ```
 *
 * @see ScrollGuiDsl
 */
@ExperimentalDslApi
inline fun scrollSlotElementsGui(vararg structure: String, gui: ScrollGuiDsl<SlotElement>.() -> Unit): ScrollGui<SlotElement> {
    contract { callsInPlace(gui, InvocationKind.EXACTLY_ONCE) }
    return ScrollGuiDslImpl.SlotElements(structure).apply(gui).build()
}

/**
 * Creates a [ScrollGui] of [SlotElement]s using the DSL, inheriting ingredients from the
 * enclosing [IngredientsDsl] scope. Inherited ingredients can be overridden by redefining the same
 * character in the inner [gui] block.
 *
 * ```
 * ingredients {
 *     '#' by borderItem
 *
 *     val myGui = scrollSlotElementsGui(
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
 * @see ScrollGuiDsl
 */
@ExperimentalDslApi
inline fun IngredientsDsl.scrollSlotElementsGui(vararg structure: String, gui: ScrollGuiDsl<SlotElement>.() -> Unit): ScrollGui<SlotElement> {
    contract { callsInPlace(gui, InvocationKind.EXACTLY_ONCE) }
    return ScrollGuiDslImpl.SlotElements(structure, (this as IngredientsDslImpl).buildPresets()).apply(gui).build()
}

/**
 * Creates a [ScrollGui] of [Gui]s using the DSL.
 *
 * Each string in [structure] represents a row in the GUI, where each character is a slot.
 * Characters are mapped to ingredients (items, inventories, markers, etc.) using the
 * [Char.by][IngredientsDsl.by] infix function inside the [gui] block. Spaces are ignored and
 * can be used for readability.
 *
 * ```
 * val myGui = scrollGuisGui(
 *     "x x x x x",
 *     "x x x x x",
 *     "x x x x x",
 * ) {
 *     'x' by Markers.CONTENT_LIST_SLOT_HORIZONTAL
 *     content by guis
 * }
 * ```
 *
 * @see ScrollGuiDsl
 */
@ExperimentalDslApi
inline fun scrollGuisGui(vararg structure: String, gui: ScrollGuiDsl<Gui>.() -> Unit): ScrollGui<Gui> {
    contract { callsInPlace(gui, InvocationKind.EXACTLY_ONCE) }
    return ScrollGuiDslImpl.Guis(structure).apply(gui).build()
}

/**
 * Creates a [ScrollGui] of [Gui]s using the DSL, inheriting ingredients from the
 * enclosing [IngredientsDsl] scope. Inherited ingredients can be overridden by redefining the same
 * character in the inner [gui] block.
 *
 * ```
 * ingredients {
 *     '#' by borderItem
 *
 *     val myGui = scrollGuisGui(
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
 * @see ScrollGuiDsl
 */
@ExperimentalDslApi
inline fun IngredientsDsl.scrollGuisGui(vararg structure: String, gui: ScrollGuiDsl<Gui>.() -> Unit): ScrollGui<Gui> {
    contract { callsInPlace(gui, InvocationKind.EXACTLY_ONCE) }
    return ScrollGuiDslImpl.Guis(structure, (this as IngredientsDslImpl).buildPresets()).apply(gui).build()
}

/**
 * Creates a [ScrollGui] of [Inventory]s using the DSL.
 *
 * Each string in [structure] represents a row in the GUI, where each character is a slot.
 * Characters are mapped to ingredients (items, inventories, markers, etc.) using the
 * [Char.by][IngredientsDsl.by] infix function inside the [gui] block. Spaces are ignored and
 * can be used for readability.
 *
 * ```
 * val myGui = scrollInventoriesGui(
 *     "x x x x x",
 *     "x x x x x",
 *     "x x x x x",
 * ) {
 *     'x' by Markers.CONTENT_LIST_SLOT_HORIZONTAL
 *     content by inventories
 * }
 * ```
 *
 * @see ScrollGuiDsl
 */
@ExperimentalDslApi
inline fun scrollInventoriesGui(vararg structure: String, gui: ScrollGuiDsl<Inventory>.() -> Unit): ScrollGui<Inventory> {
    contract { callsInPlace(gui, InvocationKind.EXACTLY_ONCE) }
    return ScrollGuiDslImpl.Inventories(structure).apply(gui).build()
}

/**
 * Creates a [ScrollGui] of [Inventory]s using the DSL, inheriting ingredients from the
 * enclosing [IngredientsDsl] scope. Inherited ingredients can be overridden by redefining the same
 * character in the inner [gui] block.
 *
 * ```
 * ingredients {
 *     '#' by borderItem
 *
 *     val myGui = scrollInventoriesGui(
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
 * @see ScrollGuiDsl
 */
@ExperimentalDslApi
inline fun IngredientsDsl.scrollInventoriesGui(vararg structure: String, gui: ScrollGuiDsl<Inventory>.() -> Unit): ScrollGui<Inventory> {
    contract { callsInPlace(gui, InvocationKind.EXACTLY_ONCE) }
    return ScrollGuiDslImpl.Inventories(structure, (this as IngredientsDslImpl).buildPresets()).apply(gui).build()
}

/**
 * DSL scope for configuring a [ScrollGui].
 *
 * Extends [GuiDsl] with scroll-specific properties: [content] to define what is scrolled through,
 * [line] to control the current scroll position, and [lineCount] / [maxLine] to observe the
 * scroll bounds.
 *
 * ```
 * val myGui = scrollItemsGui(
 *     "x x x x x",
 *     "x x x x x",
 *     "x x x x x",
 * ) {
 *     'x' by Markers.CONTENT_LIST_SLOT_HORIZONTAL
 *     content by items
 *     line by 0
 * }
 * ```
 *
 * @param C The content type being scrolled (e.g. [Item], [SlotElement], [Gui], [Inventory]).
 */
@ExperimentalDslApi
sealed interface ScrollGuiDsl<C : Any> : GuiDsl {
    
    /**
     * A [Provider] that resolves to the built [ScrollGui] instance.
     *
     * Can be used to obtain a reference to the GUI after the DSL block finishes and
     * the GUI is built. Accessing it before the GUI is built throws an [IllegalStateException].
     */
    override val gui: Provider<ScrollGui<C>>
    
    /**
     * The list of content elements to scroll through.
     *
     * Defaults to an empty list. Can be set to a static value or bound to a [Provider]:
     * ```
     * content by listOf(itemA, itemB, itemC)
     * ```
     */
    val content: ProviderDslProperty<List<C>>
    
    /**
     * The current scroll line (i.e. the index of the first displayed line).
     *
     * Defaults to `0`. Can be set to a static value or bound to a [MutableProvider][xyz.xenondevs.commons.provider.MutableProvider]:
     * ```
     * line by 0
     * ```
     */
    val line: MutableProviderDslProperty<Int>
    
    /**
     * The total number of lines in the content. This is a read-only [Provider] that updates
     * automatically as [content] changes.
     */
    val lineCount: Provider<Int>
    
    /**
     * The maximum selectable line index. This is the highest value [line] can be set to while
     * still having content visible. This is a read-only [Provider] that updates automatically
     * as [content] changes.
     */
    val maxLine: Provider<Int>
    
}

@PublishedApi
@ExperimentalDslApi
internal abstract class ScrollGuiDslImpl<C : Any>(
    structure: Array<out String>,
    presets: List<IngredientPreset>
) : GuiDslImpl<ScrollGui<C>, ScrollGui.Builder<C>>(structure, presets), ScrollGuiDsl<C> {
    
    private val internalLineCount = mutableProvider { provider(0) }
    private val internalMaxLine = mutableProvider { provider(0) }
    
    private var _content = provider(emptyList<C>())
    private var _line = mutableProvider(0)
    
    override val content: ProviderDslProperty<List<C>>
        get() = ProviderDslProperty(::_content)
    override val line: MutableProviderDslProperty<Int>
        get() = MutableProviderDslProperty(::_line)
    override val lineCount = internalLineCount.flatten()
    override val maxLine = internalMaxLine.flatten()
    
    override fun applyToBuilder(builder: ScrollGui.Builder<C>) {
        super.applyToBuilder(builder)
        builder.apply {
            setContent(_content)
            setLine(_line)
            
            addModifier { gui ->
                internalLineCount.set(gui.lineCountProvider)
                internalMaxLine.set(gui.maxLineProvider)
            }
        }
    }
    
    @PublishedApi
    internal class Items(
        structure: Array<out String>,
        presets: List<IngredientPreset> = emptyList()
    ) : ScrollGuiDslImpl<Item>(structure, presets) {
        override fun createBuilder() = ScrollGui.itemsBuilder()
    }
    
    @PublishedApi
    internal class SlotElements(
        structure: Array<out String>,
        presets: List<IngredientPreset> = emptyList()
    ) : ScrollGuiDslImpl<SlotElement>(structure, presets) {
        override fun createBuilder() = ScrollGui.slotElementsBuilder()
    }
    
    @PublishedApi
    internal class Guis(
        structure: Array<out String>,
        presets: List<IngredientPreset> = emptyList()
    ) : ScrollGuiDslImpl<Gui>(structure, presets) {
        override fun createBuilder() = ScrollGui.guisBuilder()
    }
    
    @PublishedApi
    internal class Inventories(
        structure: Array<out String>,
        presets: List<IngredientPreset> = emptyList()
    ) : ScrollGuiDslImpl<Inventory>(structure, presets) {
        override fun createBuilder() = ScrollGui.inventoriesBuilder()
    }
    
}