package xyz.xenondevs.invui.item;

import xyz.xenondevs.invui.gui.Gui;

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
    
}
