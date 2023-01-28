package de.studiocode.invui.item.impl.controlitem;

import de.studiocode.invui.gui.AbstractScrollGUI;
import de.studiocode.invui.gui.ScrollGUI;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

/**
 * Scrolls in a {@link AbstractScrollGUI}
 */
public abstract class ScrollItem extends ControlItem<ScrollGUI<?>> {
    
    private final HashMap<ClickType, Integer> scroll;
    
    public ScrollItem(int scrollLeftClick) {
        scroll = new HashMap<>();
        scroll.put(ClickType.LEFT, scrollLeftClick);
    }
    
    public ScrollItem(HashMap<ClickType, Integer> scroll) {
        this.scroll = scroll;
    }
    
    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        if (scroll.containsKey(clickType)) getGUI().scroll(scroll.get(clickType));
    }
    
}
