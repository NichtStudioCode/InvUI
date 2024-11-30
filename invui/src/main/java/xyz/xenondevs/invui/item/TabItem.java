package xyz.xenondevs.invui.item;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.TabGui;

/**
 * Switches between tabs in a {@link TabGui}
 */
public abstract class TabItem<C extends Gui> extends AbstractBoundItem {
    
    private final int tab;
    
    public TabItem(int tab) {
        this.tab = tab;
    }
    
    @Override
    public void bind(Gui gui) {
        if (!(gui instanceof TabGui<?> tabGui))
            throw new IllegalArgumentException("TabItem can only be used in a TabGui");
        
        super.bind(gui);
        tabGui.addTabChangeHandler((oldTab, newTab) -> notifyWindows());
    }
    
    @Override
    public TabGui<?> getGui() {
        return (TabGui<?>) super.getGui();
    }
    
    @Override
    public void handleClick(ClickType clickType, Player player, InventoryClickEvent event) {
        if (clickType == ClickType.LEFT)
            getGui().setTab(tab);
    }
    
}
