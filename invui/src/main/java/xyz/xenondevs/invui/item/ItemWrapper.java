package xyz.xenondevs.invui.item;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;

/**
 * An {@link ItemProvider} that just returns the {@link ItemStack}
 * passed to it in the constructor regardless of the {@link Player#locale() player's locale}.
 * <br>
 * The backing {@link ItemStack} is cloned on creation and when retrieved via {@link #get()},
 * meaning that {@link ItemWrapper ItemWrappers} are immutable {@link ItemProvider ItemProviders}.
 */
public final class ItemWrapper implements ItemProvider {
    
    private final ItemStack itemStack;
    
    /**
     * Creates a new {@link ItemWrapper} using a clone of the given {@link ItemStack}.
     * @param itemStack The {@link ItemStack} to wrap as an {@link ItemProvider}.
     */
    public ItemWrapper(ItemStack itemStack) {
        this.itemStack = itemStack.clone();
    }
    
    @Override
    public ItemStack get(Locale locale) {
        return itemStack.clone();
    }
    
    @Override
    public ItemStack get() {
        return itemStack.clone();
    }
    
    @Override
    public ItemWrapper clone() {
        try {
            return (ItemWrapper) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }
    
}
