package xyz.xenondevs.invui.item;

import org.bukkit.entity.Player;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.PagedGui;
import xyz.xenondevs.invui.gui.ScrollGui;
import xyz.xenondevs.invui.gui.TabGui;
import xyz.xenondevs.invui.util.TriConsumer;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;

/**
 * An item that is bound to a specific {@link Gui}.
 */
public sealed interface BoundItem extends Item permits AbstractBoundItem {
    
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
     * Checks if this item is already bound to a {@link Gui}.
     *
     * @return Whether this item is already bound to a {@link Gui}.
     */
    boolean isBound();
    
    static Builder<Gui> gui() {
        return new CustomBoundItem.Builder<>();
    }
    
    static Builder<PagedGui<?>> pagedGui() {
        return new CustomBoundItem.Builder.Paged();
    }
    
    static Builder<ScrollGui<?>> scrollGui() {
        return new CustomBoundItem.Builder.Scroll();
    }
    
    static Builder<TabGui> tabGui() {
        return new CustomBoundItem.Builder.Tab();
    }
    
    sealed interface Builder<G extends Gui> extends Item.Builder<Builder<G>> permits CustomBoundItem.Builder {
        
        Builder<G> addBindHandler(BiConsumer<Item, G> handler);
        
        Builder<G> addClickHandler(TriConsumer<Item, G, Click> handler);
        
        Builder<G> setItemProvider(BiFunction<Player, G, ItemProvider> itemProvider);
        
    }
    
}
