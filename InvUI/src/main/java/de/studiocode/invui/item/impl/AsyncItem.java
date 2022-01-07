package de.studiocode.invui.item.impl;

import de.studiocode.invui.InvUI;
import de.studiocode.invui.item.Item;
import de.studiocode.invui.item.ItemProvider;
import de.studiocode.invui.item.ItemWrapper;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * An {@link Item} that creates it's {@link ItemProvider} asynchronously and displays
 * a placeholder {@link ItemProvider} until the actual {@link ItemProvider} has been created.
 */
public class AsyncItem extends BaseItem {
    
    private volatile ItemProvider itemProvider;
    
    public AsyncItem(@Nullable ItemProvider itemProvider, @NotNull Supplier<? extends ItemProvider> providerSupplier) {
        this.itemProvider = itemProvider == null ? new ItemWrapper(new ItemStack(Material.AIR)) : itemProvider;
        
        Bukkit.getScheduler().runTaskAsynchronously(InvUI.getInstance().getPlugin(), () -> {
            this.itemProvider = providerSupplier.get();
            Bukkit.getScheduler().runTask(InvUI.getInstance().getPlugin(), this::notifyWindows);
        });
    }
    
    public AsyncItem(@NotNull Supplier<? extends ItemProvider> providerSupplier) {
        this(null, providerSupplier);
    }
    
    @Override
    public ItemProvider getItemProvider() {
        return itemProvider;
    }
    
    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        // empty
    }
    
}
