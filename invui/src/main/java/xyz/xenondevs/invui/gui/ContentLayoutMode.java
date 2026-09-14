package xyz.xenondevs.invui.gui;

import org.jetbrains.annotations.ApiStatus;

/**
 * Determines how content is laid out in the content list slots of a {@link PagedGui},
 * {@link ScrollGui}, or {@link TabGui}.
 */
@ApiStatus.Experimental
public enum ContentLayoutMode {
    
    /**
     * Preserves the content's spatial layout.
     * Positions that do not correspond to a content list slot are not displayed.
     */
    SPATIAL,
    
    /**
     * Places content in consecutive content list slots,
     * ignoring the spatial positions of those slots.
     */
    SEQUENTIAL
    
}
