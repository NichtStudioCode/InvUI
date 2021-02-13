package de.studiocode.invui.item.impl;

import de.studiocode.invui.InvUI;
import de.studiocode.invui.item.Item;
import de.studiocode.invui.item.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * An {@link Item} that creates it's {@link ItemBuilder} asynchronously and displays
 * a placeholder {@link ItemBuilder} until the actual {@link ItemBuilder} has been created.
 */
public class AsyncItem extends BaseItem {
    
    private volatile ItemBuilder itemBuilder;
    
    public AsyncItem(@Nullable ItemBuilder itemBuilder, @NotNull Supplier<ItemBuilder> builderSupplier) {
        this.itemBuilder = itemBuilder == null ? new ItemBuilder(Material.AIR) : itemBuilder;
        
        Bukkit.getScheduler().runTaskAsynchronously(InvUI.getInstance().getPlugin(), () -> {
            this.itemBuilder = builderSupplier.get();
            Bukkit.getScheduler().runTask(InvUI.getInstance().getPlugin(), this::notifyWindows);
        });
    }
    
    public AsyncItem(@NotNull Supplier<ItemBuilder> builderSupplier) {
        this(null, builderSupplier);
    }
    
    @Override
    public ItemBuilder getItemBuilder() {
        return itemBuilder;
    }
    
    @Override
    public void handleClick(ClickType clickType, Player player, InventoryClickEvent event) {
        // empty
    }
    
}
