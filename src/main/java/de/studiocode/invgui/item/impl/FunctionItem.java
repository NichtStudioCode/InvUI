package de.studiocode.invgui.item.impl;

import de.studiocode.invgui.item.itembuilder.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.function.Function;

public class FunctionItem<T> extends BaseItem {
    
    private final T t;
    private final Function<T, ItemBuilder> builderFunction;
    
    public FunctionItem(T t, Function<T, ItemBuilder> builderFunction) {
        this.t = t;
        this.builderFunction = builderFunction;
    }
    
    @Override
    public ItemBuilder getItemBuilder() {
        return builderFunction.apply(t);
    }
    
    public T getT() {
        return t;
    }
    
    @Override
    public void handleClick(ClickType clickType, Player player, InventoryClickEvent event) {
        // empty
    }
    
}
