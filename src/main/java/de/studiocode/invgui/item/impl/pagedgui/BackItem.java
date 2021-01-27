package de.studiocode.invgui.item.impl.pagedgui;

import de.studiocode.invgui.gui.impl.PagedGUI;
import de.studiocode.invgui.item.impl.FunctionItem;
import de.studiocode.invgui.item.itembuilder.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.function.Function;

public class BackItem extends FunctionItem<PagedGUI> {
    
    public BackItem(PagedGUI pagedGUI, Function<PagedGUI, ItemBuilder> builderFunction) {
        super(pagedGUI, builderFunction);
    }
    
    @Override
    public void handleClick(ClickType clickType, Player player, InventoryClickEvent event) {
        if (clickType == ClickType.LEFT) getT().goBack();
    }
    
}
