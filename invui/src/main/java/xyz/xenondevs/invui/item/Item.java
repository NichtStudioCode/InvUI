package xyz.xenondevs.invui.item;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.window.Window;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * An ui element for use in {@link Gui Guis}.
 */
public sealed interface Item permits AbstractItem, BoundItem {
    
    /**
     * Gets the {@link ItemProvider}.
     * This method gets called every time a {@link Window} is notified ({@link #notifyWindows()}).
     *
     * @param viewer The {@link Player} that sees the {@link Item}.
     * 
     * @return The {@link ItemProvider}
     */
    ItemProvider getItemProvider(Player viewer);
    
    /**
     * Notifies all {@link Window Windows} displaying this {@link Item} to update their
     * representative {@link ItemStack ItemStacks}.
     * <p>
     * Can be called asynchronously.
     */
    void notifyWindows();
    
    /**
     * A method called if the {@link ItemStack} associated to this {@link Item}
     * has been clicked by a player.
     *
     * @param clickType The {@link ClickType} the {@link Player} performed.
     * @param player    The {@link Player} who clicked on the {@link ItemStack}.
     * @param click     Contains additional information about the click event.
     */
    void handleClick(ClickType clickType, Player player, Click click);
    
    static Builder<?> builder() {
        return new CustomItem.Builder();
    }
    
    static Item simple(ItemStack itemStack) {
        return builder().itemProvider(new ItemWrapper(itemStack)).build();
    }
    
    static Item simple(ItemProvider itemProvider) {
        return builder().itemProvider(itemProvider).build();
    }
    
    static Item simple(Supplier<ItemProvider> itemProvider) {
        return builder().itemProvider(itemProvider).build();
    }
    
    static Item simple(Function<Player, ItemProvider> itemProvider) {
        return builder().itemProvider(itemProvider).build();
    }
    
    interface Builder<S extends Builder<S>> {
        
        S itemProvider(ItemProvider itemProvider);
        
        S itemProvider(Supplier<ItemProvider> itemProvider);
        
        S itemProvider(Function<Player, ItemProvider> itemProvider);
        
        S async(ItemProvider placeholder);
        
        S updatePeriodically(long period);
        
        S updateOnClick();
        
        S click(BiConsumer<Item, Click> clickHandler);
        
        S modify(Consumer<Item> modifier);
        
        Item build();
        
    }
    
}
