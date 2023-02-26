package xyz.xenondevs.invui.window;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.inventoryaccess.map.MapIcon;
import xyz.xenondevs.inventoryaccess.map.MapPatch;
import xyz.xenondevs.invui.gui.Gui;

import java.util.List;
import java.util.function.Consumer;

/**
 * A {@link Window} that uses a cartography table inventory.
 */
public interface CartographyWindow extends Window {
    
    /**
     * Creates a new {@link Builder.Single Window Builder} for a single {@link CartographyWindow}.
     *
     * @return The new {@link Builder.Single Window Builder}.
     */
    static @NotNull Builder.Single single() {
        return new CartographySingleWindowImpl.BuilderImpl();
    }
    
    /**
     * Creates a new single {@link CartographyWindow} after configuring a {@link Builder.Single Window Builder} using the given {@link Consumer}.
     *
     * @param consumer The {@link Consumer} to configure the {@link Builder.Single Window Builder}.
     * @return The created {@link CartographyWindow}.
     */
    static @NotNull CartographyWindow single(@NotNull Consumer<Builder.@NotNull Single> consumer) {
        Builder.Single builder = single();
        consumer.accept(builder);
        return builder.build();
    }
    
    /**
     * Creates a new {@link Builder.Split Window Builder} for a split {@link CartographyWindow}.
     *
     * @return The new {@link Builder.Split Window Builder}.
     */
    static @NotNull Builder.Split split() {
        return new CartographySplitWindowImpl.BuilderImpl();
    }
    
    /**
     * Creates a new split {@link CartographyWindow} after configuring a {@link Builder.Split Window Builder} using the given {@link Consumer}.
     *
     * @param consumer The {@link Consumer} to configure the {@link Builder.Split Window Builder}.
     * @return The created {@link CartographyWindow}.
     */
    static @NotNull CartographyWindow split(@NotNull Consumer<Builder.@NotNull Split> consumer) {
        Builder.Split builder = split();
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
     * @param <S> The builder type.
     * @see Window.Builder.Normal
     * @see Window.Builder
     */
    interface Builder<S extends Builder<S>> extends Window.Builder<CartographyWindow, Player, S> {
        
        /**
         * A single {@link CartographyWindow} builder. Combines both {@link CartographyWindow.Builder} an
         * {@link Window.Builder.Single} for a {@link CartographyWindow} with only one {@link Gui} that does not
         * access the {@link Player Player's} inventory.
         */
        interface Single extends Builder<Single>, Window.Builder.Single<CartographyWindow, Player, Single> {}
        
        /**
         * A split {@link CartographyWindow} builder. Combines both {@link CartographyWindow.Builder} an
         * {@link Window.Builder.Double} for a {@link CartographyWindow} with two {@link Gui Guis}, where the lower
         * {@link Gui} is used to fill the {@link Player Player's} inventory.
         */
        interface Split extends Builder<Split>, Window.Builder.Double<CartographyWindow, Player, Split> {}
        
    }
    
}
