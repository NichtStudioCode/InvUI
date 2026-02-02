package xyz.xenondevs.invui.item;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import xyz.xenondevs.invui.Click;

class ConstItem extends AbstractItem{
    
    private final ItemProvider itemProvider;
    
    public ConstItem(ItemProvider itemProvider) {
        this.itemProvider = itemProvider;
    }
    
    @Override
    public ItemProvider getItemProvider(Player viewer) {
        return itemProvider;
    }
    
    @Override
    public void handleClick(ClickType clickType, Player player, Click click) {
        // empty
    }
    
}
