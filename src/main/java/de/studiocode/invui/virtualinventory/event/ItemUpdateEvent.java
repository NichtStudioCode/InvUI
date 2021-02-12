package de.studiocode.invui.virtualinventory.event;

import de.studiocode.invui.virtualinventory.VirtualInventory;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An event that is called whenever a slot inside a {@link VirtualInventory} gets updated.
 */
public class ItemUpdateEvent extends Event implements Cancellable {
    
    private static final HandlerList handlers = new HandlerList();
    
    private final VirtualInventory virtualInventory;
    private final ItemStack previousItemStack;
    private final ItemStack newItemStack;
    private final Player player;
    private final int slot;
    
    private boolean cancelled;
    
    /**
     * Creates a new {@link ItemUpdateEvent}.
     *
     * @param virtualInventory  The {@link VirtualInventory} where this action takes place.
     * @param player            The {@link Player} who changed the {@link ItemStack} or <code>null</code>
     *                          if it wasn't a {@link Player}
     * @param slot              The slot that is affected
     * @param previousItemStack The {@link ItemStack} that was there previously
     * @param newItemStack      The {@link ItemStack} that will be there if the event isn't cancelled
     */
    public ItemUpdateEvent(@NotNull VirtualInventory virtualInventory, int slot, @Nullable Player player,
                           @Nullable ItemStack previousItemStack, @Nullable ItemStack newItemStack) {
        this.virtualInventory = virtualInventory;
        this.slot = slot;
        this.player = player;
        this.previousItemStack = previousItemStack;
        this.newItemStack = newItemStack;
    }
    
    /**
     * Gets the {@link HandlerList} of this {@link Event}
     *
     * @return The {@link HandlerList} of this {@link Event}
     */
    public static HandlerList getHandlerList() {
        return handlers;
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
     * Gets the {@link Player} who changed the {@link ItemStack} or <code>null</code>
     * if it wasn't a {@link Player}.
     *
     * @return The {@link Player}
     */
    public Player getPlayer() {
        return player;
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
    
    @Override
    public boolean isCancelled() {
        return cancelled;
    }
    
    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
    
    /**
     * Gets the {@link HandlerList} of this {@link Event}
     *
     * @return The {@link HandlerList} of this {@link Event}
     */
    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    
}
