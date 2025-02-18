package xyz.xenondevs.invui.gui;

import java.util.function.BiConsumer;

/**
 * Used to mark slots in a {@link Structure} as special slots.
 *
 * @see Markers
 */
public abstract class Marker {
    
    Marker() {
    }
    
    abstract void iterate(int width, int height, BiConsumer<Integer, Integer> consumer);
    
}
