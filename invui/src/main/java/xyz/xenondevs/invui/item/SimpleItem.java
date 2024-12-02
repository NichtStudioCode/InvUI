package xyz.xenondevs.invui.item;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

/**
 * A simple {@link Item} that does nothing.
 */
public class SimpleItem extends AbstractItem {
    
    private final ItemProvider itemProvider;
    private final @Nullable Consumer<Click> clickHandler;
    
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
    
    public ItemProvider getItemProvider(Player viewer) {
        return itemProvider;
    }
    
    @Override
    public void handleClick(ClickType clickType, Player player, Click click) {
        if (clickHandler != null) clickHandler.accept(click);
    }
    
}
