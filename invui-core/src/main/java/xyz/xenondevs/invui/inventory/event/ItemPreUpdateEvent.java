package xyz.xenondevs.invui.inventory.event;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.inventory.Inventory;

/**
 * An event that is called whenever a slot inside a {@link Inventory} gets updated.
 */
public class ItemPreUpdateEvent extends ItemUpdateEvent {
    
    private boolean cancelled;
    
    /**
     * Creates a new {@link ItemPreUpdateEvent}.
     *
     * @param inventory  The {@link Inventory} where this action takes place.
     * @param updateReason      The {@link UpdateReason} for the calling of this event.
     *                          This will probably be a {@link PlayerUpdateReason} in most cases but can be a custom one
     *                          if you called the methods in the {@link Inventory} yourself.
     *                          if it wasn't a {@link Player}
     * @param slot              The slot that is affected
     * @param previousItem The {@link ItemStack} that was there previously
     * @param newItem      The {@link ItemStack} that will be there if the event isn't cancelled
     */
    public ItemPreUpdateEvent(@NotNull Inventory inventory, int slot, @Nullable UpdateReason updateReason,
                              @Nullable ItemStack previousItem, @Nullable ItemStack newItem) {
        
        super(inventory, slot, updateReason, previousItem, newItem);
    }
    
    /**
     * Change the {@link ItemStack} that will appear in the {@link Inventory}
     * to a different one.
     *
     * @param newItem The {@link ItemStack} to appear in the {@link Inventory}
     *                     if the {@link ItemPreUpdateEvent} is not cancelled.
     */
    public void setNewItem(@Nullable ItemStack newItem) {
        this.newItemStack = newItem;
    }
    
    /**
     * Gets the cancellation state of this event.
     *
     * @return The cancellation state of this event.
     */
    public boolean isCancelled() {
        return cancelled;
    }
    
    /**
     * Sets the cancellation state of this event.
     *
     * @param cancel If this event should be cancelled.
     */
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
    
}
