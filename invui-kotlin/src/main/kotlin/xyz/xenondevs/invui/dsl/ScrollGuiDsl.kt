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

@ExperimentalDslApi
inline fun scrollItemsGui(vararg structure: String, gui: ScrollGuiDsl<Item>.() -> Unit): ScrollGui<Item> {
    contract { callsInPlace(gui, InvocationKind.EXACTLY_ONCE) }
    return ScrollGuiDslImpl.Items(structure).apply(gui).build()
}

@ExperimentalDslApi
inline fun IngredientsDsl.scrollItemsGui(vararg structure: String, gui: ScrollGuiDsl<Item>.() -> Unit): ScrollGui<Item> {
    contract { callsInPlace(gui, InvocationKind.EXACTLY_ONCE) }
    return ScrollGuiDslImpl.Items(structure, (this as IngredientsDslImpl).buildPresets()).apply(gui).build()
}

@ExperimentalDslApi
inline fun scrollSlotElementsGui(vararg structure: String, gui: ScrollGuiDsl<SlotElement>.() -> Unit): ScrollGui<SlotElement> {
    contract { callsInPlace(gui, InvocationKind.EXACTLY_ONCE) }
    return ScrollGuiDslImpl.SlotElements(structure).apply(gui).build()
}

@ExperimentalDslApi
inline fun IngredientsDsl.scrollSlotElementsGui(vararg structure: String, gui: ScrollGuiDsl<SlotElement>.() -> Unit): ScrollGui<SlotElement> {
    contract { callsInPlace(gui, InvocationKind.EXACTLY_ONCE) }
    return ScrollGuiDslImpl.SlotElements(structure, (this as IngredientsDslImpl).buildPresets()).apply(gui).build()
}

@ExperimentalDslApi
inline fun scrollGuisGui(vararg structure: String, gui: ScrollGuiDsl<Gui>.() -> Unit): ScrollGui<Gui> {
    contract { callsInPlace(gui, InvocationKind.EXACTLY_ONCE) }
    return ScrollGuiDslImpl.Guis(structure).apply(gui).build()
}

@ExperimentalDslApi
inline fun IngredientsDsl.scrollGuisGui(vararg structure: String, gui: ScrollGuiDsl<Gui>.() -> Unit): ScrollGui<Gui> {
    contract { callsInPlace(gui, InvocationKind.EXACTLY_ONCE) }
    return ScrollGuiDslImpl.Guis(structure, (this as IngredientsDslImpl).buildPresets()).apply(gui).build()
}

@ExperimentalDslApi
inline fun scrollInventoriesGui(vararg structure: String, gui: ScrollGuiDsl<Inventory>.() -> Unit): ScrollGui<Inventory> {
    contract { callsInPlace(gui, InvocationKind.EXACTLY_ONCE) }
    return ScrollGuiDslImpl.Inventories(structure).apply(gui).build()
}

@ExperimentalDslApi
inline fun IngredientsDsl.scrollInventoriesGui(vararg structure: String, gui: ScrollGuiDsl<Inventory>.() -> Unit): ScrollGui<Inventory> {
    contract { callsInPlace(gui, InvocationKind.EXACTLY_ONCE) }
    return ScrollGuiDslImpl.Inventories(structure, (this as IngredientsDslImpl).buildPresets()).apply(gui).build()
}

@ExperimentalDslApi
sealed interface ScrollGuiDsl<C : Any> : GuiDsl {
    
    override val gui: Provider<ScrollGui<C>>
    
    val content: ProviderDslProperty<List<C>>
    val line: MutableProviderDslProperty<Int>
    val lineCount: Provider<Int>
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