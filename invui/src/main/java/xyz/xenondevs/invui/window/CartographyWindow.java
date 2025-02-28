package xyz.xenondevs.invui.window;

import xyz.xenondevs.invui.util.ColorPalette;
import xyz.xenondevs.invui.util.MapIcon;
import xyz.xenondevs.invui.util.MapPatch;

import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.function.Consumer;

/**
 * A {@link Window} that uses a cartography table inventory.
 */
public sealed interface CartographyWindow extends Window permits CartographyWindowImpl {
    
    /**
     * Creates a new {@link Builder Window Builder} for a split {@link CartographyWindow}.
     *
     * @return The new {@link Builder Window Builder}.
     */
    static Builder split() {
        return new CartographyWindowImpl.BuilderImpl();
    }
    
    /**
     * Creates a new split {@link CartographyWindow} after configuring a {@link Builder Window Builder} using the given {@link Consumer}.
     *
     * @param consumer The {@link Consumer} to configure the {@link Builder Window Builder}.
     * @return The created {@link CartographyWindow}.
     */
    static CartographyWindow split(Consumer<Builder> consumer) {
        Builder builder = split();
        consumer.accept(builder);
        return builder.build();
    }
    
    /**
     * Updates the map in the cartography table by drawing the image (max 128x128) starting at the given coordinates.
     *
     * @param x     The x-coordinate to start drawing the image.
     * @param y     The y-coordinate to start drawing the image.
     * @param image The image to draw. Cannot exceed 128x128 pixels. Any x or y offset limits this further.
     */
    default void applyPatch(int x, int y, BufferedImage image) {
        applyPatch(new MapPatch(x, y, image.getWidth(), image.getHeight(), ColorPalette.convertImage(image)));
    }
    
    /**
     * Updates the map in the cartography table by applying the given {@link MapPatch}.
     *
     * @param patch The {@link MapPatch} to apply.
     */
    void applyPatch(MapPatch patch);
    
    /**
     * Adds a {@link MapIcon} to the map in the cartography table.
     *
     * @param icon The {@link MapIcon} to add.
     */
    void addIcon(MapIcon icon);
    
    /**
     * Removes a {@link MapIcon} from the map in the cartography table.
     *
     * @param icon The {@link MapIcon} to remove.
     */
    void removeIcon(MapIcon icon);
    
    /**
     * Sets the {@link MapIcon MapIcons} of the map in the cartography table.
     *
     * @param icons The {@link MapIcon MapIcons} to set.
     */
    void setIcons(Collection<? extends MapIcon> icons);
    
    /**
     * Resets the map in the cartography table, removing all {@link MapIcon MapIcons} and patches.
     */
    void resetMap();
    
    /**
     * A {@link CartographyWindow} builder.
     *
     * @see Window.Builder.Normal
     * @see Window.Builder
     */
    sealed interface Builder extends Window.Builder.Split<CartographyWindow, Builder> permits CartographyWindowImpl.BuilderImpl {
        
        /**
         * Adds a {@link MapIcon} to the map.
         *
         * @param icon The {@link MapIcon} to add.
         * @return The {@link Builder} instance.
         */
        Builder addIcon(MapIcon icon);
        
        /**
         * Sets the {@link MapIcon MapIcons} of the map.
         *
         * @param icons The {@link MapIcon MapIcons} to set.
         * @return The {@link Builder} instance.
         */
        Builder setIcons(Collection<? extends MapIcon> icons);
        
        /**
         * Sets the map colors of the map.
         *
         * @param colors The map colors to set.
         * @return The {@link Builder} instance.
         * @throws IllegalArgumentException If colors length is not 128x128
         */
        Builder setMap(byte[] colors);
        
        /**
         * Sets the map colors of the map.
         *
         * @param image The image to read the colors from
         * @return The {@link Builder} instance.
         * @throws IllegalArgumentException If the image dimensions are not 128x128
         */
        Builder setMap(BufferedImage image);
        
    }
    
}
