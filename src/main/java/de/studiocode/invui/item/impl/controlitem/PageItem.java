package de.studiocode.invui.item.impl.controlitem;

import de.studiocode.invui.gui.impl.PagedGUI;
import de.studiocode.invui.item.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.function.Function;

/**
 * Switches between pages in a {@link PagedGUI}
 */
public class PageItem extends ControlItem<PagedGUI> {
    
    private final boolean forward;
    
    public PageItem(boolean forward, Function<PagedGUI, ItemBuilder> builderFunction) {
        super(builderFunction);
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
