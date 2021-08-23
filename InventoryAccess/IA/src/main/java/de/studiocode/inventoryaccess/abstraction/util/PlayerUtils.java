package de.studiocode.inventoryaccess.abstraction.util;

import org.bukkit.entity.Player;

public interface PlayerUtils {
    
    /**
     * Stops the advancement listener for a {@link Player}
     *
     * @param player The {@link Player}
     */
    void stopAdvancementListening(Player player);
    
    /**
     * Stops the advancement listener for a player
     *
     * @param player The player
     */
    void stopAdvancementListening(Object player);
    
    /**
     * Starts the advancement listener for a {@link Player}
     *
     * @param player The {@link Player}
     */
    void startAdvancementListening(Player player);
    
    /**
     * Stops the advancement listener for a player
     *
     * @param player The player
     */
    void startAdvancementListening(Object player);
    
}
