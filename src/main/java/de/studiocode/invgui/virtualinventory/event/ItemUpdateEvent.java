package de.studiocode.invgui.virtualinventory.event;

import de.studiocode.invgui.virtualinventory.VirtualInventory;
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
    private final ItemStack itemStack;
    private final Player player;
    private final int slot;
    private final int previousAmount;
    private final int newAmount;
    
    private boolean cancelled;
    
    /**
     * Creates a new {@link ItemUpdateEvent}.
     *
     * @param virtualInventory The {@link VirtualInventory} where this action takes place.
     * @param player           The {@link Player} who changed the {@link ItemStack} or <code>null</code>
     *                         if it wasn't a {@link Player}
     * @param itemStack        The {@link ItemStack} that is affected
     * @param slot             The slot that is affected
     * @param previousAmount   The previous amount of the {@link ItemStack}
     * @param newAmount        The amount that the {@link ItemStack} will have if the event is not being
     *                         cancelled or -1 if not known
     */
    public ItemUpdateEvent(@NotNull VirtualInventory virtualInventory, @Nullable Player player, @NotNull ItemStack itemStack, int slot, int previousAmount,
                           int newAmount) {
        this.virtualInventory = virtualInventory;
        this.player = player;
        this.itemStack = itemStack;
        this.slot = slot;
        this.previousAmount = previousAmount;
        this.newAmount = newAmount;
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
     * Gets the {@link ItemStack} involved in this action.
     *
     * @return The {@link ItemStack}
     */
    public ItemStack getItemStack() {
        return itemStack;
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
     * Gets the previous amount of the {@link ItemStack}
     *
     * @return The previous amount
     */
    public int getPreviousAmount() {
        return previousAmount;
    }
    
    /**
     * Gets the new amount of the {@link ItemStack} if the event
     * isn't being cancelled or -1 if not unknown.
     *
     * @return The new amount
     */
    public int getNewAmount() {
        return newAmount;
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
