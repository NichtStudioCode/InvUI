package xyz.xenondevs.invui.inventory.event;

import xyz.xenondevs.invui.inventory.Inventory;

/**
 * A reason for an {@link Inventory} update.
 */
public interface UpdateReason {
    
    /**
     * An {@link UpdateReason} that suppresses all event calls.
     */
    UpdateReason SUPPRESSED = new UpdateReason() {};
    
}
