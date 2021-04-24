package de.studiocode.invui.virtualinventory.event;

import de.studiocode.invui.virtualinventory.VirtualInventory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An event that is called whenever a slot inside a {@link VirtualInventory} gets updated.
 */
public class ItemUpdateEvent {
    
    private final VirtualInventory virtualInventory;
    private final ItemStack previousItemStack;
    private final ItemStack newItemStack;
    private final UpdateReason updateReason;
    private final int slot;
    
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
        this.virtualInventory = virtualInventory;
        this.slot = slot;
        this.updateReason = updateReason;
        this.previousItemStack = previousItemStack;
        this.newItemStack = newItemStack;
    }
    
    /**
     * Gets the {@link VirtualInventory} where this action takes place.
     *
     * @return The {@link VirtualInventory}
     */
    public VirtualInventory getVirtualInventory() {
        return virtualInventory;
    }
    
    /**
     * Gets the {@link UpdateReason} for the calling of this event.
     *
     * @return The reason why this event was called. Probably a {@link PlayerUpdateReason} in most cases.
     */
    public UpdateReason getUpdateReason() {
        return updateReason;
    }
    
    /**
     * Gets the {@link ItemStack} that was there previously.
     *
     * @return The {@link ItemStack}
     */
    public ItemStack getPreviousItemStack() {
        return previousItemStack;
    }
    
    /**
     * The new {@link ItemStack} that will be there if the event isn't cancelled.
     *
     * @return The new {@link ItemStack}
     */
    public ItemStack getNewItemStack() {
        return newItemStack;
    }
    
    /**
     * Gets the slot that is affected.
     *
     * @return The slot
     */
    public int getSlot() {
        return slot;
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
