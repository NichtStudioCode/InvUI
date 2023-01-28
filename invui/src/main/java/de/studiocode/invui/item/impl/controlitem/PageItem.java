package de.studiocode.invui.item.impl.controlitem;

import de.studiocode.invui.gui.AbstractPagedGUI;
import de.studiocode.invui.gui.PagedGUI;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Switches between pages in a {@link AbstractPagedGUI}
 */
public abstract class PageItem extends ControlItem<PagedGUI<?>> {
    
    private final boolean forward;
    
    public PageItem(boolean forward) {
        this.forward = forward;
    }
    
    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        if (clickType == ClickType.LEFT) {
            if (forward) getGUI().goForward();
            else getGUI().goBack();
        }
    }
    
}
