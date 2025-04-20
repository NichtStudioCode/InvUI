package xyz.xenondevs.invui.window;

import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.util.ColorPalette;
import xyz.xenondevs.invui.util.MapIcon;
import xyz.xenondevs.invui.util.MapPatch;

import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.function.Supplier;

/**
 * A {@link Window} that uses a cartography table inventory.
 */
public sealed interface CartographyWindow extends Window permits CartographyWindowImpl {
    
    /**
     * Creates a new {@link Builder Window Builder} for a split {@link CartographyWindow}.
     *
     * @return The new {@link Builder Window Builder}.
     */
    static Builder builder() {
        return new CartographyWindowImpl.BuilderImpl();
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
         * Sets the 1x2 input {@link Gui} (map and paper) of the {@link CartographyWindow}.
         *
         * @param gui The 1x2 input {@link Gui} (map and paper) of the {@link CartographyWindow}
         * @return This {@link Builder}
         */
        default Builder setInputGui(Gui gui) {
            return setInputGui(() -> gui);
        }
        
        /**
         * Sets the {@link Gui.Builder} for the 1x2 input {@link Gui} (map and paper) of this {@link Builder}.
         * The {@link Gui.Builder} will be called every time a new {@link CartographyWindow} is created using this {@link Builder}.
         *
         * @param builder The {@link Gui.Builder} for the 1x2 input {@link Gui} (map and paper)
         * @return This {@link Builder}
         */
        default Builder setInputGui(Gui.Builder<?, ?> builder) {
            return setInputGui(builder::build);
        }
        
        /**
         * Sets the {@link Gui} {@link Supplier} for the 1x2 input {@link Gui} (map and paper) of this {@link Builder}.
         * The {@link Supplier} will be called every time a new {@link CartographyWindow} is created using this builder.
         *
         * @param guiSupplier The {@link Gui} {@link Supplier} for the 1x2 input {@link Gui} (map and paper)
         * @return This {@link Builder}
         */
        Builder setInputGui(Supplier<Gui> guiSupplier);
        
        /**
         * Sets the 1x1 output {@link Gui} of the {@link CartographyWindow}.
         *
         * @param gui The 1x1 output {@link Gui}
         * @return This {@link Builder}
         */
        default Builder setOutputGui(Gui gui) {
            return setOutputGui(() -> gui);
        }
        
        /**
         * Sets the {@link Gui.Builder} for the 1x1 output {@link Gui} of this {@link Builder}.
         * The {@link Gui.Builder} will be called every time a new {@link CartographyWindow} is created using this builder.
         *
         * @param builder The {@link Gui.Builder} for the 1x1 output {@link Gui}
         * @return This {@link Builder}
         */
        default Builder setOutputGui(Gui.Builder<?, ?> builder) {
            return setOutputGui(builder::build);
        }
        
        /**
         * Sets the {@link Gui} {@link Supplier} for the 1x1 output {@link Gui} of this {@link Builder}.
         * The {@link Supplier} will be called every time a new {@link CartographyWindow} is created using this builder.
         *
         * @param guiSupplier The {@link Gui} {@link Supplier} for the 1x1 output {@link Gui}
         * @return This {@link Builder}
         */
        Builder setOutputGui(Supplier<Gui> guiSupplier);
        
        /**
         * Adds a {@link MapIcon} to the map.
         *
         * @param icon The {@link MapIcon} to add.
         * @return This {@link Builder}
         */
        Builder addIcon(MapIcon icon);
        
        /**
         * Sets the {@link MapIcon MapIcons} of the map.
         *
         * @param icons The {@link MapIcon MapIcons} to set.
         * @return This {@link Builder}
         */
        Builder setIcons(Collection<? extends MapIcon> icons);
        
        /**
         * Sets the map colors of the map.
         *
         * @param colors The map colors to set.
         * @return This {@link Builder}
         * @throws IllegalArgumentException If colors length is not 128x128
         */
        Builder setMap(byte[] colors);
        
        /**
         * Sets the map colors of the map.
         *
         * @param image The image to read the colors from
         * @return This {@link Builder}
         * @throws IllegalArgumentException If the image dimensions are not 128x128
         */
        Builder setMap(BufferedImage image);
        
    }
    
}
