package xyz.xenondevs.invui.util;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemUtils {
    
    /**
     * Checks whether the given {@link ItemStack} is empty.
     * <p>
     * An {@link ItemStack} is considered empty if it is null, air, or has an amount of or less than 0.
     *
     * @param itemStack The {@link ItemStack} to check.
     * @return Whether the {@link ItemStack} is empty.
     */
    public static boolean isEmpty(@Nullable ItemStack itemStack) {
        return itemStack == null || itemStack.getType().isAir() || itemStack.getAmount() <= 0;
    }
    
    /**
     * Checks whether the given {@link ItemStack} is empty and returns null if it is.
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
    public static @Nullable ItemStack @NotNull [] clone(@Nullable ItemStack @NotNull [] array) {
        ItemStack[] clone = new ItemStack[array.length];
        for (int i = 0; i < array.length; i++) {
            ItemStack element = array[i];
            if (element != null)
                clone[i] = element.clone();
        }
        
        return clone;
    }
    
}
