package xyz.xenondevs.invui.item;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

public class Click {
    
    private final Player player;
    private final ClickType clickType;
    private final InventoryClickEvent event;
    
    public Click(InventoryClickEvent event) {
        this.player = (Player) event.getWhoClicked();
        this.clickType = event.getClick();
        this.event = event;
    }
    
    public Player getPlayer() {
        return player;
    }
    
    public ClickType getClickType() {
        return clickType;
    }
    
    public InventoryClickEvent getEvent() {
        return event;
    }
    
}
