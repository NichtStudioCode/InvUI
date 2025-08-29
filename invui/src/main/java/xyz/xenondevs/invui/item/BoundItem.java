package xyz.xenondevs.invui.item;

import org.bukkit.entity.Player;
import xyz.xenondevs.invui.Click;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.PagedGui;
import xyz.xenondevs.invui.gui.ScrollGui;
import xyz.xenondevs.invui.gui.TabGui;
import xyz.xenondevs.invui.util.QuadConsumer;
import xyz.xenondevs.invui.util.TriConsumer;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;

/**
 * An item that is bound to a specific {@link Gui}.
 */
public interface BoundItem extends Item {
    
    /**
     * Gets the {@link Gui} this item is bound to.
     *
     * @return The {@link Gui} this item is bound to.
     * @throws IllegalStateException If no {@link Gui} is bound to this item.
     */
    Gui getGui();
    
    /**
     * Binds this item to a specific {@link Gui}.
     * Called when the item is added to a {@link Gui}.
     *
     * @param gui The {@link Gui} to bind this item to.
     * @throws IllegalStateException If this item is already bound to a {@link Gui}.
     */
    void bind(Gui gui);
    
    /**
     * Unbinds this item from the currently bound {@link Gui}.
     * Called when the item is fully removed from a {@link Gui}.
     * 
     * @throws IllegalStateException If this item is not bound to a {@link Gui}.
     */
    void unbind();
    
    /**
     * Checks if this item is already bound to a {@link Gui}.
     *
     * @return Whether this item is already bound to a {@link Gui}.
     */
    boolean isBound();
    
    /**
     * Creates a new {@link Builder} for a {@link BoundItem} that can be bound to any {@link Gui}.
     *
     * @return A new {@link Builder} for a {@link BoundItem}.
     */
    static Builder<Gui> builder() {
        return new CustomBoundItem.Builder<>(Gui.class);
    }
    
    /**
     * Creates a new {@link Builder} for a {@link BoundItem} that can be bound to a {@link PagedGui}.
     * The item will be automatically updated when the bound {@link PagedGui PagedGui's} page or content changes.
     *
     * @return A new {@link Builder} for a {@link BoundItem}.
     */
    static Builder<PagedGui<?>> pagedBuilder() {
        return new CustomBoundItem.Builder.Paged();
    }
    
    /**
     * Creates a new {@link Builder} for a {@link BoundItem} that can be bound to a {@link ScrollGui}.
     * The item will be automatically updated when the bound {@link ScrollGui ScrollGui's} line or content changes.
     *
     * @return A new {@link Builder} for a {@link BoundItem}.
     */
    static Builder<ScrollGui<?>> scrollBuilder() {
        return new CustomBoundItem.Builder.Scroll();
    }
    
    /**
     * Creates a new {@link Builder} for a {@link BoundItem} that can be bound to a {@link TabGui}.
     * The item will be automatically updated when the bound {@link TabGui TabGui's} tab changes.
     *
     * @return A new {@link Builder} for a {@link BoundItem}.
     */
    static Builder<TabGui> tabBuilder() {
        return new CustomBoundItem.Builder.Tab();
    }
    
    /**
     * A builder for a {@link BoundItem}.
     *
     * @param <G> The type of {@link Gui} this item can be bound to.
     */
    sealed interface Builder<G extends Gui> extends Item.Builder<Builder<G>> permits CustomBoundItem.Builder {
        
        /**
         * Adds a bind handler that is called when the item is bound to a {@link Gui}.
         *
         * @param handler The bind handler, receiving the {@link Item} itself and the bound {@link Gui}.
         * @return This builder.
         */
        Builder<G> addBindHandler(BiConsumer<? super Item, ? super G> handler);
        
        /**
         * Adds an unbind handler that is called when the item is unbound from a {@link Gui}.
         *
         * @param handler The unbind handler, receiving the {@link Item} itself and the bound {@link Gui}.
         * @return This builder.
         */
        Builder<G> addUnbindHandler(BiConsumer<? super Item, ? super G> handler);
        
        /**
         * Adds a click handler that is called when the item is clicked.
         *
         * @param handler The click handler, receiving the {@link Item} itself, the bound {@link Gui} and the {@link Click}.
         * @return This builder.
         */
        Builder<G> addClickHandler(TriConsumer<? super Item, ? super G, ? super Click> handler);
        
        /**
         * Adds a handler that is called when the {@link ItemProvider} has bundle contents and the {@link Player}
         * selects a bundle slot.
         *
         * @param handler The select handler, receiving the {@link Item}, the bound {@link Gui}, the {@link Player}
         *                that interacted and the selected bundle slot or -1 if the player's cursor left the
         *                {@link ItemProvider}.
         * @return This builder.
         */
        Builder<G> addBundleSelectHandler(QuadConsumer<? super Item, ? super G, ? super Player, ? super Integer> handler);
        
        /**
         * Sets the item provider that provides the item for a specific player and {@link Gui}.
         *
         * @param itemProvider The item provider.
         * @return This builder.
         */
        Builder<G> setItemProvider(BiFunction<? super Player, ? super G, ? extends ItemProvider> itemProvider);
        
        /**
         * Builds the {@link BoundItem}.
         *
         * @return The {@link BoundItem}.
         */
        @Override
        BoundItem build();
    }
    
}
