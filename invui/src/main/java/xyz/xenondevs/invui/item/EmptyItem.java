package xyz.xenondevs.invui.item;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import xyz.xenondevs.invui.Click;
import xyz.xenondevs.invui.window.AbstractWindow;

final class EmptyItem extends AbstractItem {
    
    @Override
    public ItemProvider getItemProvider(Player viewer) {
        return ItemProvider.EMPTY;
    }
    
    @Override
    public void handleClick(ClickType clickType, Player player, Click click) {
        // empty
    }
    
    @Override
    public void addViewer(AbstractWindow<?> who, int how) {
        // empty
    }
    
    @Override
    public void removeViewer(AbstractWindow<?> who, int how) {
        // empty
    }
    
    @Override
    public void notifyWindows() {
        // empty
    }
    
}
