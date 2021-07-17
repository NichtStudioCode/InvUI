package de.studiocode.inventoryaccess.abstraction.inventory;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public interface AnvilInventory {
    
    /**
     * Gets Bukkit's {@link Inventory} associated to this {@link AnvilInventory}
     *
     * @return The {@link Inventory}
     */
    Inventory getBukkitInventory();
    
    /**
     * Opens the inventory.
     */
    void open();
    
    /**
     * Sets an {@link ItemStack} on one of the three slots.
     *
     * @param slot      The slot
     * @param itemStack The {@link ItemStack}
     */
    void setItem(int slot, ItemStack itemStack);
    
    /**
     * Gets the rename text the user has typed in the renaming section of the anvil.
     *
     * @return The rename text
     */
    String getRenameText();
    
    /**
     * Gets if this inventory is currently open.
     *
     * @return If the inventory is currently open
     */
    boolean isOpen();
    
}
