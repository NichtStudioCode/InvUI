package xyz.xenondevs.inventoryaccess.abstraction.util;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.inventoryaccess.component.ComponentWrapper;

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
    
    /**
     * Gets the {@link ItemStack} at a given raw slot from the {@link InventoryView}.
     *
     * @param view The {@link InventoryView}
     * @param slot  The raw slot
     * @return The {@link ItemStack} at the given slot
     */
    @Nullable
    ItemStack getItemStackFromView(@NotNull InventoryView view, int slot);
    
}
