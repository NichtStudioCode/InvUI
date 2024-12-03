package xyz.xenondevs.invui.inventory.event;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryEvent;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.inventory.Inventory;

/**
 * An {@link UpdateReason} that is used when a {@link Player} interacts with an {@link Inventory}
 * that is embedded in a {@link Gui}.
 * 
 * @param player The player who interacted with the inventory.
 * @param event The event that was triggered by the player. Should be treated as read-only.
 */
public record PlayerUpdateReason(Player player, InventoryEvent event) implements UpdateReason {
}
