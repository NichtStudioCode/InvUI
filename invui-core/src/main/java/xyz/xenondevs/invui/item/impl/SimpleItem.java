package xyz.xenondevs.invui.item.impl;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.item.Click;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.ItemProvider;

import java.util.function.Consumer;

/**
 * A simple {@link Item} that does nothing.
 */
public class SimpleItem extends AbstractItem {
    
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
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        if (clickHandler != null) clickHandler.accept(new Click(event));
    }
    
}
