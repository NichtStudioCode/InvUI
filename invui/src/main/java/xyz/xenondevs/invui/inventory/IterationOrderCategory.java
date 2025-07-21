package xyz.xenondevs.invui.inventory;

import org.bukkit.inventory.ItemStack;
import xyz.xenondevs.invui.inventory.event.UpdateReason;

import java.util.function.Predicate;

/**
 * Category of functions that iterate over slots in the inventory.
 */
public enum IterationOrderCategory {
    
    /**
     * Operations that add items to the inventory, such as {@link Inventory#addItem(UpdateReason, ItemStack)}. 
     */
    ADD,
    
    /**
     * Operations that collect items from the inventory, such as {@link Inventory#collectSimilar(UpdateReason, ItemStack)}.
     */
    COLLECT,
    
    /**
     * Other operations that do not fit into the above categories, such as {@link Inventory#removeFirst(UpdateReason, int, Predicate)}. 
     */
    OTHER
    
}