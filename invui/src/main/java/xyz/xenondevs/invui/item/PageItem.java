package xyz.xenondevs.invui.item;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import xyz.xenondevs.invui.gui.PagedGui;

/**
 * Switches between pages in a {@link PagedGui}
 */
public abstract class PageItem extends ControlItem<PagedGui<?>> {
    
    private final boolean forward;
    
    public PageItem(boolean forward) {
        this.forward = forward;
    }
    
    @Override
    public void handleClick(ClickType clickType, Player player, InventoryClickEvent event) {
        if (clickType == ClickType.LEFT) {
            if (forward) getGui().goForward();
            else getGui().goBack();
        }
    }
    
}
