package xyz.xenondevs.invui.gui

import org.bukkit.inventory.ItemStack
import xyz.xenondevs.commons.provider.Provider
import xyz.xenondevs.commons.provider.provider
import xyz.xenondevs.invui.ExperimentalReactiveApi
import xyz.xenondevs.invui.PropertyAdapter
import xyz.xenondevs.invui.inventory.Inventory
import xyz.xenondevs.invui.item.ItemProvider

/**
 * Creates a new [SlotElement.InventoryLink] that links to [slot] in [inventory],
 * uses [background] if no item is present, and transforms all item stacks using [visualizer].
 */
@ExperimentalReactiveApi
fun InventoryLink(
    inventory: Inventory,
    slot: Int,
    background: Provider<ItemProvider> = provider(ItemProvider.EMPTY),
    visualizer: (ItemStack?) -> ItemProvider? = { null }
) = SlotElement.InventoryLink(inventory, slot, PropertyAdapter(background), visualizer)