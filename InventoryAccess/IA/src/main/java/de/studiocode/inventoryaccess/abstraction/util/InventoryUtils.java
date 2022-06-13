package de.studiocode.inventoryaccess.abstraction.util;

import de.studiocode.inventoryaccess.component.ComponentWrapper;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    void openCustomInventory(@NotNull Player player, @NotNull Inventory inventory);
    
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
    void openCustomInventory(@NotNull Player player, @NotNull Inventory inventory, @Nullable ComponentWrapper title);
    
    /**
     * Changes the title of the {@link Inventory} the player is currently viewing.
     *
     * @param player The {@link Player}
     * @param title  The new title
     */
    void updateOpenInventoryTitle(@NotNull Player player, @NotNull ComponentWrapper title);
    
}
