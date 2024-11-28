package xyz.xenondevs.inventoryaccess.abstraction.util;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.inventoryaccess.map.MapIcon;
import xyz.xenondevs.inventoryaccess.map.MapPatch;

import java.util.List;

public interface PlayerUtils {
    
    /**
     * Stops the advancement listener for a {@link Player}
     *
     * @param player The {@link Player}
     */
    void stopAdvancementListening(@NotNull Player player);
    
    /**
     * Stops the advancement listener for a {@link Player}
     *
     * @param player The {@link Player}
     */
    void stopAdvancementListening(@NotNull Object player);
    
    /**
     * Starts the advancement listener for a {@link Player}
     *
     * @param player The {@link Player}
     */
    void startAdvancementListening(@NotNull Player player);
    
    /**
     * Stops the advancement listener for a {@link Player}
     *
     * @param player The {@link Player}
     */
    void startAdvancementListening(@NotNull Object player);
    
    /**
     * Sends a map update to a {@link Player}
     *
     * @param player   The {@link Player} to receive the map update
     * @param mapId    The id of the map to update
     * @param scale    The scale of the map. From 0 for a fully zoomed-in map
     *                 (1 block per pixel) to 4 for a fully zoomed-out map (16 blocks per pixel)
     * @param locked   If the map has been locked in the cartography table
     * @param mapPatch The {@link MapPatch} to update, can be null
     * @param icons    The {@link MapIcon new icons} to be displayed, can be null
     */
    void sendMapUpdate(@NotNull Player player, int mapId, byte scale, boolean locked, @Nullable MapPatch mapPatch, @Nullable List<MapIcon> icons);
    
}
