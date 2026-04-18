package xyz.xenondevs.invui.util;

import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.key.Key;
import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.item.ItemBuilder;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.ItemWrapper;

import java.util.Objects;

/**
 * Generic item-related utilities.
 */
@SuppressWarnings("UnstableApiUsage")
public final class ItemUtils {
    
    private static @Nullable ItemStack placeholder;
    private static @Nullable ItemWrapper placeholderProvider;
    
    private ItemUtils() {}
    
    /**
     * Checks whether the given {@link ItemStack} is empty.
     * <p>
     * An {@link ItemStack} is considered empty if it is null, air, or has an amount of or less than 0.
     *
     * @param itemStack The {@link ItemStack} to check.
     * @return Whether the {@link ItemStack} is empty.
     */
    public static boolean isEmpty(@Nullable ItemStack itemStack) {
        return itemStack == null || itemStack.isEmpty();
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
     * Clones the given {@link ItemStack} and sets its amount to the given count.
     *
     * @param itemStack the item stack to clone
     * @param count     the amount for the cloned item stack
     * @return the cloned item stack with the given amount, or null if the item stack is empty
     */
    public static @Nullable ItemStack cloneWithCount(@Nullable ItemStack itemStack, int count) {
        if (isEmpty(itemStack))
            return null;
        
        ItemStack clone = itemStack.clone();
        clone.setAmount(count);
        return clone;
    }
    
    /**
     * Gets the amount of the given {@link ItemStack}, or 0 if it is empty.
     *
     * @param itemStack the item stack
     * @return the amount of the item stack, or 0 if it is empty
     */
    public static int getAmount(@Nullable ItemStack itemStack) {
        if (isEmpty(itemStack))
            return 0;
        
        return itemStack.getAmount();
    }
    
    /**
     * Returns a copy of the non-empty placeholder item, which is an invisible non-air item stack.
     *
     * @return the non-empty placeholder item
     */
    public static ItemStack getPlaceholder() {
        if (placeholder == null) {
            placeholder = new ItemBuilder(Material.BARRIER)
                .setName("") // for anvil window default rename text
                .hideTooltip(true)
                .set(DataComponentTypes.ITEM_MODEL, Key.key("air"))
                .build();
        }
        
        return placeholder.clone();
    }
    
    /**
     * Gets an {@link ItemProvider} for the {@link #getPlaceholder() placeholder} item.
     *
     * @return the placeholder item provider
     */
    public static ItemProvider getPlaceholderProvider() {
        if (placeholderProvider == null) {
            placeholderProvider = new ItemWrapper(getPlaceholder());
        }
        
        return placeholderProvider;
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
    
    /**
     * Creates a new {@link ItemStack} of the given target type, copying all data components (prototype + patch) to
     * the new item stack. This will make the new item stack look exactly like the original item stack, except
     * that it is a different type.
     *
     * @param original   the original item stack to copy data components from
     * @param targetType the target type of the new item stack
     * @return a new item stack of the target type with all data components copied from the original item stack
     */
    public static ItemStack asType(ItemStack original, ItemType targetType) {
        if (original.isEmpty())
            return ItemStack.empty();
        
        ItemStack result = targetType.createItemStack(original.getAmount());
        for (var type : Registry.DATA_COMPONENT_TYPE) {
            if (original.hasData(type)) {
                if (type instanceof DataComponentType.Valued<?> valuedType) {
                    copyDataComponent(valuedType, original, result);
                } else if (type instanceof DataComponentType.NonValued nonValuedType) {
                    result.setData(nonValuedType);
                }
            } else {
                result.unsetData(type);
            }
        }
        return result;
    }
    
    private static <T> void copyDataComponent(DataComponentType.Valued<T> type, ItemStack from, ItemStack to) {
        to.setData(type, Objects.requireNonNull(from.getData(type)));
    }
    
}
