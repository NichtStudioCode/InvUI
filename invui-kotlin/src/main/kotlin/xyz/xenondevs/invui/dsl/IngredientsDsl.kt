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

/**
 * Opens an [IngredientsDsl] scope, allowing ingredient mappings to be defined and shared across
 * multiple GUIs.
 *
 * ```
 * ingredients {
 *     '#' by borderItem
 *     'x' by closeButton
 *
 *     val mainGui = gui(
 *         "# # # # # # # # #",
 *         "# . . . . . . . #",
 *         "# # # # x # # # #",
 *     ) {
 *         // '#' and 'x' are inherited
 *     }
 *
 *     val settingsGui = gui(
 *         "# # # # # # # # #",
 *         "# . . . . . . . #",
 *         "# # # # x # # # #",
 *     ) {
 *         // '#' and 'x' are inherited here too
 *     }
 * }
 * ```
 */
@ExperimentalDslApi
inline fun ingredients(run: IngredientsDsl.() -> Unit) {
    contract { callsInPlace(run, InvocationKind.EXACTLY_ONCE) }
    IngredientsDslImpl().run()
}

/**
 * Opens a nested [IngredientsDsl] scope, inheriting ingredients from the enclosing scope.
 * Inherited ingredients can be overridden by redefining the same character in the inner block.
 *
 * ```
 * ingredients {
 *     '#' by borderItem
 *
 *     ingredients {
 *         // '#' is inherited
 *         'x' by someItem
 *
 *         val myGui = gui(
 *             "# # # # # # # # #",
 *             "# . . . x . . . #",
 *             "# # # # # # # # #",
 *         ) {
 *             // both '#' and 'x' are inherited
 *         }
 *     }
 * }
 * ```
 */
@ExperimentalDslApi
inline fun IngredientsDsl.ingredients(run: IngredientsDsl.() -> Unit) {
    contract { callsInPlace(run, InvocationKind.EXACTLY_ONCE) }
    IngredientsDslImpl((this as IngredientsDslImpl).buildPresets()).run()
}

/**
 * Pairs an [Inventory] with a static [ItemProvider] background to be used as an ingredient.
 *
 * Created via the [Inventory.with] infix function:
 * ```
 * 'i' by (myInventory with ItemStack(Material.GRAY_STAINED_GLASS_PANE))
 * ```
 *
 * @see InventoryWithBackgroundProvider
 */
@ExperimentalDslApi
data class InventoryWithBackground(
    val inventory: Inventory,
    val background: ItemProvider
)

/**
 * Pairs an [Inventory] with a reactive [Provider]-based [ItemProvider] background to be used as
 * an ingredient.
 *
 * Created via the [Inventory.with] infix function:
 * ```
 * 'i' by (myInventory with myItemProviderProvider)
 * ```
 *
 * @see InventoryWithBackground
 */
@ExperimentalDslApi
data class InventoryWithBackgroundProvider(
    val inventory: Inventory,
    val background: Provider<ItemProvider>
)

/**
 * Pairs this [Inventory] with a static [ItemProvider] background for use as an ingredient.
 *
 * ```
 * 'i' by (myInventory with ItemStack(Material.GRAY_STAINED_GLASS_PANE))
 * ```
 */
@ExperimentalDslApi
infix fun Inventory.with(background: ItemProvider): InventoryWithBackground =
    InventoryWithBackground(this, background)

/**
 * Pairs this [Inventory] with a reactive [Provider]-based [ItemProvider] background for use as
 * an ingredient.
 *
 * ```
 * 'i' by (myInventory with myItemProviderProvider)
 * ```
 */
@ExperimentalDslApi
infix fun Inventory.with(background: Provider<ItemProvider>): InventoryWithBackgroundProvider =
    InventoryWithBackgroundProvider(this, background)

/**
 * DSL scope for defining character-to-ingredient mappings used in GUI structure strings.
 *
 * Ingredients are mapped using the [Char.by] infix function, which has overloads for all
 * supported ingredient types:
 *
 * ```
 * gui(
 *     "# # # # # # # # #",
 *     "# . . . x . . . #",
 *     "# # # # # # # # #",
 * ) {
 *     '#' by borderItem                  // Item
 *     'x' by ItemStack(Material.DIAMOND) // ItemStack
 * }
 * ```
 *
 * Ingredients can also be dynamic by using suppliers or [Provider]s:
 * ```
 * 'x' by { createItemForSlot() }        // () -> Item supplier
 * 'x' by { slots -> slots.map { ... } } // (List<Slot>) -> List<Item> supplier
 * 'x' by myReactiveItemProviderProvider // Provider<ItemProvider>
 * ```
 *
 * @see GuiDsl
 */
@GuiDslMarker
@ExperimentalDslApi
sealed interface IngredientsDsl {
    
    /**
     * Applies a pre-built [IngredientPreset] to this scope, adding all its ingredient mappings.
     */
    fun applyPreset(preset: IngredientPreset)
    
    /**
     * Maps this character to a [Marker], used to designate slots for special purposes like
     * content list areas in paged, scroll, or tab GUIs.
     *
     * ```
     * 'x' by Markers.CONTENT_LIST_SLOT_HORIZONTAL
     * ```
     */
    infix fun Char.by(marker: Marker)
    
    /**
     * Maps this character to an [Item].
     *
     * ```
     * 'x' by myItem
     * ```
     */
    infix fun Char.by(item: Item)
    
    /**
     * Maps this character to an [Item.Builder], which will be built into a separate [Item]
     * instance for each slot this character occupies.
     *
     * ```
     * 'x' by Item.builder().setItemProvider(myProvider)
     * ```
     */
    infix fun Char.by(itemBuilder: Item.Builder<*>)
    
