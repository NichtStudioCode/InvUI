package de.studiocode.invui.item.impl;

import de.studiocode.invui.InvUI;
import de.studiocode.invui.item.ItemProvider;
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
 * An {@link Item} that creates it's {@link ItemProvider} asynchronously and displays
 * a placeholder {@link ItemProvider} until the actual {@link ItemProvider} has been created.
 */
public class AsyncItem extends BaseItem {
    
    private volatile ItemProvider itemBuilder;
    
    public AsyncItem(@Nullable ItemProvider itemBuilder, @NotNull Supplier<? extends ItemProvider> builderSupplier) {
        this.itemBuilder = itemBuilder == null ? new ItemBuilder(Material.AIR) : itemBuilder;
        
        Bukkit.getScheduler().runTaskAsynchronously(InvUI.getInstance().getPlugin(), () -> {
            this.itemBuilder = builderSupplier.get();
            Bukkit.getScheduler().runTask(InvUI.getInstance().getPlugin(), this::notifyWindows);
        });
    }
    
    public AsyncItem(@NotNull Supplier<? extends ItemProvider> builderSupplier) {
        this(null, builderSupplier);
    }
    
    @Override
    public ItemProvider getItemBuilder() {
        return itemBuilder;
    }
    
    @Override
    public void handleClick(ClickType clickType, Player player, InventoryClickEvent event) {
        // empty
    }
    
}
