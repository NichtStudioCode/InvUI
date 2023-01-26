package de.studiocode.invui.item.impl.controlitem;

import de.studiocode.invui.gui.impl.PagedGUI;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Switches between pages in a {@link PagedGUI}
 */
public abstract class PageItem extends ControlItem<PagedGUI> {
    
    private final boolean forward;
    
    public PageItem(boolean forward) {
        this.forward = forward;
    }
    
    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        if (clickType == ClickType.LEFT) {
            if (forward) getGui().goForward();
            else getGui().goBack();
        }
    }
    
}
