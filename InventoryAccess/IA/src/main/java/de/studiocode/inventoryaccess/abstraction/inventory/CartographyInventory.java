package de.studiocode.inventoryaccess.abstraction.inventory;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public interface CartographyInventory {
    
    /**
     * Gets Bukkit's {@link Inventory} associated to this {@link AnvilInventory}
     *
     * @return The {@link Inventory}
     */
    Inventory getBukkitInventory();
    
    /**
     * Sets an {@link ItemStack} on one of the three slots.
     *
     * @param slot      The slot
     * @param itemStack The {@link ItemStack}
     */
    void setItem(int slot, ItemStack itemStack);
    
    /**
     * Opens the inventory.
     */
    void open();
    
    /**
     * Gets if this inventory is currently open.
     *
     * @return If the inventory is currently open
     */
    boolean isOpen();
    
}
