package xyz.xenondevs.invui.gui.structure;

/**
 * Used to mark slots in a {@link Structure} as special slots.
 * 
 * @see Markers
 */
public class Marker {
    
    private final boolean horizontal;
    
    public Marker(boolean horizontal) {
        this.horizontal = horizontal;
    }
    
    public boolean isHorizontal() {
        return horizontal;
    }
    
}
