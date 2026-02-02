package xyz.xenondevs.invui.item

import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import xyz.xenondevs.commons.provider.Provider
import xyz.xenondevs.invui.Click

internal fun simpleOrConstItem(itemProvider: Provider<ItemProvider>): Item =
    if (itemProvider.isStable) Item.simple(itemProvider.get()) else SimpleDynamicItem(itemProvider)

private class SimpleDynamicItem(private val itemProvider: Provider<ItemProvider>) : AbstractItem() {
    
    init {
        itemProvider.observeWeak(this) { thisRef -> thisRef.notifyWindows() }
    }
    
    override fun getItemProvider(viewer: Player) = itemProvider.get()
    override fun handleClick(clickType: ClickType, player: Player, click: Click) = Unit
    
}