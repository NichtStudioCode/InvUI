package xyz.xenondevs.invui.item;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

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
    public void handleClick(ClickType clickType, Player player, Click click) {
        player.chat(command);
    }
    
}
