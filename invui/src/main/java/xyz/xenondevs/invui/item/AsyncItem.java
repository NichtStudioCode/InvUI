package xyz.xenondevs.invui.item;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.InvUI;

import java.util.function.Supplier;

/**
 * An {@link Item} that creates it's {@link ItemProvider} asynchronously and displays
 * a placeholder {@link ItemProvider} until the actual {@link ItemProvider} has been created.
 */
public class AsyncItem extends AbstractItem {
    
    private volatile ItemProvider itemProvider;
    
    public AsyncItem(@Nullable ItemProvider itemProvider, Supplier<? extends ItemProvider> providerSupplier) {
        this.itemProvider = itemProvider == null ? new ItemWrapper(new ItemStack(Material.AIR)) : itemProvider;
        
        Bukkit.getScheduler().runTaskAsynchronously(InvUI.getInstance().getPlugin(), () -> {
            this.itemProvider = providerSupplier.get();
            notifyWindows();
        });
    }
    
    public AsyncItem(Supplier<? extends ItemProvider> providerSupplier) {
        this(null, providerSupplier);
    }
    
    @Override
    public ItemProvider getItemProvider(Player viewer) {
        return itemProvider;
    }
    
    @Override
    public void handleClick(ClickType clickType, Player player, Click click) {
        // empty
    }
    
}