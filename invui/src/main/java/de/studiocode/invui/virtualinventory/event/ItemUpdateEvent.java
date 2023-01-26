package de.studiocode.invui.virtualinventory.event;

import de.studiocode.invui.virtualinventory.VirtualInventory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An event that is called whenever a slot inside a {@link VirtualInventory} gets updated.
 */
public class ItemUpdateEvent extends UpdateEvent {
    
    private boolean cancelled;
    
    /**
     * Creates a new {@link ItemUpdateEvent}.
     *
     * @param virtualInventory  The {@link VirtualInventory} where this action takes place.
     * @param updateReason      The {@link UpdateReason} for the calling of this event.
     *                          This will probably be a {@link PlayerUpdateReason} in most cases but can be a custom one
     *                          if you called the methods in the {@link VirtualInventory} yourself.
     *                          if it wasn't a {@link Player}
     * @param slot              The slot that is affected
     * @param previousItemStack The {@link ItemStack} that was there previously
     * @param newItemStack      The {@link ItemStack} that will be there if the event isn't cancelled
     */
    public ItemUpdateEvent(@NotNull VirtualInventory virtualInventory, int slot, @Nullable UpdateReason updateReason,
                           @Nullable ItemStack previousItemStack, @Nullable ItemStack newItemStack) {
        
        super(virtualInventory, slot, updateReason, previousItemStack, newItemStack);
    }
    
    /**
     * Change the {@link ItemStack} that will appear in the {@link VirtualInventory}
     * to a different one.
     *
     * @param newItemStack The {@link ItemStack} to appear in the {@link VirtualInventory}
     *                     if the {@link ItemUpdateEvent} is not cancelled.
     */
    public void setNewItemStack(@Nullable ItemStack newItemStack) {
        this.newItemStack = newItemStack;
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
