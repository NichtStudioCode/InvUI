package xyz.xenondevs.invui.item;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jspecify.annotations.Nullable;

import java.util.function.Function;
import java.util.function.Supplier;

public class SuppliedItem extends AbstractItem {
    
    private final Supplier<? extends ItemProvider> builderSupplier;
    private final Function<Click, Boolean> clickHandler;
    
    public SuppliedItem(Supplier<? extends ItemProvider> builderSupplier, @Nullable Function<Click, Boolean> clickHandler) {
        this.builderSupplier = builderSupplier;
        this.clickHandler = clickHandler;
    }
    
    @Override
    public ItemProvider getItemProvider() {
        return builderSupplier.get();
    }
    
    @Override
    public void handleClick(ClickType clickType, Player player, InventoryClickEvent event) {
        if (clickHandler != null && clickHandler.apply(new Click(event))) notifyWindows();
    }
    
}
