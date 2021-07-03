package de.studiocode.inventoryaccess.api.abstraction.util;

import net.md_5.bungee.api.chat.BaseComponent;
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
    
    /**
     * Opens an {@link Inventory} as a custom inventory with a title that differs from the
     * actual title of the {@link Inventory}.
     * Internally, this creates a CraftContainer which can save the {@link InventoryView},
     * unlike when using nms Containers, which is the default way for opening Inventories of
     * TileEntities.
     *
     * @param player    The {@link Player} to open the {@link Inventory} for
     * @param inventory The {@link Inventory}
     * @param title     The title of the inventory
     */
    void openCustomInventory(Player player, Inventory inventory, BaseComponent[] title);
    
    /**
     * Changes the title of the {@link Inventory} the player is currently viewing.
     *
     * @param player The {@link Player}
     * @param title  The new title
     */
    void updateOpenInventoryTitle(Player player, BaseComponent[] title);
    
}
