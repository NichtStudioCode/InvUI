package xyz.xenondevs.invui.util;

import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.key.Key;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.item.ItemBuilder;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.ItemWrapper;

/**
 * Generic item-related utilities.
 */
@SuppressWarnings("UnstableApiUsage")
public class ItemUtils {
    
    private static final ItemStack PLACEHOLDER = new ItemBuilder(Material.BARRIER)
        .hideTooltip(true)
        .set(DataComponentTypes.ITEM_MODEL, Key.key("air"))
        .build();
    private static final ItemWrapper PLACEHOLDER_PROVIDER = new ItemWrapper(PLACEHOLDER);
    
    /**
     * Checks whether the given {@link ItemStack} is empty.
     * <p>
     * An {@link ItemStack} is considered empty if it is null, air, or has an amount of or less than 0.
     *
     * @param itemStack The {@link ItemStack} to check.
     * @return Whether the {@link ItemStack} is empty.
     */
    public static boolean isEmpty(@Nullable ItemStack itemStack) {
        if (itemStack == null || itemStack.getAmount() <= 0)
            return true;
        
        Material type = itemStack.getType();
        return type == Material.AIR || type == Material.CAVE_AIR || type == Material.VOID_AIR;
    }
    
    /**
     * Checks whether the given {@link ItemStack} is empty and returns null if it is.
     *
     * @param itemStack The {@link ItemStack} to check.
     * @return The {@link ItemStack} if it is not empty or null otherwise.
     */
    public static @Nullable ItemStack takeUnlessEmpty(@Nullable ItemStack itemStack) {
        if (isEmpty(itemStack))
            return null;
        
        return itemStack;
    }
    
    /**
     * Creates a new array with clones of the given {@link ItemStack ItemStacks}.
     *
     * @param array The array to clone.
     * @return The cloned array.
     */
    public static @Nullable ItemStack[] clone(@Nullable ItemStack[] array) {
        ItemStack[] clone = new ItemStack[array.length];
        for (int i = 0; i < array.length; i++) {
            ItemStack element = array[i];
            if (element != null)
                clone[i] = element.clone();
        }
        
        return clone;
    }
    
    /**
     * Clones the given {@link ItemStack} and returns it, unless it is empty, in which case null is returned.
     *
     * @param itemStack The {@link ItemStack} to clone.
     * @return The cloned {@link ItemStack} or null if it is empty.
     */
    public static @Nullable ItemStack cloneUnlessEmpty(@Nullable ItemStack itemStack) {
        if (isEmpty(itemStack))
            return null;
        
        return itemStack.clone();
    }
    
    /**
     * Returns a copy of the non-empty placeholder item, which is an invisible non-air item stack.
     *
     * @return the non-empty placeholder item
     */
    public static ItemStack getPlaceholder() {
        return PLACEHOLDER.clone();
    }
    
    /**
     * Gets an {@link ItemProvider} for the {@link #getPlaceholder() placeholder} item.
     *
     * @return the placeholder item provider
     */
    public static ItemProvider getPlaceholderProvider() {
        return PLACEHOLDER_PROVIDER;
    }
    
    /**
     * Returns the given item stack if it is not {@link #isEmpty(ItemStack) empty},
     * otherwise the {@link #getPlaceholder() placeholder} item.
     *
     * @param itemStack the item stack
     * @return the non-empty item stack
     */
    public static ItemStack takeOrPlaceholder(@Nullable ItemStack itemStack) {
        return isEmpty(itemStack) ? getPlaceholder() : itemStack;
    }
    
}
