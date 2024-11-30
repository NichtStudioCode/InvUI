package xyz.xenondevs.invui.item;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.PagedGui;

/**
 * Switches between pages in a {@link PagedGui}
 */
public abstract class PageItem<C> extends AbstractBoundItem {
    
    private final boolean forward;
    
    public PageItem(boolean forward) {
        this.forward = forward;
    }
    
    @Override
    public void bind(Gui gui) {
        if (!(gui instanceof PagedGui<?> pagedGui))
            throw new IllegalArgumentException("PageItem can only be used in a PagedGui");
        
        super.bind(gui);
        pagedGui.addPageChangeHandler((oldPage, newPage) -> notifyWindows());
    }
    
    @Override
    public PagedGui<?> getGui() {
        return (PagedGui<?>) super.getGui();
    }
    
    @Override
    public void handleClick(ClickType clickType, Player player, InventoryClickEvent event) {
        if (clickType == ClickType.LEFT) {
            if (forward)
                getGui().goForward();
            else getGui().goBack();
        }
    }
    
}
