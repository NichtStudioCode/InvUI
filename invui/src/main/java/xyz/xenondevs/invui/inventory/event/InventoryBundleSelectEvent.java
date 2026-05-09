package xyz.xenondevs.invui.inventory.event;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import xyz.xenondevs.invui.inventory.Inventory;

/**
 * An event that is called when a {@link Player} selects an item from a bundle in an {@link Inventory}.
 */
@ApiStatus.Experimental
public class InventoryBundleSelectEvent {
    
    private final Inventory inventory;
    private final int slot;
    private final Player player;
    private final int bundleSlot;
    
    /**
     * Creates a new {@link InventoryBundleSelectEvent}.
     *
     * @param inventory  The {@link Inventory} containing the bundle.
     * @param slot       The slot of the {@link Inventory} where the bundle is located.
     * @param player     The {@link Player} who selected the item.
     * @param bundleSlot The index of the selected item inside the bundle.
     */
    public InventoryBundleSelectEvent(Inventory inventory, int slot, Player player, int bundleSlot) {
        this.inventory = inventory;
        this.slot = slot;
        this.player = player;
        this.bundleSlot = bundleSlot;
    }
    
    /**
     * Gets the {@link Inventory} containing the bundle.
     *
     * @return The {@link Inventory} containing the bundle.
     */
    public Inventory getInventory() {
        return inventory;
    }
    
    /**
     * Gets the slot of the {@link #getInventory() inventory} where the bundle is located.
     *
     * @return The slot where the bundle is located.
     */
    public int getSlot() {
        return slot;
    }
    
    /**
     * Gets the {@link Player} who selected the item from the bundle.
     *
     * @return The {@link Player} who selected the item.
     */
    public Player getPlayer() {
        return player;
    }
    
    /**
     * Gets the index of the selected item inside the bundle.
     *
     * @return The index of the selected item.
     */
    public int getBundleSlot() {
        return bundleSlot;
    }
    
}
