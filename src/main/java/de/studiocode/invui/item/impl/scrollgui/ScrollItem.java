package de.studiocode.invui.item.impl.scrollgui;

import de.studiocode.invui.gui.impl.ScrollGUI;
import de.studiocode.invui.item.impl.FunctionItem;
import de.studiocode.invui.item.itembuilder.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.HashMap;
import java.util.function.Function;

public class ScrollItem extends FunctionItem<ScrollGUI> {
    
    private final HashMap<ClickType, Integer> scroll;
    
    public ScrollItem(ScrollGUI scrollGUI, int scrollLeftClick, Function<ScrollGUI, ItemBuilder> builderFunction) {
        super(scrollGUI, builderFunction);
        
        scroll = new HashMap<>();
        scroll.put(ClickType.LEFT, scrollLeftClick);
    }
    
    public ScrollItem(ScrollGUI scrollGUI, HashMap<ClickType, Integer> scroll, Function<ScrollGUI, ItemBuilder> builderFunction) {
        super(scrollGUI, builderFunction);
        this.scroll = scroll;
    }
    
    @Override
    public void handleClick(ClickType clickType, Player player, InventoryClickEvent event) {
        if (scroll.containsKey(clickType)) getT().scroll(scroll.get(clickType));
    }
    
}
