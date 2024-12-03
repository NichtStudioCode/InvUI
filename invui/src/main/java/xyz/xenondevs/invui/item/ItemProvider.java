package xyz.xenondevs.invui.item;

import org.bukkit.inventory.ItemStack;

import java.util.Locale;
import java.util.function.Supplier;

/**
 * Provides {@link ItemStack ItemStacks} based on a specified language.
 */
public interface ItemProvider extends Supplier<ItemStack>, Cloneable {
    
    /**
     * An {@link ItemProvider} for an {@link ItemStack}.
     */
    ItemProvider EMPTY = new ItemWrapper(ItemStack.empty());
    
    /**
     * Gets the {@link ItemStack} translated in the specified language.
     *
     * @param locale The language to translate the item to.
     * @return The {@link ItemStack}
     */
    ItemStack get(Locale locale);
    
    /**
     * Gets the {@link ItemStack} without requesting a specific language.
     *
     * @return The {@link ItemStack}
     */
    default ItemStack get() {
        return get(Locale.US);
    }
    
}
