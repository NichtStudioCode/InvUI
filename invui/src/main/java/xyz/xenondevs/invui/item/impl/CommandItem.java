package xyz.xenondevs.invui.item.impl;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.ItemProvider;

/**
 * An {@link Item} that will force a player to run a command or say something in the chat when clicked.
 */
public class CommandItem extends SimpleItem {
    
    private final String command;
    
    public CommandItem(ItemProvider itemProvider, String command) {
        super(itemProvider);
        this.command = command;
    }
    
    @Override
    public void handleClick(ClickType clickType, Player player, InventoryClickEvent event) {
        player.chat(command);
    }
    
}
