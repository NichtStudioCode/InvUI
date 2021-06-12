package de.studiocode.invui.item.impl.controlitem;

import de.studiocode.invui.gui.impl.TabGUI;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * Switches between tabs in a {@link TabGUI}
 */
public abstract class TabItem extends ControlItem<TabGUI> {
    
    private final int tab;
    
    public TabItem(int tab) {
        this.tab = tab;
    }
    
    @Override
    public void handleClick(ClickType clickType, Player player, InventoryClickEvent event) {
        if (clickType == ClickType.LEFT) getGui().showTab(tab);
    }
    
}
