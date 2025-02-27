package xyz.xenondevs.invui;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

/**
 * Contains information about a click on an item by a player.
 *
 * @param player       The player who clicked.
 * @param clickType    The type of click.
 * @param hotbarButton The hotbar key that was pressed (in [0; 8]),
 *                     or -1 if {@link #clickType()} is not {@link ClickType#NUMBER_KEY}.
 */
public record Click(
    Player player,
    ClickType clickType,
    int hotbarButton
) {
    
    public Click(Player player, ClickType clickType) {
        this(player, clickType, -1);
    }
    
}