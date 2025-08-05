package xyz.xenondevs.invui.window;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.UnmodifiableView;
import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.state.MutableProperty;
import xyz.xenondevs.invui.util.ColorPalette;

import java.awt.image.BufferedImage;
import java.util.Set;
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
     * Sets the {@link MapIcon MapIcons} of the map in the cartography table.
     *
     * @param icons The {@link MapIcon MapIcons} to set.
     */
    void setIcons(Set<? extends MapIcon> icons);
    
    /**
     * Gets the {@link MapIcon MapIcons} of the map in the cartography table.
     *
     * @return A set of {@link MapIcon MapIcons} currently on the map.
     */
    @UnmodifiableView
    Set<MapIcon> getIcons();
    
    /**
     * Resets the map in the cartography table, removing all {@link MapIcon MapIcons} and patches.
     */
    void resetMap();
    
    /**
     * Sets the view mode of the cartography table.
     *
     * @param view The {@link View} to set.
     */
    void setView(View view);
    
    /**
     * Gets the current view mode of the cartography table.
     *
     * @return The current {@link View} of the cartography table.
     */
    View getView();
    
    /**
     * An icon on a map.
     *
     * @param type      The type of icon
     * @param x         The x-coordinate of the icon, from 0 to 256, where 0 is the top left corner
     * @param y         The y-coordinate of the icon, from 0 to 256, where 0 is the top left corner
     * @param rot       The rotation of the icon, from 0 to 15 in 22.5° steps
     * @param component The text displayed under the icon
     */
    record MapIcon(
        MapIcon.Type type,
        int x, int y, int rot,
        @Nullable Component component
    )
    {
        
        /**
         * The map icon type.
         */
        public enum Type {
            /**
             * <img src="https://i.imgur.com/PzCWlhY.png" alt="image of white arrow"/>
             */
            WHITE_ARROW,
            /**
             * <img src="https://i.imgur.com/oPOvp5O.png" alt="image of green arrow"/>
             */
            GREEN_ARROW,
            /**
             * <img src="https://i.imgur.com/pD8EkLs.png" alt="image of red arrow"/>
             */
            RED_ARROW,
            /**
             * <img src="https://i.imgur.com/1Sd0xkw.png" alt="image of blue arrow"/>
             */
            BLUE_ARROW,
            /**
             * <img src="https://i.imgur.com/AYqFuZD.png" alt="image of white cross"/>
             */
            WHITE_CROSS,
            /**
             * <img src="https://i.imgur.com/we4oJeI.png" alt="image of red pointer"/>
             */
            RED_POINTER,
            /**
             * <img src="https://i.imgur.com/M7Zk2Vw.png" alt="image of white circle"/>
             */
            WHITE_CIRCLE,
            /**
             * <img src="https://i.imgur.com/r8bNePl.png" alt="image of small white circle"/>
             */
            SMALL_WHITE_CIRCLE,
            /**
             * <img src="https://i.imgur.com/3gSxXAA.png" alt="image of mansion"/>
             */
            MANSION,
            /**
             * <img src="https://i.imgur.com/1YsDNS1.png" alt="image of temple"/>
             */
            TEMPLE,
            /**
             * <img src="https://i.imgur.com/DQBgkm2.png" alt="image of white banner"/>
             */
            WHITE_BANNER,
            /**
             * <img src="https://i.imgur.com/6toX8W7.png" alt="image of orange banner"/>
             */
            ORANGE_BANNER,
            /**
             * <img src="https://i.imgur.com/AKZtCrr.png" alt="image of magenta banner"/>
             */
            MAGENTA_BANNER,
            /**
             * <img src="https://i.imgur.com/kfpiTv2.png" alt="image of light blue banner"/>
             */
            LIGHT_BLUE_BANNER,
            /**
             * <img src="https://i.imgur.com/v1QktUa.png" alt="image of yellow banner"/>
             */
            YELLOW_BANNER,
            /**
             * <img src="https://i.imgur.com/fPkRIw1.png" alt="image of lime banner"/>
             */
            LIME_BANNER,
            /**
             * <img src="https://i.imgur.com/Job7ICS.png" alt="image of pink banner"/>
             */
            PINK_BANNER,
            /**
             * <img src="https://i.imgur.com/fK0XlZE.png" alt="image of gray banner"/>
             */
            GRAY_BANNER,
            /**
             * <img src="https://i.imgur.com/Vwcaoqo.png" alt="image of light gray banner"/>
             */
            LIGHT_GRAY_BANNER,
            /**
             * <img src="https://i.imgur.com/NZ1Qcf1.png" alt="image of cyan banner"/>
             */
            CYAN_BANNER,
            /**
             * <img src="https://i.imgur.com/5XpJ5ao.png" alt="image of purple banner"/>
             */
            PURPLE_BANNER,
            /**
             * <img src="https://i.imgur.com/kihvjrG.png" alt="image of blue banner"/>
             */
            BLUE_BANNER,
            /**
             * <img src="https://i.imgur.com/M9PE7hL.png" alt="image of brown banner"/>
             */
            BROWN_BANNER,
            /**
             * <img src="https://i.imgur.com/8URfvnG.png" alt="image of green banner"/>
             */
            GREEN_BANNER,
            /**
             * <img src="https://i.imgur.com/yMR3wLB.png" alt="image of red banner"/>
             */
            RED_BANNER,
            /**
             * <img src="https://i.imgur.com/jW07hij.png" alt="image of black banner"/>
             */
            BLACK_BANNER,
            /**
             * <img src="https://i.imgur.com/GmWo4uJ.png" alt="image of red X"/>
             */
            RED_CROSS
        }
        
    }
    
    /**
     * A color section of a map.
     *
     * @param startX The x-coordinate of the top-left corner of the patch.
     * @param startY The y-coordinate of the top-left corner of the patch.
     * @param width  The width of the patch.
     * @param height The height of the patch.
     * @param colors The colors of the patch.
     * @see ColorPalette
     */
    record MapPatch(int startX, int startY, int width, int height, byte[] colors) {
        
        /**
         * Creates a new {@link MapPatch} with the given parameters.
         *
         * @param startX The x-coordinate of the top-left corner of the patch.
         * @param startY The y-coordinate of the top-left corner of the patch.
         * @param width  The width of the patch.
         * @param height The height of the patch.
         * @param colors The colors of the patch.
         */
        public MapPatch {
            if (colors.length != width * height)
                throw new IllegalArgumentException("Invalid colors array length, expected " + width * height + " but got " + colors.length);
        }
        
    }
    
    /**
     * The view mode of the cartography table.
     */
    enum View {
        
        /**
         * Normal view, showing the map at normal size.
         */
        NORMAL,
        
        /**
         * Small view, showing the map at half size with empty space around it.
         * This view can be triggered in vanilla by placing paper in the bottom input slot.
         */
        SMALL,
        
        /**
         * Duplication view, showing the map twice.
         * This view can be triggered in vanilla by placing an empty map in the bottom input slot.
         */
        DUPLICATE,
        
        /**
         * Lock view, showing the map at normal size but with a lock icon in the lower right corner.
         * This view can be triggered in vanilla by placing a locked map in the bottom input slot.
         */
        LOCK
        
    }
    
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
        Builder setInputGui(Supplier<? extends Gui> guiSupplier);
        
        /**
         * Sets the 1x1 result {@link Gui} of the {@link CartographyWindow}.
         *
         * @param gui The 1x1 result {@link Gui}
         * @return This {@link Builder}
         */
        default Builder setResultGui(Gui gui) {
            return setResultGui(() -> gui);
        }
        
        /**
         * Sets the {@link Gui.Builder} for the 1x1 result {@link Gui} of this {@link Builder}.
         * The {@link Gui.Builder} will be called every time a new {@link CartographyWindow} is created using this builder.
         *
         * @param builder The {@link Gui.Builder} for the 1x1 result {@link Gui}
         * @return This {@link Builder}
         */
        default Builder setResultGui(Gui.Builder<?, ?> builder) {
            return setResultGui(builder::build);
        }
        
        /**
         * Sets the {@link Gui} {@link Supplier} for the 1x1 result {@link Gui} of this {@link Builder}.
         * The {@link Supplier} will be called every time a new {@link CartographyWindow} is created using this builder.
         *
         * @param guiSupplier The {@link Gui} {@link Supplier} for the 1x1 result {@link Gui}
         * @return This {@link Builder}
         */
        Builder setResultGui(Supplier<? extends Gui> guiSupplier);
        
        /**
         * Sets the {@link MapIcon MapIcons} of the map.
         *
         * @param icons The {@link MapIcon MapIcons} to set.
         * @return This {@link Builder}
         */
        default Builder setIcons(Set<? extends MapIcon> icons) {
            return setIcons(MutableProperty.of(icons));
        }
        
        /**
         * Sets the property containing the {@link MapIcon MapIcons} of the map.
         *
         * @param icons The property containing the {@link MapIcon MapIcons} to set.
         * @return This {@link Builder}
         */
        Builder setIcons(MutableProperty<Set<? extends MapIcon>> icons);
        
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
        
        /**
         * Sets the view of the cartography table.
         *
         * @param view The view.
         * @return This {@link Builder}
         */
        default Builder setView(View view) {
            return setView(MutableProperty.of(view));
        }
        
        /**
         * Sets the property containing the view of the cartography table.
         *
         * @param view The view property.
         * @return This {@link Builder}
         */
        Builder setView(MutableProperty<View> view);
        
    }
    
}
