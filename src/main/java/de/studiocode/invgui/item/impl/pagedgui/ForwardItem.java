package de.studiocode.invgui.item.impl.pagedgui;

import de.studiocode.invgui.gui.impl.PagedGUI;
import de.studiocode.invgui.item.impl.BaseItem;
import de.studiocode.invgui.item.itembuilder.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.function.Function;

public class ForwardItem extends BaseItem {
    
    private final PagedGUI pagedGUI;
    private final Function<PagedGUI, ItemBuilder> builderFunction;
    
    public ForwardItem(PagedGUI pagedGUI, Function<PagedGUI, ItemBuilder> builderFunction) {
        this.pagedGUI = pagedGUI;
        this.builderFunction = builderFunction;
    }
    
    @Override
    public ItemBuilder getItemBuilder() {
        return builderFunction.apply(pagedGUI);
    }
    
    @Override
    public void handleClick(ClickType clickType, Player player, InventoryClickEvent event) {
        if (clickType == ClickType.LEFT) pagedGUI.goForward();
    }
    
}
