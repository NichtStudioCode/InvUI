package xyz.xenondevs.invui.inventory.event;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.inventory.Inventory;

/**
 * An event that is called after the {@link Inventory} has been updated.
 */
public class ItemPostUpdateEvent extends ItemUpdateEvent {
    
    /**
     * Creates a new {@link ItemPreUpdateEvent}.
     *
     * @param inventory  The {@link Inventory} where this action takes place.
     * @param updateReason      The {@link UpdateReason} for the calling of this event.
     *                          This will probably be a {@link PlayerUpdateReason} in most cases but can be a custom one
     *                          if you called the methods in the {@link Inventory} yourself.
     *                          if it wasn't a {@link Player}
     * @param slot              The slot that is affected
     * @param previousItem The {@link ItemStack} that was on that slot previously
     * @param newItem      The {@link ItemStack} that is on that slot now
     */
    public ItemPostUpdateEvent(@NotNull Inventory inventory, int slot, @Nullable UpdateReason updateReason,
                               @Nullable ItemStack previousItem, @Nullable ItemStack newItem) {
        
        super(inventory, slot, updateReason, previousItem, newItem);
    }
    
}
