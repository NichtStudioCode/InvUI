package de.studiocode.invui.item.impl;

import de.studiocode.invui.item.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.function.Supplier;

public class SupplierItem extends BaseItem {
    
    private final Supplier<ItemBuilder> builderSupplier;
    
    public SupplierItem(Supplier<ItemBuilder> builderSupplier) {
        this.builderSupplier = builderSupplier;
    }
    
    @Override
    public ItemBuilder getItemBuilder() {
        return builderSupplier.get();
    }
    
    @Override
    public void handleClick(ClickType clickType, Player player, InventoryClickEvent event) {
        // empty
    }
    
}
