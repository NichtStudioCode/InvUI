package xyz.xenondevs.invui.window;

import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.util.ColorPalette;
import xyz.xenondevs.invui.util.MapIcon;
import xyz.xenondevs.invui.util.MapPatch;

import java.awt.image.BufferedImage;
import java.util.List;
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
     * Updates the map in the cartography table.
     *
     * @param patch The {@link MapPatch} to apply to the map.
     * @param icons The {@link MapIcon MapIcons} to display on the map.
     */
    void updateMap(@Nullable MapPatch patch, @Nullable List<MapIcon> icons);
    
    /**
     * Updates the map in the cartography table.
     *
     * @param patch The {@link MapPatch} to apply to the map.
     */
    default void updateMap(@Nullable MapPatch patch) {
        updateMap(patch, null);
    }
    
    /**
     * Updates the map in the cartography table.
     *
     * @param icons The {@link MapIcon MapIcons} to display on the map.
     */
    default void updateMap(@Nullable List<MapIcon> icons) {
        updateMap(null, icons);
    }
    
    /**
     * Resets the map in the cartography table.
     */
    void resetMap();
    
    /**
     * A {@link CartographyWindow} builder.
     *
     * @see Window.Builder.Normal
     * @see Window.Builder
     */
    sealed interface Builder extends Window.Builder.Split<CartographyWindow, Builder> permits CartographyWindowImpl.BuilderImpl {
    }
    
}
