package xyz.xenondevs.invui.window;

import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.inventoryaccess.component.ComponentWrapper;
import xyz.xenondevs.invui.gui.Gui;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A Window is the way to show a player a {@link Gui}. Windows can only have one viewer.
 * To create a new {@link Window}, use the builder factory methods {@link Window#single},
 * {@link Window#split} and {@link Window#merged}.
 *
 * @see AnvilWindow
 * @see CartographyWindow
 */
public interface Window {
    
    /**
     * Creates a new {@link Builder.Normal.Single Window Builder} for a normal single window.
     *
     * @return The new {@link Builder.Normal.Single Window Builder}.
     */
    static @NotNull Builder.Normal.Single single() {
        return new NormalSingleWindowImpl.BuilderImpl();
    }
    
    /**
     * Creates a new normal single {@link Window} after configuring a {@link Builder.Normal.Split Window Builder} with the given {@link Consumer}.
     *
     * @param consumer The {@link Consumer} to configure the {@link Builder.Normal.Split Window Builder}.
     * @return The new {@link Window}.
     */
    static @NotNull Window single(@NotNull Consumer<Builder.Normal.@NotNull Single> consumer) {
        Builder.Normal.Single builder = single();
        consumer.accept(builder);
        return builder.build();
    }
    
    /**
     * Creates a new {@link Builder.Normal.Split Window Builder} for a normal split window.
     *
     * @return The new {@link Builder.Normal.Split Window Builder}.
     */
    static @NotNull Builder.Normal.Split split() {
        return new NormalSplitWindowImpl.BuilderImpl();
    }
    
    /**
     * Creates a new normal split {@link Window} after configuring a {@link Builder.Normal.Split Window Builder} with the given {@link Consumer}.
     *
     * @param consumer The {@link Consumer} to configure the {@link Builder.Normal.Split Window Builder}.
     * @return The new {@link Window}.
     */
    static @NotNull Window split(@NotNull Consumer<Builder.Normal.@NotNull Split> consumer) {
        Builder.Normal.Split builder = split();
        consumer.accept(builder);
        return builder.build();
    }
    
    /**
     * Creates a new {@link Builder.Normal.Merged Window Builder} for a normal merged window.
     *
     * @return The new {@link Builder.Normal.Merged Window Builder}.
     */
    static @NotNull Builder.Normal.Merged merged() {
        return new NormalMergedWindowImpl.BuilderImpl();
    }
    
    /**
     * Creates a new normal merged {@link Window} after configuring a {@link Builder.Normal.Merged Window Builder} with the given {@link Consumer}.
     *
     * @param consumer The {@link Consumer} to configure the {@link Builder.Normal.Merged Window Builder}.
     * @return The new {@link Window}.
     */
    static @NotNull Window merged(@NotNull Consumer<Builder.Normal.@NotNull Merged> consumer) {
        Builder.Normal.Merged builder = merged();
        consumer.accept(builder);
        return builder.build();
    }
    
    /**
     * Shows the window to the player.
     */
    void open();
    
    /**
     * Gets if the player is able to close the {@link Inventory}.
     *
     * @return If the player is able to close the {@link Inventory}.
     */
    boolean isCloseable();
    
    /**
     * Sets if the player should be able to close the {@link Inventory}.
     *
     * @param closeable If the player should be able to close the {@link Inventory}.
     */
    void setCloseable(boolean closeable);
    
    /**
     * Closes the underlying {@link Inventory} for its viewer.
     */
    void close();
    
    /**
     * Gets if the viewer is currently viewing this {@link Window}.
     *
     * @return If the {@link Window} is currently open.
     */
    boolean isOpen();
    
    /**
     * Changes the title of the {@link Inventory}.
     *
     * @param title The new title
     */
    void changeTitle(@NotNull ComponentWrapper title);
    
    /**
     * Changes the title of the {@link Inventory}.
     *
     * @param title The new title
     */
    void changeTitle(@NotNull BaseComponent[] title);
    
    /**
     * Changes the title of the {@link Inventory}.
     *
     * @param title The new title
     */
    void changeTitle(@NotNull String title);
    
    /**
     * Gets the viewer of this {@link Window}
     *
     * @return The viewer of this window.
     */
    @Nullable Player getViewer();
    
    /**
     * Gets the current {@link Player} that is viewing this
     * {@link Window} or null of there isn't one.
     *
     * @return The current viewer of this {@link Window} (can be null)
     */
    @Nullable Player getCurrentViewer();
    
    /**
     * Gets the viewer's {@link UUID}
     *
     * @return The viewer's {@link UUID}
     */
    @NotNull UUID getViewerUUID();
    
    /**
     * Replaces the currently registered open handlers with the given list.
     *
     * @param openHandlers The new open handlers
     */
    void setOpenHandlers(@Nullable List<@NotNull Runnable> openHandlers);
    
    /**
     * Adds an open handler that will be called when this window gets opened.
     *
     * @param openHandler The close handler to add
     */
    void addOpenHandler(@NotNull Runnable openHandler);
    
    /**
     * Replaces the currently registered close handlers with the given list.
     *
     * @param closeHandlers The new close handlers
     */
    void setCloseHandlers(@Nullable List<@NotNull Runnable> closeHandlers);
    
    /**
     * Adds a close handler that will be called when this window gets closed.
     *
     * @param closeHandler The close handler to add
     */
    void addCloseHandler(@NotNull Runnable closeHandler);
    
    /**
     * Removes a close handler that has been added previously.
     *
     * @param closeHandler The close handler to remove
     */
    void removeCloseHandler(@NotNull Runnable closeHandler);
    
    /**
     * Replaces the currently registered outside click handlers with the given list.
     *
     * @param outsideClickHandlers The new outside click handlers
     */
    void setOutsideClickHandlers(@Nullable List<@NotNull Consumer<@NotNull InventoryClickEvent>> outsideClickHandlers);
    
    /**
     * Adds an outside click handler that will be called when a player clicks outside the inventory.
     *
     * @param outsideClickHandlers The outside click handler to add
     */
    void addOutsideClickHandler(@NotNull Consumer<@NotNull InventoryClickEvent> outsideClickHandlers);
    
    /**
     * Removes an outside click handler that has been added previously.
     *
     * @param outsideClickHandlers The outside click handler to remove
     */
    void removeOutsideClickHandler(@NotNull Consumer<@NotNull InventoryClickEvent> outsideClickHandlers);
    
    /**
     * A {@link Window} builder.
     *
     * @param <W> The window type
     * @param <S> The builder type
     */
    interface Builder<W extends Window, S extends Builder<W, S>> extends Cloneable {
        
        /**
         * Sets the viewer of the {@link Window}.
         *
         * @param viewer The viewer of the {@link Window}
         * @return This {@link Builder Window Builder}
         */
        @Contract("_ -> this")
        @NotNull S setViewer(@NotNull Player viewer);
        
        /**
         * Sets the title of the {@link Window}.
         *
         * @param title The title of the {@link Window}
         * @return This {@link Builder Window Builder}
         */
        @Contract("_ -> this")
        @NotNull S setTitle(@NotNull ComponentWrapper title);
        
        /**
         * Sets the title of the {@link Window}.
         *
         * @param title The title of the {@link Window}
         * @return This {@link Builder Window Builder}
         */
        @Contract("_ -> this")
        @NotNull S setTitle(@NotNull BaseComponent @NotNull [] title);
        
        /**
         * Sets the title of the {@link Window}.
         *
         * @param title The title of the {@link Window}
         * @return This {@link Builder Window Builder}
         */
        @Contract("_ -> this")
        @NotNull S setTitle(@NotNull String title);
        
        /**
         * Configures if the {@link Window} is closeable.
         *
         * @param closeable If the {@link Window} is closeable
         * @return This {@link Builder Window Builder}
         */
        @Contract("_ -> this")
        @NotNull S setCloseable(boolean closeable);
        
        /**
         * Sets the open handlers of the {@link Window}.
         *
         * @param openHandlers The open handlers of the {@link Window}
         * @return This {@link Builder Window Builder}
         */
        @Contract("_ -> this")
        @NotNull S setOpenHandlers(@Nullable List<@NotNull Runnable> openHandlers);
        
        /**
         * Adds an open handler to the {@link Window}.
         *
         * @param openHandler The open handler to add
         * @return This {@link Builder Window Builder}
         */
        @Contract("_ -> this")
        @NotNull S addOpenHandler(@NotNull Runnable openHandler);
        
        /**
         * Sets the close handlers of the {@link Window}.
         *
         * @param closeHandlers The close handlers of the {@link Window}
         * @return This {@link Builder Window Builder}
         */
        @Contract("_ -> this")
        @NotNull S setCloseHandlers(@Nullable List<@NotNull Runnable> closeHandlers);
        
        /**
         * Adds a close handler to the {@link Window}.
         *
         * @param closeHandler The close handler to add
         * @return This {@link Builder Window Builder}
         */
        @Contract("_ -> this")
        @NotNull S addCloseHandler(@NotNull Runnable closeHandler);
        
        /**
         * Sets the outside click handlers of the {@link Window}.
         *
         * @param outsideClickHandlers The outside click handlers of the {@link Window}
         * @return This {@link Builder Window Builder}
         */
        @Contract("_ -> this")
        @NotNull S setOutsideClickHandlers(@NotNull List<@NotNull Consumer<@NotNull InventoryClickEvent>> outsideClickHandlers);
        
        /**
         * Adds an outside click handler to the {@link Window}.
         *
         * @param outsideClickHandler The outside click handler to add
         * @return This {@link Builder Window Builder}
         */
        @Contract("_ -> this")
        @NotNull S addOutsideClickHandler(@NotNull Consumer<@NotNull InventoryClickEvent> outsideClickHandler);
        
        /**
         * Sets the modifiers of the {@link Window}.
         *
         * @param modifiers The modifiers of the {@link Window}
         * @return This {@link Builder Window Builder}
         */
        @Contract("_ -> this")
        @NotNull S setModifiers(@Nullable List<@NotNull Consumer<@NotNull Window>> modifiers);
        
        /**
         * Adds a modifier to the {@link Window}.
         *
         * @param modifier The modifier to add
         * @return This {@link Builder Window Builder}
         */
        @Contract("_ -> this")
        @NotNull S addModifier(@NotNull Consumer<@NotNull Window> modifier);
        
        /**
         * Builds the {@link Window}.
         *
         * @return The built {@link Window}
         */
        @Contract("-> new")
        @NotNull W build();
        
        /**
         * Builds the {@link Window} with the specified viewer.
         * If this method is used, the viewer does not need to be set using {@link #setViewer(Player)}.
         *
         * @param viewer The {@link Player} to build the {@link Window} for.
         */
        @Contract("_ -> new")
        @NotNull W build(Player viewer);
        
        /**
         * Builds and shows the {@link Window} to the specified viewer.
         * If this method is used, the viewer does not need to be set using {@link #setViewer(Player)}.
         *
         * @param viewer The {@link Player} to show the {@link Window} to.
         */
        void open(Player viewer);
        
        /**
         * Clones the {@link Builder Window Builder}.
         *
         * @return The cloned {@link Builder Window Builder}
         */
        @Contract("-> new")
        @NotNull S clone();
        
        /**
         * A single {@link Window} builder. Single Windows only have on {@link Gui}.
         *
         * @param <W> The window type
         * @param <S> The builder type
         * @see Window.Builder.Normal.Single
         * @see Window.Builder.Normal.Merged
         * @see AnvilWindow.Builder.Single
         * @see CartographyWindow.Builder.Single
         */
        interface Single<W extends Window, S extends Single<W, S>> extends Builder<W, S> {
            
            /**
             * Sets the {@link Gui} of the {@link Window}.
             *
             * @param gui The {@link Gui} of the {@link Window}
             * @return This {@link Single Window Builder}
             */
            @Contract("_ -> this")
            @NotNull S setGui(@NotNull Gui gui);
            
            /**
             * Sets the {@link Gui.Builder} for this {@link Single Window Builder}.
             * The {@link Gui.Builder} will be called every time a new {@link Window} is created using this builder.
             *
             * @param builder The {@link Gui.Builder} for this {@link Single Window Builder}
             * @return This {@link Single Window Builder}
             */
            @Contract("_ -> this")
            @NotNull S setGui(@NotNull Gui.Builder<?, ?> builder);
            
            /**
             * Sets the {@link Gui} {@link Supplier} for this {@link Single Window Builder}.
             * The {@link Supplier} will be called every time a new {@link Window} is created using this builder.
             *
             * @param guiSupplier The {@link Gui} {@link Supplier}
             * @return This {@link Single Window Builder}
             */
            @Contract("_ -> this")
            @NotNull S setGui(@NotNull Supplier<Gui> guiSupplier);
            
        }
        
        /**
         * A double {@link Window} builder. Double Windows have two {@link Gui Guis}.
         *
         * @param <W> The window type
         * @param <S> The builder type
         * @see Window.Builder.Normal.Split
         * @see AnvilWindow.Builder.Split
         * @see CartographyWindow.Builder.Split
         */
        interface Double<W extends Window, S extends Builder.Double<W, S>> extends Builder<W, S> {
            
            /**
             * Sets the upper {@link Gui} of the {@link Window}.
             *
             * @param gui The upper {@link Gui} of the {@link Window}
             * @return This {@link Double Window Builder}
             */
            @Contract("_ -> this")
            @NotNull S setUpperGui(@NotNull Gui gui);
            
            /**
             * Sets the {@link Gui.Builder} for the upper {@link Gui} of this {@link Double Window Builder}.
             * The {@link Gui.Builder} will be called every time a new {@link Window} is created using this builder.
             *
             * @param builder The {@link Gui.Builder} for the upper {@link Gui} of this {@link Double Window Builder}
             * @return This {@link Double Window Builder}
             */
            @Contract("_ -> this")
            @NotNull S setUpperGui(@NotNull Gui.Builder<?, ?> builder);
            
            /**
             * Sets the {@link Gui} {@link Supplier} for the upper {@link Gui} of this {@link Double Window Builder}.
             * The {@link Supplier} will be called every time a new {@link Window} is created using this builder.
             *
             * @param guiSupplier The {@link Gui} {@link Supplier} for the upper {@link Gui} of this {@link Double Window Builder}
             * @return This {@link Double Window Builder}
             */
            @Contract("_ -> this")
            @NotNull S setUpperGui(@NotNull Supplier<Gui> guiSupplier);
            
            /**
             * Sets the lower {@link Gui} of the {@link Window}.
             *
             * @param gui The lower {@link Gui} of the {@link Window}
             * @return This {@link Double Window Builder}
             */
            @Contract("_ -> this")
            @NotNull S setLowerGui(@NotNull Gui gui);
            
            /**
             * Sets the {@link Gui.Builder} for the lower {@link Gui} of this {@link Double Window Builder}.
             * The {@link Gui.Builder} will be called every time a new {@link Window} is created using this builder.
             *
             * @param builder The {@link Gui.Builder} for the lower {@link Gui} of this {@link Double Window Builder}
             * @return This {@link Double Window Builder}
             */
            @Contract("_ -> this")
            @NotNull S setLowerGui(@NotNull Gui.Builder<?, ?> builder);
            
            /**
             * Sets the {@link Gui} {@link Supplier} for the lower {@link Gui} of this {@link Double Window Builder}.
             * The {@link Supplier} will be called every time a new {@link Window} is created using this builder.
             *
             * @param guiSupplier The {@link Gui} {@link Supplier} for the lower {@link Gui} of this {@link Double Window Builder}
             * @return This {@link Double Window Builder}
             */
            @Contract("_ -> this")
            @NotNull S setLowerGui(@NotNull Supplier<Gui> guiSupplier);
            
        }
        
        /**
         * A normal {@link Window} builder for {@link Window Windows} of inventories with no special functionality, such
         * as chests, hoppers and droppers.
         *
         * @param <S> The builder type
         * @see AnvilWindow.Builder
         * @see CartographyWindow.Builder
         */
        interface Normal<V, S extends Normal<V, S>> extends Builder<Window, S> {
            
            /**
             * A normal single {@link Window} builder. Combines both {@link Builder.Single} and {@link Builder.Normal}
             * for a normal {@link Window} with only one {@link Gui} that does not access the {@link Player Player's} inventory.
             *
             * @see AnvilWindow.Builder.Single
             * @see CartographyWindow.Builder.Single
             */
            interface Single extends Builder.Normal<UUID, Single>, Builder.Single<Window, Single> {}
            
            /**
             * A normal split {@link Window} builder. Combines both {@link Builder.Double} and {@link Builder.Normal}
             * for a normal {@link Window} with two {@link Gui Guis}, where the lower {@link Gui} is used to fill the
             * {@link Player Player's} inventory.
             *
             * @see AnvilWindow.Builder.Split
             * @see CartographyWindow.Builder.Split
             */
            interface Split extends Builder.Normal<Player, Split>, Builder.Double<Window, Split> {}
            
            /**
             * A normal merged {@link Window} builder. Combines both {@link Builder.Single} and {@link Builder.Normal}
             * for a normal {@link Window} with one {@link Gui}, which fills both the upper inventory and the
             * {@link Player Player's} inventory.
             */
            interface Merged extends Builder.Normal<Player, Merged>, Builder.Single<Window, Merged> {}
            
        }
        
    }
    
}
