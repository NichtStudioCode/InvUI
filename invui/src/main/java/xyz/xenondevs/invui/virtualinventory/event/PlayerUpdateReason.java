package xyz.xenondevs.invui.virtualinventory.event;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryEvent;

public class PlayerUpdateReason implements UpdateReason {
    
    private final Player player;
    private final InventoryEvent event;
    
    public PlayerUpdateReason(Player player, InventoryEvent event) {
        this.player = player;
        this.event = event;
    }
    
    public Player getPlayer() {
        return player;
    }
    
    public InventoryEvent getEvent() {
        return event;
    }
    
}
