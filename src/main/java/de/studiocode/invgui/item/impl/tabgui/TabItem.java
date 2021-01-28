package de.studiocode.invgui.item.impl.tabgui;

import de.studiocode.invgui.gui.impl.TabGUI;
import de.studiocode.invgui.item.impl.FunctionItem;
import de.studiocode.invgui.item.itembuilder.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.function.Function;

public class TabItem extends FunctionItem<TabGUI> {
    
    private final int tab;
    
    public TabItem(TabGUI tabGUI, int tab, Function<TabGUI, ItemBuilder> builderFunction) {
        super(tabGUI, builderFunction);
        this.tab = tab;
    }
    
    @Override
    public void handleClick(ClickType clickType, Player player, InventoryClickEvent event) {
        if (clickType == ClickType.LEFT) getT().showTab(tab);
    }
    
}
