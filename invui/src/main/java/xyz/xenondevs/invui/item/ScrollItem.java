package xyz.xenondevs.invui.item;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.ScrollGui;

import java.util.HashMap;

/**
 * Scrolls in a {@link ScrollGui}
 */
public abstract class ScrollItem<C> extends AbstractBoundItem {
    
    private final HashMap<ClickType, Integer> scroll;
    
    public ScrollItem(int scrollLeftClick) {
        scroll = new HashMap<>();
        scroll.put(ClickType.LEFT, scrollLeftClick);
    }
    
    public ScrollItem(HashMap<ClickType, Integer> scroll) {
        this.scroll = scroll;
    }
    
    @Override
    public void bind(Gui gui) {
        if (!(gui instanceof ScrollGui<?> scrollGui))
            throw new IllegalArgumentException("ScrollItem can only be used in a ScrollGui");
        
        super.bind(gui);
        scrollGui.addScrollHandler((fromLine, toLine) -> notifyWindows());
    }
    
    @Override
    public ScrollGui<?> getGui() {
        return (ScrollGui<?>) super.getGui();
    }
    
    @Override
    public void handleClick(ClickType clickType, Player player, InventoryClickEvent event) {
        if (scroll.containsKey(clickType)) {
            getGui().scroll(scroll.get(clickType));
        }
    }
    
}
