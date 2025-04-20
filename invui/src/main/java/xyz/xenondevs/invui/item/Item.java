package xyz.xenondevs.invui.item;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import xyz.xenondevs.invui.Click;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.internal.util.ArrayUtils;
import xyz.xenondevs.invui.util.TriConsumer;
import xyz.xenondevs.invui.window.Window;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A UI element for use in {@link Gui Guis}.
 */
public sealed interface Item permits AbstractItem, BoundItem {
    
    /**
     * Gets the {@link ItemProvider}.
     * This method gets called every time a {@link Window} is updated, triggered by ({@link #notifyWindows()}).
     *
     * @param viewer The {@link Player} that sees the {@link Item}.
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
    
    /**
     * Called when the {@link #getItemProvider(Player) ItemProvider} has bundle contents
     * and the player selects a bundle slot.
     *
     * @param bundleSlot The selected bundle slot, or -1 if the player's cursor left the {@link ItemProvider}.
     */
    void handleBundleSelect(Player player, int bundleSlot);
    
    /**
     * Creates a new {@link Builder} for an {@link Item}.
     *
     * @return A new {@link Builder} for an {@link Item}.
     */
    static Builder<?> builder() {
        return new CustomItem.Builder();
    }
    
    /**
     * Creates a simple {@link Item} with a static {@link ItemStack}.
     *
     * @param itemStack The {@link ItemStack} to display.
     * @return A simple {@link Item}.
     */
    static Item simple(ItemStack itemStack) {
        return builder().setItemProvider(new ItemWrapper(itemStack)).build();
    }
    
    /**
     * Creates a simple {@link Item} with a static {@link ItemProvider}.
     *
     * @param itemProvider The {@link ItemProvider} to display.
     * @return A simple {@link Item}.
     */
    static Item simple(ItemProvider itemProvider) {
        return builder().setItemProvider(itemProvider).build();
    }
    
    /**
     * Creates a simple {@link Item} that resolves its {@link ItemProvider} using the provided function.
     * The function is called every time it is updated, triggered by {@link #notifyWindows()}.
     *
     * @param itemProvider The function that resolves the {@link ItemProvider}, receiving the viewing {@link Player}.
     * @return A simple {@link Item}.
     */
    static Item simple(Function<? super Player, ? extends ItemProvider> itemProvider) {
        return builder().setItemProvider(itemProvider).build();
    }
    
    /**
     * A builder for an {@link Item}.
     *
     * @param <S> The type of the builder itself.
     */
    interface Builder<S extends Builder<S>> {
        
        /**
         * Sets the {@link ItemProvider} of the {@link Item}.
         *
         * @param itemProvider The {@link ItemProvider}.
         * @return This builder.
         */
        S setItemProvider(ItemProvider itemProvider);
        
        /**
         * Sets the function that resolves the {@link ItemProvider}, receiving the viewing {@link Player}.
         * The function is called every time it is updated, triggered by {@link Item#notifyWindows()}.
         *
         * @param itemProvider The function that resolves the {@link ItemProvider}.
         * @return This builder.
         */
        S setItemProvider(Function<? super Player, ? extends ItemProvider> itemProvider);
        
        /**
         * Configures the {@link Item} to automatically cycle through a given array of {@link ItemProvider ItemProviders}.
         *
         * @param period        The period in ticks between each cycle.
         * @param itemProvider  The first {@link ItemProvider}.
         * @param itemProviders The rest of the {@link ItemProvider ItemProviders}.
         * @return This builder.
         */
        default S setCyclingItemProvider(long period, ItemProvider itemProvider, ItemProvider... itemProviders) {
            return setCyclingItemProvider(period, List.of(ArrayUtils.concat(ItemProvider[]::new, itemProvider, itemProviders)));
        }
        
        /**
         * Configures the {@link Item} to automatically cycle through a given list of {@link ItemProvider ItemProviders}.
         *
         * @param period        The period in ticks between each cycle.
         * @param itemProviders The {@link ItemProvider ItemProviders}.
         * @return This builder.
         */
        S setCyclingItemProvider(long period, List<? extends ItemProvider> itemProviders);
        
        /**
         * Configures the resulting {@link Item} to resolve the {@link ItemProvider} asynchronously.
         * Once resolved, the {@link ItemProvider} will stay like this and the function will never be called again.
         * (Not even when calling {@link Item#notifyWindows()})
         * <p>
         * This function invalidates all previously configured {@link ItemProvider ItemProviders}.
         *
         * @param placeholder          The {@link ItemProvider} to display while the actual {@link ItemProvider} is being resolved.
         * @param itemProviderSupplier The supplier that resolves the {@link ItemProvider}.
         * @return This builder.
         */
        S async(ItemProvider placeholder, Supplier<? extends ItemProvider> itemProviderSupplier);
        
        /**
         * Configures the resulting {@link Item} to display a placeholder {@link ItemProvider} until
         * the specified {@link Future} arrives.
         * <p>
         * This function invalidates all previously configured {@link ItemProvider ItemProviders}.
         *
         * @param placeholder        The {@link ItemProvider} to display while the actual {@link ItemProvider} is being resolved.
         * @param itemProviderFuture The future that resolves the {@link ItemProvider}.
         * @return This builder.
         */
        S async(ItemProvider placeholder, CompletableFuture<? extends ItemProvider> itemProviderFuture);
        
        /**
         * Configures the resulting to automatically call {@link #notifyWindows()} every period ticks, while it is
         * being displayed in a {@link Window}.
         *
         * @param period The period in ticks between each update.
         * @return This builder.
         */
        S updatePeriodically(long period);
        
        /**
         * Configures the resulting {@link Item} to automatically call {@link #notifyWindows()} when it is clicked.
         *
         * @return This builder.
         */
        S updateOnClick();
        
        /**
         * Adds a click handler that is called when the {@link Item} is clicked.
         *
         * @param clickHandler The click handler, receiving the {@link Item} itself and the {@link Click}.
         * @return This builder.
         */
        S addClickHandler(BiConsumer<? super Item, ? super Click> clickHandler);
        
        /**
         * Adds a handler that is called when the {@link ItemProvider} has bundle contents and the {@link Player}
         * selects a bundle slot.
         *
         * @param selectHandler The select handler, receiving the {@link Item}, the {@link Player} that interacted
         *                      and the selected bundle slot or -1 if the player's cursor left the {@link ItemProvider}.
         * @return This builder.
         */
        S addBundleSelectHandler(TriConsumer<? super Item, ? super Player, ? super Integer> selectHandler);
        
        /**
         * Adds a modifier that is run on the {@link Item} when it is being built.
         *
         * @param modifier The modifier.
         * @return This builder.
         */
        S addModifier(Consumer<? super Item> modifier);
        
        /**
         * Builds the {@link Item}.
         *
         * @return The {@link Item}.
         */
        Item build();
        
    }
    
}
