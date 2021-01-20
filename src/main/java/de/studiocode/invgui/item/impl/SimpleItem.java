package de.studiocode.invgui.item.impl;

import de.studiocode.invgui.item.Item;
import de.studiocode.invgui.item.itembuilder.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

/**
 * A simple {@link Item} that does nothing.
 */
public class SimpleItem extends BaseItem {
    
    private final ItemBuilder itemBuilder;
    
    public SimpleItem(@NotNull ItemBuilder itemBuilder) {
        this.itemBuilder = itemBuilder;
    }
    
    public ItemBuilder getItemBuilder() {
        return itemBuilder;
    }
    
    @Override
    public void handleClick(ClickType clickType, Player player, InventoryClickEvent event) {
        // empty
    }
    
}
