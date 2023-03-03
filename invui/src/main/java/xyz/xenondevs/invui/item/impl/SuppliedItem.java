package xyz.xenondevs.invui.item.impl;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.item.Click;
import xyz.xenondevs.invui.item.ItemProvider;

import java.util.function.Function;
import java.util.function.Supplier;

public class SuppliedItem extends AbstractItem {
    
    private final Supplier<? extends ItemProvider> builderSupplier;
    private final Function<Click, Boolean> clickHandler;
    
    public SuppliedItem(@NotNull Supplier<? extends ItemProvider> builderSupplier, @Nullable Function<Click, Boolean> clickHandler) {
        this.builderSupplier = builderSupplier;
        this.clickHandler = clickHandler;
    }
    
    @Override
    public ItemProvider getItemProvider() {
        return builderSupplier.get();
    }
    
    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        if (clickHandler != null && clickHandler.apply(new Click(event))) notifyWindows();
    }
    
}
