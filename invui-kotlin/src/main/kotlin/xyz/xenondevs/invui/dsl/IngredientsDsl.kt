@file:Suppress("INAPPLICABLE_JVM_NAME")
@file:OptIn(ExperimentalReactiveApi::class)

package xyz.xenondevs.invui.dsl

import org.bukkit.inventory.ItemStack
import xyz.xenondevs.commons.provider.Provider
import xyz.xenondevs.invui.ExperimentalReactiveApi
import xyz.xenondevs.invui.gui.Gui
import xyz.xenondevs.invui.gui.IngredientPreset
import xyz.xenondevs.invui.gui.Marker
import xyz.xenondevs.invui.gui.Slot
import xyz.xenondevs.invui.gui.SlotElement
import xyz.xenondevs.invui.gui.SlotElementSupplier
import xyz.xenondevs.invui.gui.addIngredient
import xyz.xenondevs.invui.inventory.Inventory
import xyz.xenondevs.invui.item.Item
import xyz.xenondevs.invui.item.ItemProvider
import java.util.function.Supplier
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@ExperimentalDslApi
inline fun ingredients(run: IngredientsDsl.() -> Unit) {
    contract { callsInPlace(run, InvocationKind.EXACTLY_ONCE) }
    IngredientsDslImpl().run()
}

@ExperimentalDslApi
inline fun IngredientsDsl.ingredients(run: IngredientsDsl.() -> Unit) {
    contract { callsInPlace(run, InvocationKind.EXACTLY_ONCE) }
    IngredientsDslImpl((this as IngredientsDslImpl).buildPresets()).run()
}

@ExperimentalDslApi
data class InventoryWithBackground(
    val inventory: Inventory,
    val background: ItemProvider
)

@ExperimentalDslApi
data class InventoryWithBackgroundProvider(
    val inventory: Inventory,
    val background: Provider<ItemProvider>
)

@ExperimentalDslApi
infix fun Inventory.with(background: ItemProvider): InventoryWithBackground =
    InventoryWithBackground(this, background)

@ExperimentalDslApi
infix fun Inventory.with(background: Provider<ItemProvider>): InventoryWithBackgroundProvider =
    InventoryWithBackgroundProvider(this, background)

@GuiDslMarker
@ExperimentalDslApi
sealed interface IngredientsDsl {
    
    fun applyPreset(preset: IngredientPreset)
    
    infix fun Char.by(marker: Marker)
    
    infix fun Char.by(item: Item)
    
    infix fun Char.by(itemBuilder: Item.Builder<*>)
    
    infix fun Char.by(itemProvider: ItemProvider)
    
    infix fun Char.by(itemProvider: Provider<ItemProvider>)
    
    infix fun Char.by(itemStack: ItemStack)
    
    infix fun Char.by(element: SlotElement)
    
    infix fun Char.by(inventory: Inventory)

    infix fun Char.by(inventory: InventoryWithBackground)

    infix fun Char.by(inventory: InventoryWithBackgroundProvider)
    
    infix fun Char.by(gui: Gui)
    
    @OverloadResolutionByLambdaReturnType
    @JvmName("byItemSupplier")
    infix fun Char.by(supplier: () -> Item)
    
    @OverloadResolutionByLambdaReturnType
    @JvmName("byItemProviderSupplier")
    infix fun Char.by(supplier: () -> ItemProvider)
    
    @OverloadResolutionByLambdaReturnType
    @JvmName("byItemStackSupplier")
    infix fun Char.by(supplier: () -> ItemStack)
    
    @OverloadResolutionByLambdaReturnType
    @JvmName("bySlotElementSupplier")
    infix fun Char.by(supplier: () -> SlotElement)
    
    @OverloadResolutionByLambdaReturnType
    @JvmName("bySlotElementSupplier")
    infix fun Char.by(supplier: (List<Slot>) -> List<SlotElement>)
    
    @OverloadResolutionByLambdaReturnType
    @JvmName("byItemSupplier")
    infix fun Char.by(supplier: (List<Slot>) -> List<Item>)
    
    @JvmName("byItemSupplier")
    infix fun Char.by(supplier: Supplier<Item>)
    
    @JvmName("bySlotElementSupplier")
    infix fun Char.by(supplier: Supplier<SlotElement>)
    
    infix fun Char.by(supplier: SlotElementSupplier)
    
}

@PublishedApi
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
    
    override fun Char.by(itemBuilder: Item.Builder<*>) {
        ingredients.addIngredient(this, itemBuilder)
    }
    
    override fun Char.by(itemProvider: ItemProvider) {
        ingredients.addIngredient(this, itemProvider)
    }
    
    override fun Char.by(itemProvider: Provider<ItemProvider>) {
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

    override fun Char.by(inventory: InventoryWithBackground) {
        ingredients.addIngredient(this, inventory.inventory, inventory.background)
    }

    override fun Char.by(inventory: InventoryWithBackgroundProvider) {
        ingredients.addIngredient(this, inventory.inventory, inventory.background)
    }
    
    override fun Char.by(gui: Gui) {
        ingredients.addIngredient(this, gui)
    }
    
    @JvmName("byItemSupplier")
    override fun Char.by(supplier: () -> Item) {
        ingredients.addIngredient(this, supplier)
    }
    
    @JvmName("byItemProviderSupplier")
    override fun Char.by(supplier: () -> ItemProvider) {
        ingredients.addIngredient(this, Supplier { Item.simple(supplier()) })
    }
    
    @JvmName("byItemStackSupplier")
    override fun Char.by(supplier: () -> ItemStack) {
        ingredients.addIngredient(this, Supplier { Item.simple(supplier()) })
    }
    
    @JvmName("bySlotElementSupplier")
    override fun Char.by(supplier: () -> SlotElement) {
        ingredients.addIngredientElementSupplier(this, supplier)
    }
    
    @JvmName("bySlotElementSupplier")
    override fun Char.by(supplier: (List<Slot>) -> List<SlotElement>) {
        ingredients.addIngredient(this, supplier)
    }
    
    @JvmName("byItemSupplier")
    override fun Char.by(supplier: (List<Slot>) -> List<Item>) {
        ingredients.addIngredient(this) { slots -> supplier(slots).map(SlotElement::Item) }
    }
    
    @JvmName("byItemSupplier")
    override fun Char.by(supplier: Supplier<Item>) {
        ingredients.addIngredient(this, supplier)
    }
    
    @JvmName("bySlotElementSupplier")
    override fun Char.by(supplier: Supplier<SlotElement>) {
        ingredients.addIngredientElementSupplier(this, supplier)
    }
    
    override fun Char.by(supplier: SlotElementSupplier) {
        ingredients.addIngredient(this, supplier)
    }
    
    fun buildPresets(): List<IngredientPreset> = presets + ingredients.build()
    
}