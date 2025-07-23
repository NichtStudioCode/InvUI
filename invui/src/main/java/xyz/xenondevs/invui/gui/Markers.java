package xyz.xenondevs.invui.gui;

import java.util.function.BiConsumer;

/**
 * Contains markers
 */
public class Markers {
    
    /**
     * The marker for horizontal content list slots in {@link PagedGui PagedGuis},
     * {@link ScrollGui ScrollGuis} and {@link TabGui TabGuis}
     */
    public static final Marker CONTENT_LIST_SLOT_HORIZONTAL = new Marker() {
        
        @Override
        void iterate(int width, int height, BiConsumer<? super Integer, ? super Integer> consumer) {
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    consumer.accept(x, y);
                }
            }
        }
        
    };
    
    /**
     * The marker for vertical content list slots in {@link PagedGui PagedGuis},
     * {@link ScrollGui ScrollGuis} and {@link TabGui TabGuis}
     */
    public static final Marker CONTENT_LIST_SLOT_VERTICAL = new Marker() {
        
        @Override
        void iterate(int width, int height, BiConsumer<? super Integer, ? super Integer> consumer) {
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    consumer.accept(x, y);
                }
            }
        }
        
    };
    
}
