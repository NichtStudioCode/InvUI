package xyz.xenondevs.invui.dsl

import org.bukkit.inventory.ItemStack
import xyz.xenondevs.invui.gui.Gui
import xyz.xenondevs.invui.gui.IngredientPreset
import xyz.xenondevs.invui.gui.Marker
import xyz.xenondevs.invui.gui.PagedGui
import xyz.xenondevs.invui.gui.ScrollGui
import xyz.xenondevs.invui.gui.SlotElement
import xyz.xenondevs.invui.gui.TabGui
import xyz.xenondevs.invui.inventory.Inventory
import xyz.xenondevs.invui.item.Item
import xyz.xenondevs.invui.item.ItemProvider

@ExperimentalDslApi
fun ingredients(run: IngredientsDsl.() -> Unit): Unit =
    IngredientsDslImpl().run()

@GuiDslMarker
@ExperimentalDslApi
sealed interface IngredientsDsl {
    
    fun applyPreset(preset: IngredientPreset)
    
    infix fun Char.by(marker: Marker)
    
    infix fun Char.by(item: Item)
    
    infix fun Char.by(itemProvider: ItemProvider)
    
    infix fun Char.by(itemStack: ItemStack)
    
    infix fun Char.by(element: SlotElement)
    
    infix fun Char.by(inventory: Inventory)
    
    infix fun Char.by(gui: Gui)
    
    fun ingredients(run: IngredientsDsl.() -> Unit)
    
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
internal open class IngredientsDslImpl(
    protected val presets: List<IngredientPreset> = emptyList()
) : IngredientsDsl {
    
    protected val ingredients = IngredientPreset.builder()
    
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
    
    override fun ingredients(run: IngredientsDsl.() -> Unit): Unit =
        IngredientsDslImpl(presets + ingredients.build()).run()
    
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
    
}