package de.studiocode.invui.virtualinventory.event;

public interface UpdateReason {
    
    /**
     * An {@link UpdateReason} that suppresses all event calls.
     */
    UpdateReason SUPPRESSED = new UpdateReason() {};
    
}
