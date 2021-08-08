package de.studiocode.invui.item.impl;

import de.studiocode.invui.item.Click;
import de.studiocode.invui.item.Item;
import de.studiocode.invui.item.ItemProvider;
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
    
    private final ItemProvider itemProvider;
    private final Consumer<Click> clickHandler;
    
    public SimpleItem(@NotNull ItemProvider itemProvider) {
        this.itemProvider = itemProvider;
        this.clickHandler = null;
    }
    
    public SimpleItem(@NotNull ItemProvider itemProvider, @Nullable Consumer<Click> clickHandler) {
        this.itemProvider = itemProvider;
        this.clickHandler = clickHandler;
    }
    
    public ItemProvider getItemProvider() {
        return itemProvider;
    }
    
    @Override
    public void handleClick(ClickType clickType, Player player, InventoryClickEvent event) {
        if (clickHandler != null) clickHandler.accept(new Click(event));
    }
    
}
