@file:OptIn(ExperimentalReactiveApi::class)

package xyz.xenondevs.invui.dsl

import xyz.xenondevs.invui.ExperimentalReactiveApi
import xyz.xenondevs.invui.dsl.property.NullableItemProviderDslProperty
import xyz.xenondevs.invui.dsl.property.ProviderDslProperty
import xyz.xenondevs.invui.gui.Gui
import xyz.xenondevs.invui.gui.IngredientPreset
import xyz.xenondevs.invui.gui.PagedGui
import xyz.xenondevs.invui.gui.ScrollGui
import xyz.xenondevs.invui.gui.TabGui
import xyz.xenondevs.invui.gui.setBackground
import xyz.xenondevs.invui.gui.setFrozen
import xyz.xenondevs.invui.gui.setIgnoreObscuredInventorySlots
import xyz.xenondevs.invui.inventory.Inventory
import xyz.xenondevs.invui.item.Item

@ExperimentalDslApi
fun gui(vararg structure: String, gui: GuiDsl.() -> Unit): Gui =
    NormalGuiDslImpl(structure).apply(gui).build()

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
fun scrollItemsGui(vararg structure: String, gui: ScrollGuiDsl<Item>.() -> Unit): ScrollGui<Item> =
    ScrollGuiDslImpl.Items(structure).apply(gui).build()

@ExperimentalDslApi
fun scrollGuisGui(vararg structure: String, gui: ScrollGuiDsl<Gui>.() -> Unit): ScrollGui<Gui> =
    ScrollGuiDslImpl.Guis(structure).apply(gui).build()

@ExperimentalDslApi
fun scrollInventoriesGui(vararg structure: String, gui: ScrollGuiDsl<Inventory>.() -> Unit): ScrollGui<Inventory> =
    ScrollGuiDslImpl.Inventories(structure).apply(gui).build()

@ExperimentalDslApi
fun tabGui(vararg structure: String, gui: TabGuiDsl.() -> Unit): TabGui =
    TabGuiDslImpl(structure).apply(gui).build()

@ExperimentalDslApi
sealed interface GuiDsl : IngredientsDsl {
    
    val background: NullableItemProviderDslProperty
    
    val frozen: ProviderDslProperty<Boolean>
    
    val ignoreObscuredInventorySlots: ProviderDslProperty<Boolean>
    
}

@ExperimentalDslApi
internal abstract class GuiDslImpl<G : Gui, B : Gui.Builder<G, B>>(
    private val structure: Array<out String>,
    presets: List<IngredientPreset>
) : IngredientsDslImpl(presets), GuiDsl {
    
    override val background = NullableItemProviderDslProperty()
    override val frozen = ProviderDslProperty(false)
    override val ignoreObscuredInventorySlots = ProviderDslProperty(false)
    
    fun build(): G = createBuilder().apply(::applyToBuilder).build()
    
    open fun applyToBuilder(builder: B) {
        builder.apply {
            setStructure(*structure)
            for (preset in presets) {
                applyPreset(preset)
            }
            applyPreset(ingredients.build())
            
            setBackground(background.delegate)
            setFrozen(frozen.delegate)
            setIgnoreObscuredInventorySlots(ignoreObscuredInventorySlots.delegate)
        }
    }
    
    abstract fun createBuilder(): B
    
}

@ExperimentalDslApi
internal class NormalGuiDslImpl<B : Gui.Builder<Gui, B>>(
    structure: Array<out String>,
    presets: List<IngredientPreset> = emptyList()
) : GuiDslImpl<Gui, B>(structure, presets) {
    
    @Suppress("UNCHECKED_CAST")
    override fun createBuilder(): B = Gui.builder() as B
    
}