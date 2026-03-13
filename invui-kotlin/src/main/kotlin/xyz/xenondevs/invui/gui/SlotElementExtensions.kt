package xyz.xenondevs.invui.gui

import org.bukkit.inventory.ItemStack
import xyz.xenondevs.commons.provider.Provider
import xyz.xenondevs.invui.ExperimentalReactiveApi
import xyz.xenondevs.invui.PropertyAdapter
import xyz.xenondevs.invui.inventory.Inventory
import xyz.xenondevs.invui.item.ItemProvider

@ExperimentalReactiveApi
fun InventoryLink(
    inventory: Inventory,
    slot: Int,
    background: Provider<ItemProvider>,
    visualizer: (ItemStack?) -> ItemProvider? = { null }
) = SlotElement.InventoryLink(inventory, slot, PropertyAdapter(background), visualizer)