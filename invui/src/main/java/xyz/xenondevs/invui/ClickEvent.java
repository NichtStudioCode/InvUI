package xyz.xenondevs.invui;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

/**
 * An event for a player clicking, inside or outside an inventory.
 */
public class ClickEvent {
    
    private final Player player;
    private final ClickType clickType;
    private final int hotbarButton;
    private boolean cancelled;
    
    /**
     * Creates a new {@link ClickEvent}.
     *
     * @param player       The {@link Player} who clicked.
     * @param clickType    The type of click.
     * @param hotbarButton The hotbar key that was pressed (in [0; 8]),
     *                     or -1 if {@link #getClickType()} is not {@link ClickType#NUMBER_KEY}.
     */
    public ClickEvent(Player player, ClickType clickType, int hotbarButton) {
        this.player = player;
        this.clickType = clickType;
        this.hotbarButton = hotbarButton;
    }
    
    /**
     * Creates a new {@link ClickEvent} from a {@link Click}.
     *
     * @param click The {@link Click} to create the event from.
     */
    public ClickEvent(Click click) {
        this(click.player(), click.clickType(), click.hotbarButton());
    }
    
    /**
     * Gets the {@link Player} who clicked.
     *
     * @return The {@link Player} who clicked.
     */
    public Player getPlayer() {
        return player;
    }
    
    /**
     * Gets the type of click.
     *
     * @return The type of click.
     */
    public ClickType getClickType() {
        return clickType;
    }
    
    /**
     * Gets the hotbar key that was pressed (in [0; 8]),
     * or -1 if {@link #getClickType()} is not {@link ClickType#NUMBER_KEY}.
     *
     * @return The hotbar key that was pressed.
     */
    public int getHotbarButton() {
        return hotbarButton;
    }
    
    /**
     * Gets whether the event is cancelled.
     *
     * @return Whether the event is cancelled.
     */
    public boolean isCancelled() {
        return cancelled;
    }
    
    /**
     * Changes the cancellation status of the event.
     *
     * @param cancelled Whether the event should be cancelled.
     */
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
    
}
