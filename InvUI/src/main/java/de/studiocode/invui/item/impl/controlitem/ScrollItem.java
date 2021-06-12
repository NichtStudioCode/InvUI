package de.studiocode.invui.item.impl.controlitem;

import de.studiocode.invui.gui.impl.ScrollGUI;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.HashMap;

/**
 * Scrolls in a {@link ScrollGUI}
 */
public abstract class ScrollItem extends ControlItem<ScrollGUI> {
    
    private final HashMap<ClickType, Integer> scroll;
    
    public ScrollItem(int scrollLeftClick) {
        scroll = new HashMap<>();
        scroll.put(ClickType.LEFT, scrollLeftClick);
    }
    
    public ScrollItem(HashMap<ClickType, Integer> scroll) {
        this.scroll = scroll;
    }
    
    @Override
    public void handleClick(ClickType clickType, Player player, InventoryClickEvent event) {
        if (scroll.containsKey(clickType)) getGui().scroll(scroll.get(clickType));
    }
    
}
