@file:OptIn(ExperimentalReactiveApi::class)

package xyz.xenondevs.invui.dsl

import org.bukkit.inventory.ItemStack
import xyz.xenondevs.invui.ExperimentalReactiveApi
import xyz.xenondevs.invui.gui.Gui
import xyz.xenondevs.invui.gui.IngredientPreset
import xyz.xenondevs.invui.gui.Marker
import xyz.xenondevs.invui.gui.PagedGui
import xyz.xenondevs.invui.gui.ScrollGui
import xyz.xenondevs.invui.gui.SlotElement
import xyz.xenondevs.invui.gui.TabGui
import xyz.xenondevs.invui.gui.setBackground
import xyz.xenondevs.invui.gui.setFrozen
import xyz.xenondevs.invui.gui.setIgnoreObscuredInventorySlots
import xyz.xenondevs.invui.inventory.Inventory
import xyz.xenondevs.invui.item.Item
import xyz.xenondevs.invui.item.ItemProvider

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

@MenuDsl
@ExperimentalDslApi
sealed interface GuiDsl {
    
    val background: NullableItemProviderDslProperty
    
    val frozen: ProviderDslProperty<Boolean>
    
    val ignoreObscuredInventorySlots: ProviderDslProperty<Boolean>
    
    fun applyPreset(preset: IngredientPreset)
    
    infix fun Char.by(marker: Marker)
    
    infix fun Char.by(item: Item)
    
    infix fun Char.by(itemProvider: ItemProvider)
    
    infix fun Char.by(itemStack: ItemStack)
    
    infix fun Char.by(element: SlotElement)
    
    infix fun Char.by(inventory: Inventory)
    
    infix fun Char.by(gui: Gui)
    
    fun gui(vararg structure: String, gui: GuiDsl.() -> Unit): Gui
    
    fun pagedItemsGui(vararg structure: String, gui: PagedGuiDsl<Item>.() -> Unit): PagedGui<Item>
    
    fun pagedGuisGui(vararg structure: String, gui: PagedGuiDsl<Gui>.() -> Unit): PagedGui<Gui>
    
    fun pagedInventoriesGui(vararg structure: String, gui: PagedGuiDsl<Inventory>.() -> Unit): PagedGui<Inventory>
    
    fun scrollItemsGui(vararg structure: String, gui: ScrollGuiDsl<Item>.() -> Unit): ScrollGui<Item>
    
    fun scrollGuisGui(vararg structure: String, gui: ScrollGuiDsl<Gui>.() -> Unit): ScrollGui<Gui>
    
    fun scrollInventoriesGui(vararg structure: String, gui: ScrollGuiDsl<Inventory>.() -> Unit): ScrollGui<Inventory>
    
    fun tabGui(vararg structure: String, gui: TabGuiDsl.() -> Unit): TabGui
    
}

@ExperimentalDslApi
internal abstract class GuiDslImpl<G : Gui, B : Gui.Builder<G, B>>(
    private val structure: Array<out String>,
    private val presets: List<IngredientPreset>
) : GuiDsl {
    
    private val ingredients = IngredientPreset.builder()
    
    override val background = NullableItemProviderDslProperty()
    override val frozen = ProviderDslProperty(false)
    override val ignoreObscuredInventorySlots = ProviderDslProperty(false)
    
    override fun applyPreset(preset: IngredientPreset) {
        ingredients.applyPreset(preset)
    }
    
    override fun Char.by(marker: Marker) {
        ingredients.addIngredient(this, marker)
    }
    
    override fun Char.by(item: Item) {
        ingredients.addIngredient(this, item)
    }
    
    override fun Char.by(itemProvider: ItemProvider) {
        ingredients.addIngredient(this, itemProvider)
    }
    
    override fun Char.by(itemStack: ItemStack) {
        ingredients.addIngredient(this, itemStack)
    }
    
    override fun Char.by(element: SlotElement) {
        ingredients.addIngredient(this, element)
    }
    
    override fun Char.by(inventory: Inventory) {
        ingredients.addIngredient(this, inventory)
    }
    
    override fun Char.by(gui: Gui) {
        ingredients.addIngredient(this, gui)
    }
    
    override fun gui(vararg structure: String, gui: GuiDsl.() -> Unit): Gui =
        NormalGuiDslImpl(structure, presets + ingredients.build()).apply(gui).build()
    
    override fun pagedItemsGui(vararg structure: String, gui: PagedGuiDsl<Item>.() -> Unit): PagedGui<Item> =
        PagedGuiDslImpl.Items(structure, presets + ingredients.build()).apply(gui).build()
    
    override fun pagedGuisGui(vararg structure: String, gui: PagedGuiDsl<Gui>.() -> Unit): PagedGui<Gui> =
        PagedGuiDslImpl.Guis(structure, presets + ingredients.build()).apply(gui).build()
    
    override fun pagedInventoriesGui(vararg structure: String, gui: PagedGuiDsl<Inventory>.() -> Unit): PagedGui<Inventory> =
        PagedGuiDslImpl.Inventories(structure, presets + ingredients.build()).apply(gui).build()
    
    override fun scrollItemsGui(vararg structure: String, gui: ScrollGuiDsl<Item>.() -> Unit): ScrollGui<Item> =
        ScrollGuiDslImpl.Items(structure, presets + ingredients.build()).apply(gui).build()
    
    override fun scrollGuisGui(vararg structure: String, gui: ScrollGuiDsl<Gui>.() -> Unit): ScrollGui<Gui> =
        ScrollGuiDslImpl.Guis(structure, presets + ingredients.build()).apply(gui).build()
    
    override fun scrollInventoriesGui(vararg structure: String, gui: ScrollGuiDsl<Inventory>.() -> Unit): ScrollGui<Inventory> =
        ScrollGuiDslImpl.Inventories(structure, presets + ingredients.build()).apply(gui).build()
    
    override fun tabGui(vararg structure: String, gui: TabGuiDsl.() -> Unit): TabGui =
        TabGuiDslImpl(structure, presets + ingredients.build()).apply(gui).build()
    
    fun build(): G = createBuilder().apply(::applyToBuilder).build()
    
    open fun applyToBuilder(builder: B) {
        builder.apply {
            setStructure(*structure)
            for (preset in presets) {
                applyPreset(preset)
            }
            applyPreset(ingredients.build())
            
            setBackground(background.value)
            setFrozen(frozen.value)
            setIgnoreObscuredInventorySlots(ignoreObscuredInventorySlots.value)
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