package xyz.xenondevs.invui.inventory.event;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.inventory.Inventory;
import xyz.xenondevs.invui.util.ItemUtils;

/**
 * An event that is called before a slot in an {@link Inventory} is updated.
 * <p>
 * Cancelling this event affects the source of the change, i.e. a {@link Player} would for example keep the item they
 * tried to place on their cursor. In certain situations, changing the amount of {@link #getNewItem()} will also be
 * reflected in the source.
 * <p>
 * Note that a fired {@link ItemPreUpdateEvent} does not necessitate that the given action will actually take place,
 * even if the event remains uncancelled. For example, moving an item from one inventory to another via shift-clicking
 * may be allowed by update handlers of one inventory, but rejected by the other.
 * As such, {@link ItemPreUpdateEvent} is not a reliable way to determine inventory state changes.
 * For that, use {@link ItemPostUpdateEvent} instead.
 */
public class ItemPreUpdateEvent extends ItemUpdateEvent {
    
    private boolean cancelled;
    
    /**
     * Creates a new {@link ItemPreUpdateEvent}.
     *
     * @param inventory    The {@link Inventory} where this action takes place.
     * @param updateReason The {@link UpdateReason} for the calling of this event.
     *                     This will probably be a {@link PlayerUpdateReason} in most cases but can be a custom one
     *                     if you called the methods in the {@link Inventory} yourself.
     *                     if it wasn't a {@link Player}
     * @param slot         The slot that is affected
     * @param previousItem The {@link ItemStack} that was there previously
     * @param newItem      The {@link ItemStack} that will be there if the event isn't cancelled
     */
    public ItemPreUpdateEvent(Inventory inventory, int slot, @Nullable UpdateReason updateReason,
                              @Nullable ItemStack previousItem, @Nullable ItemStack newItem) {
        
        super(inventory, slot, updateReason, previousItem, newItem);
    }
    
    /**
     * Change the {@link ItemStack} that will appear in the {@link Inventory}
     * to a different one.
     *
     * @param newItem The {@link ItemStack} to appear in the {@link Inventory}
     *                if the {@link ItemPreUpdateEvent} is not cancelled.
     */
    public void setNewItem(@Nullable ItemStack newItem) {
        this.newItemStack = ItemUtils.takeUnlessEmpty(newItem);
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
