package xyz.xenondevs.invui.item.impl;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.InvUI;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.ItemWrapper;
import xyz.xenondevs.invui.scheduler.UniversalScheduler;

import java.util.function.Supplier;

/**
 * An {@link Item} that creates it's {@link ItemProvider} asynchronously and displays
 * a placeholder {@link ItemProvider} until the actual {@link ItemProvider} has been created.
 */
public class AsyncItem extends AbstractItem {
    
    private volatile ItemProvider itemProvider;
    
    public AsyncItem(@Nullable ItemProvider itemProvider, @NotNull Supplier<? extends ItemProvider> providerSupplier) {
        this.itemProvider = itemProvider == null ? new ItemWrapper(new ItemStack(Material.AIR)) : itemProvider;

        UniversalScheduler universalScheduler = InvUI.getInstance().getUniversalScheduler();

        universalScheduler.runTaskAsynchronously(() -> {
            this.itemProvider = providerSupplier.get();
            universalScheduler.runTask(this::notifyWindows);
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
