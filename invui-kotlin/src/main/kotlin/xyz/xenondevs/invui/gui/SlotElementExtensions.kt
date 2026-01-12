package xyz.xenondevs.invui.gui

import xyz.xenondevs.commons.provider.Provider
import xyz.xenondevs.invui.ExperimentalReactiveApi
import xyz.xenondevs.invui.PropertyAdapter
import xyz.xenondevs.invui.inventory.Inventory
import xyz.xenondevs.invui.item.ItemProvider

@ExperimentalReactiveApi
fun InventoryLink(inventory: Inventory, slot: Int, background: Provider<ItemProvider>): SlotElement.InventoryLink =
    SlotElement.InventoryLink(inventory, slot, PropertyAdapter(background))