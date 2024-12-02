package xyz.xenondevs.invui.item;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.jspecify.annotations.Nullable;

import java.util.function.Function;
import java.util.function.Supplier;

public class SuppliedItem extends AbstractItem {
    
    private final Supplier<? extends ItemProvider> builderSupplier;
    private final @Nullable Function<Click, Boolean> clickHandler;
    
    public SuppliedItem(Supplier<? extends ItemProvider> builderSupplier, @Nullable Function<Click, Boolean> clickHandler) {
        this.builderSupplier = builderSupplier;
        this.clickHandler = clickHandler;
    }
    
    @Override
    public ItemProvider getItemProvider(Player viewer) {
        return builderSupplier.get();
    }
    
    @Override
    public void handleClick(ClickType clickType, Player player, Click click) {
        if (clickHandler != null && clickHandler.apply(click)) notifyWindows();
    }
    
}
