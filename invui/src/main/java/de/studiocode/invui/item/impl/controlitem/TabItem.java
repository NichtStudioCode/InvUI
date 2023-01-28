package de.studiocode.invui.item.impl.controlitem;

import de.studiocode.invui.gui.AbstractTabGUI;
import de.studiocode.invui.gui.TabGUI;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Switches between tabs in a {@link AbstractTabGUI}
 */
public abstract class TabItem extends ControlItem<TabGUI> {
    
    private final int tab;
    
    public TabItem(int tab) {
        this.tab = tab;
    }
    
    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        if (clickType == ClickType.LEFT) getGUI().showTab(tab);
    }
    
}
