package xyz.xenondevs.invui.internal.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.item.ItemBuilder;
import xyz.xenondevs.invui.util.ItemUtils;

import java.util.ArrayList;
import java.util.List;

public class ItemUtils2 {
    
    private static final ItemStack NON_EMPTY_PLACEHOLDER = new ItemBuilder(Material.BARRIER)
        .setName("<red>InvUI Placeholder Item</red>")
        .addLoreLines("<red>Empty slots are not supported at this position</red>")
        .build();
    
    /**
     * Returns the copy of the non-empty placeholder item, which should be used to replace empty slots.
     *
     * @return the non-empty placeholder item
     */
    public static ItemStack getNonEmptyPlaceholder() {
        return NON_EMPTY_PLACEHOLDER.clone();
    }
    
    /**
     * Returns the given item stack if it is not empty, otherwise the non-empty placeholder item.
     *
     * @param itemStack the item stack
     * @return the non-empty item stack
     */
    public static ItemStack nonEmpty(@Nullable ItemStack itemStack) {
        return ItemUtils.isEmpty(itemStack) ? getNonEmptyPlaceholder() : itemStack;
    }
    
    /**
     * Creates a new list where all intermediary empty item stacks are replaced with the non-empty placeholder item
     * and all trailing empty item stacks are removed.
     *
     * @param items the list of item stacks
     * @return the sanitized list of item stacks
     */
    public static List<ItemStack> withoutIntermediaryEmpties(List<@Nullable ItemStack> items) {
        var sanitized = new ArrayList<ItemStack>();
        
        int lastNonNull = -1;
        for (int i = 0; i < items.size(); i++) {
            var itemStack = items.get(i);
            if (itemStack != null) {
                lastNonNull = i;
            }
        }
        
        for (int i = 0; i <= lastNonNull; i++) {
            var itemStack = items.get(i);
            if (ItemUtils.isEmpty(itemStack)) {
                sanitized.add(getNonEmptyPlaceholder());
            } else {
                sanitized.add(itemStack);
            }
        }
        
        return sanitized;
    }
    
}
