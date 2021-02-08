package de.studiocode.invui.item.impl.controlitem;

import de.studiocode.invui.gui.impl.TabGUI;
import de.studiocode.invui.item.itembuilder.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.function.Function;

/**
 * Switches between tabs in a {@link TabGUI}
 */
public class TabItem extends ControlItem<TabGUI> {
    
    private final int tab;
    
    public TabItem(int tab, Function<TabGUI, ItemBuilder> builderFunction) {
        super(builderFunction);
        this.tab = tab;
    }
    
    @Override
    public void handleClick(ClickType clickType, Player player, InventoryClickEvent event) {
        if (clickType == ClickType.LEFT) getGui().showTab(tab);
    }
    
}
