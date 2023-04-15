package xyz.xenondevs.invui.item.impl.controlitem;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.AbstractScrollGui;
import xyz.xenondevs.invui.gui.ScrollGui;

import java.util.HashMap;

/**
 * Scrolls in a {@link AbstractScrollGui}
 */
public abstract class ScrollItem extends ControlItem<ScrollGui<?>> {
    
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
        if (scroll.containsKey(clickType)) getGui().scroll(scroll.get(clickType));
    }
    
}
