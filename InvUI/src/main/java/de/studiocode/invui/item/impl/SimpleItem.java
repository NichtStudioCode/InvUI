package de.studiocode.invui.item.impl;

import de.studiocode.invui.item.Click;
import de.studiocode.invui.item.Item;
import de.studiocode.invui.item.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * A simple {@link Item} that does nothing.
 */
public class SimpleItem extends BaseItem {
    
    private final ItemBuilder itemBuilder;
    private final Consumer<Click> clickHandler;
    
    public SimpleItem(@NotNull ItemBuilder itemBuilder) {
        this.itemBuilder = itemBuilder;
        this.clickHandler = null;
    }
    
    public SimpleItem(@NotNull ItemBuilder itemBuilder, @Nullable Consumer<Click> clickHandler) {
        this.itemBuilder = itemBuilder;
        this.clickHandler = clickHandler;
    }
    
    public ItemBuilder getItemBuilder() {
        return itemBuilder;
    }
    
    @Override
    public void handleClick(ClickType clickType, Player player, InventoryClickEvent event) {
        if (clickHandler != null) clickHandler.accept(new Click(event));
    }
    
}
