package xyz.xenondevs.invui.item;

import org.bukkit.inventory.ItemStack;

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
     * @param lang The language to translate the item in.
     * @return The {@link ItemStack}
     */
    ItemStack get(String lang);
    
    /**
     * Gets the {@link ItemStack} without requesting a specific language.
     *
     * @return The {@link ItemStack}
     */
    default ItemStack get() {
        return get("en_us");
    }
    
}
