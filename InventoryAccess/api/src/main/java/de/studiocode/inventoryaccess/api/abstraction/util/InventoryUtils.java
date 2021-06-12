package de.studiocode.inventoryaccess.api.abstraction.util;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

public interface InventoryUtils {
    
    /**
     * Opens an {@link Inventory} as a custom inventory.
     * Internally, this creates a CraftContainer which can save the {@link InventoryView},
     * unlike when using nms Containers, which is the default way for opening Inventories of
     * TileEntities.
     *
     * @param player    The {@link Player} to open the {@link Inventory} for
     * @param inventory The {@link Inventory}
     */
    void openCustomInventory(Player player, Inventory inventory);
    
}
