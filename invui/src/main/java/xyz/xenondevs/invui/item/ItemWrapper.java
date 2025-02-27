package xyz.xenondevs.invui.item;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;

/**
 * An {@link ItemProvider} that just returns the {@link ItemStack}
 * passed to it in the constructor regardless of the {@link Player#locale() player's locale}.
 */
public final class ItemWrapper implements ItemProvider {
    
    private ItemStack itemStack;
    
    public ItemWrapper(ItemStack itemStack) {
        this.itemStack = itemStack;
    }
    
    @Override
    public ItemStack get(Locale locale) {
        return itemStack;
    }
    
    @Override
    public ItemStack get() {
        return itemStack;
    }
    
    @Override
    public ItemWrapper clone() {
        try {
            ItemWrapper clone = (ItemWrapper) super.clone();
            clone.itemStack = itemStack.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }
    
}
