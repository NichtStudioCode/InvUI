package xyz.xenondevs.invui.item.impl;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.item.Click;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.ItemWrapper;

import java.util.function.Consumer;

/**
 * A simple {@link Item} that does nothing.
 */
public class SimpleItem extends AbstractItem {
    
    private final ItemProvider itemProvider;
    private final Consumer<Click> clickHandler;
    
    public SimpleItem(ItemProvider itemProvider) {
        this.itemProvider = itemProvider;
        this.clickHandler = null;
    }
    
    public SimpleItem(ItemStack itemStack) {
        this.itemProvider = new ItemWrapper(itemStack);
        this.clickHandler = null;
    }
    
    public SimpleItem(ItemProvider itemProvider, @Nullable Consumer<Click> clickHandler) {
        this.itemProvider = itemProvider;
        this.clickHandler = clickHandler;
    }
    
    public SimpleItem(ItemStack itemStack, @Nullable Consumer<Click> clickHandler) {
        this.itemProvider = new ItemWrapper(itemStack);
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