    /**
     * Maps this character to a static [ItemProvider], creating a non-interactive display item.
     *
     * ```
     * 'x' by ItemWrapper(myItemStack)
     * ```
     */
    infix fun Char.by(itemProvider: ItemProvider)
    
    /**
     * Maps this character to a reactive [Provider]-based [ItemProvider] that updates automatically
     * when the provider's value changes.
     *
     * ```
     * 'x' by itemProvider(ItemType.DIAMOND) {
     *     name by "<aqua>Reactive Item"
     * }
     * ```
     */
    infix fun Char.by(itemProvider: Provider<ItemProvider>)
    
    /**
     * Maps this character to an [ItemStack], creating a non-interactive display item.
     *
     * ```
     * '#' by ItemStack(Material.GRAY_STAINED_GLASS_PANE)
     * ```
     */
    infix fun Char.by(itemStack: ItemStack)
    
    /**
     * Maps this character to a [SlotElement] directly.
     *
     * ```
     * 'x' by SlotElement.Item(myItem)
     * ```
     */
    infix fun Char.by(element: SlotElement)
    
    /**
     * Maps this character to an [Inventory], making those slots interact with the inventory's
     * contents.
     *
     * ```
     * 'i' by myInventory
     * ```
     */
    infix fun Char.by(inventory: Inventory)
    
    /**
     * Maps this character to an [Inventory] with a static background [ItemProvider] shown in
     * empty inventory slots.
     *
     * ```
     * 'i' by (myInventory with ItemWrapper(ItemStack(Material.GRAY_STAINED_GLASS_PANE)))
     * ```
     *
     * @see with
     */
    infix fun Char.by(inventory: InventoryWithBackground)
    
    /**
     * Maps this character to an [Inventory] with a reactive [Provider]-based background
     * [ItemProvider] shown in empty inventory slots.
     *
     * ```
     * 'i' by (myInventory with myBackgroundProvider)
     * ```
     *
     * @see with
     */
    infix fun Char.by(inventory: InventoryWithBackgroundProvider)
    
    /**
     * Maps this character to a [Gui], embedding it within the parent GUI.
     *
     * ```
     * 'g' by gui(
     *     "# # #",
     *     "# x #",
     *     "# # #",
     * ) {
     *     // '#' is inherited from outer scope, if it is present there
     *     'x' by someItem
     * }
     * ```
     */
    infix fun Char.by(gui: Gui)
    
    /**
     * Maps this character to a supplier that creates a new [Item] for each slot this character
     * occupies.
     *
     * ```
     * 'x' by { createUniqueItem() }
     * ```
     */
    @OverloadResolutionByLambdaReturnType
    @JvmName("byItemSupplier")
    infix fun Char.by(supplier: () -> Item)
    
    /**
     * Maps this character to a supplier that creates a new [ItemProvider] for each slot this
     * character occupies, wrapping each in a simple [Item].
     *
     * ```
     * 'x' by { createUniqueItemProvider() }
     * ```
     */
    @OverloadResolutionByLambdaReturnType
    @JvmName("byItemProviderSupplier")
    infix fun Char.by(supplier: () -> ItemProvider)
    
    /**
     * Maps this character to a supplier that creates a new [ItemStack] for each slot this
     * character occupies, wrapping each in a simple [Item].
     *
     * ```
     * 'x' by { ItemStack(Material.DIAMOND) }
     * ```
     */
    @OverloadResolutionByLambdaReturnType
    @JvmName("byItemStackSupplier")
    infix fun Char.by(supplier: () -> ItemStack)
    
    /**
     * Maps this character to a supplier that creates a new [SlotElement] for each slot this
     * character occupies.
     *
     * ```
     * 'x' by { SlotElement.Item(createUniqueItem()) }
     * ```
     */
    @OverloadResolutionByLambdaReturnType
    @JvmName("bySlotElementSupplier")
    infix fun Char.by(supplier: () -> SlotElement)
    
    /**
     * Maps this character to a supplier that receives the [Slot] positions assigned to this
     * character and returns a [SlotElement] for each. Useful when the element depends on its
     * position in the GUI.
     *
     * ```
     * 'x' by { slots -> slots.map { slot -> SlotElement.Item(createItemAt(slot)) } }
     * ```
     */
    @OverloadResolutionByLambdaReturnType
    @JvmName("bySlotElementSupplier")
    infix fun Char.by(supplier: (List<Slot>) -> List<SlotElement>)
    
    /**
     * Maps this character to a supplier that receives the [Slot] positions assigned to this
     * character and returns an [Item] for each. Useful when the item depends on its position
     * in the GUI.
     *
     * ```
     * 'x' by { slots -> slots.map { slot -> createItemAt(slot) } }
     * ```
     */
    @OverloadResolutionByLambdaReturnType
    @JvmName("byItemSupplier")
    infix fun Char.by(supplier: (List<Slot>) -> List<Item>)
    
    /**
     * Maps this character to a Java [Supplier] that creates a new [Item] for each slot this
     * character occupies.
     *
     * ```
     * 'x' by Supplier { createUniqueItem() }
     * ```
     */
    @JvmName("byItemSupplier")
    infix fun Char.by(supplier: Supplier<Item>)
    
    /**
     * Maps this character to a Java [Supplier] that creates a new [SlotElement] for each slot
     * this character occupies.
     *
     * ```
     * 'x' by Supplier { SlotElement.Item(createUniqueItem()) }
     * ```
     */
    @JvmName("bySlotElementSupplier")
    infix fun Char.by(supplier: Supplier<SlotElement>)
    
    /**
     * Maps this character to a [SlotElementSupplier] that creates [SlotElement]s based on
     * the slots assigned to this character.
     *
     * ```
     * 'x' by mySlotElementSupplier
     * ```
     */
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