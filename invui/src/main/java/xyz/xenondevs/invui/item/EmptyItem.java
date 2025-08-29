package xyz.xenondevs.invui.item;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import xyz.xenondevs.invui.Click;
import xyz.xenondevs.invui.Observer;

final class EmptyItem implements Item {
    
    @Override
    public ItemProvider getItemProvider(Player viewer) {
        return ItemProvider.EMPTY;
    }
    
    @Override
    public void handleClick(ClickType clickType, Player player, Click click) {
        // empty
    }
    
    
    @Override
    public void addObserver(Observer who, int what, int how) {
        // empty
    }
    
    @Override
    public void removeObserver(Observer who, int what, int how) {
        // empty
    }
    
    @Override
    public void notifyWindows() {
        // empty
    }
    
}
