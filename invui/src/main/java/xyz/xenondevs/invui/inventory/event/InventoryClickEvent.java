package xyz.xenondevs.invui.inventory.event;

import org.bukkit.entity.Player;
import xyz.xenondevs.invui.Click;
import xyz.xenondevs.invui.ClickEvent;
import xyz.xenondevs.invui.inventory.Inventory;

/**
 * An event that is called when a {@link Player} clicks in an {@link Inventory}.
 */
public class InventoryClickEvent extends ClickEvent {
    
    private final Inventory inventory;
    private final int slot;
    
    /**
     * Creates a new {@link InventoryClickEvent}.
     *
     * @param inventory The {@link Inventory} that was clicked.
     * @param slot      The slot that was clicked.
     * @param click     The {@link Click} that was performed.
     */
    public InventoryClickEvent(Inventory inventory, int slot, Click click) {
        super(click);
        this.inventory = inventory;
        this.slot = slot;
    }
    
    /**
     * Gets the {@link Inventory} that was clicked.
     *
     * @return The clicked {@link Inventory}.
     */
    public Inventory getInventory() {
        return inventory;
    }
    
    /**
     * Gets the slot of the {@link #getInventory() inventory} that was clicked.
     *
     * @return The slot that was clicked.
     */
    public int getSlot() {
        return slot;
    }
    
}
