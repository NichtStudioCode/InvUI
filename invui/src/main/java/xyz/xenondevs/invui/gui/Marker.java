package xyz.xenondevs.invui.gui;

/**
 * Used to mark slots in a {@link Structure} as special slots.
 *
 * @see Markers
 */
public class Marker {
    
    private final boolean horizontal;
    
    Marker(boolean horizontal) {
        this.horizontal = horizontal;
    }
    
    boolean isHorizontal() {
        return horizontal;
    }
    
}
