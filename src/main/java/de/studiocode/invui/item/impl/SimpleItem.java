package de.studiocode.invui.item.impl;

import de.studiocode.invui.item.Click;
import de.studiocode.invui.item.Item;
import de.studiocode.invui.item.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * A simple {@link Item} that does nothing.
 */
public class SimpleItem extends BaseItem {
    
    private final ItemBuilder itemBuilder;
    
    public SimpleItem(@NotNull ItemBuilder itemBuilder) {
        this.itemBuilder = itemBuilder;
    }
    
    public static SimpleItem of(ItemBuilder itemBuilder, Consumer<Click> clickHandler) {
        return new SimpleItem(itemBuilder) {
            @Override
            public void handleClick(ClickType clickType, Player player, InventoryClickEvent event) {
                clickHandler.accept(new Click(event));
            }
        };
    }
    
    public ItemBuilder getItemBuilder() {
        return itemBuilder;
    }
    
    @Override
    public void handleClick(ClickType clickType, Player player, InventoryClickEvent event) {
        // empty
    }
    
}
