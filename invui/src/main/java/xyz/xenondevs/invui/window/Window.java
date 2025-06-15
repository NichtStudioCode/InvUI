package xyz.xenondevs.invui.window;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent.Reason;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.ClickEvent;
import xyz.xenondevs.invui.gui.Gui;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A Window is the way to show a player a {@link Gui}. Windows can only have one viewer.
 * To create a new {@link Window}, use the builder factory methods {@link Window#builder} and {@link Window#mergedBuilder}.
 */
public sealed interface Window permits AbstractWindow, AnvilWindow, CartographyWindow, CrafterWindow, CraftingTableWindow, FurnaceWindow, MerchantWindow, StonecutterWindow {
    
    /**
     * Creates a new {@link Builder.Normal.Split Window Builder} for a normal split window.
     *
     * @return The new {@link Builder.Normal.Split Window Builder}.
     */
    static Builder.Normal.Split builder() {
        return new NormalSplitWindowImpl.BuilderImpl();
    }
    
    /**
     * Creates a new {@link Builder.Normal.Merged Window Builder} for a normal merged window.
     *
     * @return The new {@link Builder.Normal.Merged Window Builder}.
     */
    static Builder.Normal.Merged mergedBuilder() {
        return new NormalMergedWindowImpl.BuilderImpl();
    }
    
    /**
     * Gets an unmodifiable list of all {@link Gui Guis} in this {@link Window},
     * not including {@link Gui Guis} that are nested in other {@link Gui Guis}.
     * <p>
     * The list has the following order:
     * <ol>
     *     <li>upper {@link Gui}</li>
     *     <li>lower {@link Gui}</li>
     *     <li>special {@link Gui Gui(s)} (if present, such as the buttons gui in {@link StonecutterWindow}</li>
     * </ol>
     *
     * @return An unmodifiable collection of all {@link Gui Guis} in this {@link Window}.
     */
    @Unmodifiable List<Gui> getGuis();
    
    /**
     * Shows the window to the player.
     *
     * @throws IllegalStateException If the window is already open.
     */
    void open();
    
    /**
     * Whether the player is able to close the {@link Window}.
     *
     * @return If the player is able to close the {@link Window}.
     */
    boolean isCloseable();
    
    /**
     * Sets whether the {@link Window} is closable by the viewer.
     * Plugins can always close inventories.
     *
     * @param closeable If the {@link Window} is closeable.
     */
    void setCloseable(boolean closeable);
    
    /**
     * Closes the {@link Window} for the {@link #getViewer() viewer}, if they are
     * {@link #isOpen() currently viewing} the {@link Window}.
     */
    void close();
    
    /**
     * Gets if the viewer is currently viewing this {@link Window}.
     *
     * @return If the {@link Window} is currently open.
     */
    boolean isOpen();
    
    /**
     * Sets the {@link Supplier} used to retrieve the title of the {@link Window}.
     * Refreshes can be triggered via {@link #updateTitle()}.
     *
     * @param titleSupplier The new title supplier
     */
    void setTitleSupplier(Supplier<? extends Component> titleSupplier);
    
    /**
     * Changes the title of the {@link Window}.
     *
     * @param title The new title
     */
    void setTitle(Component title);
    
    /**
     * Changes the title of the {@link Window}.
     *
     * @param title The new title
     */
    void setTitle(String title);
    
    /**
     * Refreshes the title using the currently set {@link Supplier}.
     */
    void updateTitle();
    
    /**
     * Gets the viewer of this {@link Window}
     *
     * @return The viewer of this window.
     */
    Player getViewer();
    
    /**
     * Replaces the currently registered open handlers with the given list.
     *
     * @param openHandlers The new open handlers
     */
    void setOpenHandlers(@Nullable List<? extends Runnable> openHandlers);
    
    /**
     * Adds an open handler that will be called when this window gets opened.
     *
     * @param openHandler The close handler to add
     */
    void addOpenHandler(Runnable openHandler);
    
    /**
     * Replaces the currently registered close handlers with the given list.
     *
     * @param closeHandlers The new close handlers, receiving the {@link Reason} why the {@link Window} was closed
     */
    void setCloseHandlers(@Nullable List<? extends Consumer<? super Reason>> closeHandlers);
    
    /**
     * Adds a close handler that will be called when this window gets closed.
     *
     * @param closeHandler The close handler to add, receiving the {@link Reason} why the {@link Window} was closed
     */
    void addCloseHandler(Consumer<? super Reason> closeHandler);
    
    /**
     * Removes a close handler that has been added previously.
     *
     * @param closeHandler The close handler to remove
     */
    void removeCloseHandler(Consumer<? super Reason> closeHandler);
    
    /**
     * Replaces the currently registered outside click handlers with the given list.
     *
     * @param outsideClickHandlers The new outside click handlers
     */
    void setOutsideClickHandlers(@Nullable List<? extends Consumer<? super ClickEvent>> outsideClickHandlers);
    
    /**
     * Adds an outside click handler that will be called when a player clicks outside the inventory.
     *
     * @param outsideClickHandler The outside click handler to add
     */
    void addOutsideClickHandler(Consumer<? super ClickEvent> outsideClickHandler);
    
    /**
     * Removes an outside click handler that has been added previously.
     *
     * @param outsideClickHandler The outside click handler to remove
     */
    void removeOutsideClickHandler(Consumer<? super ClickEvent> outsideClickHandler);
    
    /**
     * Sets the fallback {@link Window} that is opened when this {@link Window} is closed
     * by the viewer pressing the close inventory key ({@code E} or {@code ESC} by default).
     *
     * @param fallbackWindow The fallback {@link Window}
     */
    default void setFallbackWindow(@Nullable Window fallbackWindow) {
        setFallbackWindow(() -> fallbackWindow);
    }
    
    /**
     * Sets a {@link Supplier} that provides the fallback {@link Window} that is opened when this {@link Window} is
     * closed by the viewer pressing the close inventory key ({@code E} or {@code ESC} by default).
     *
     * @param fallbackWindow The fallback {@link Window} supplier
     */
    void setFallbackWindow(Supplier<? extends @Nullable Window> fallbackWindow);
    
    /**
     * A {@link Window} builder.
     *
     * @param <W> The window type
     * @param <S> The builder type
     */
    sealed interface Builder<W extends Window, S extends Builder<W, S>>
        extends Cloneable
        permits AbstractWindow.AbstractBuilder, Builder.Split, Builder.Normal, Builder.Merged
    {
        
        /**
         * Sets the viewer of the {@link Window}.
         *
         * @param viewer The viewer of the {@link Window}
         * @return This {@link Builder Window Builder}
         */
        S setViewer(Player viewer);
        
        /**
         * Sets the {@link Supplier} used to retrieve the title of the {@link Window}.
         * Refreshes can be triggered via {@link #updateTitle()}.
         *
         * @param title The title supplier
         * @return This {@link Builder Window Builder}
         */
        S setTitleSupplier(Supplier<? extends Component> title);
        
        /**
         * Sets the title of the {@link Window}.
         *
         * @param title The title of the {@link Window}
         * @return This {@link Builder Window Builder}
         */
        S setTitle(Component title);
        
        /**
         * Sets the title of the {@link Window}.
         *
         * @param title The title of the {@link Window}
         * @return This {@link Builder Window Builder}
         */
        S setTitle(String title);
        
        /**
         * Configures whether the {@link Window} is closable by the viewer.
         * Plugins can always close inventories.
         *
         * @param closeable If the {@link Window} is closeable
         * @return This {@link Builder Window Builder}
         */
        S setCloseable(boolean closeable);
        
        /**
         * Sets the open handlers of the {@link Window}.
         *
         * @param openHandlers The open handlers of the {@link Window}
         * @return This {@link Builder Window Builder}
         */
        S setOpenHandlers(@Nullable List<? extends Runnable> openHandlers);
        
        /**
         * Adds an open handler to the {@link Window}.
         *
         * @param openHandler The open handler to add
         * @return This {@link Builder Window Builder}
         */
        S addOpenHandler(Runnable openHandler);
        
        /**
         * Sets the close handlers of the {@link Window}.
         *
         * @param closeHandlers The close handlers, receiving the {@link Reason} why the {@link Window} was closed
         * @return This {@link Builder Window Builder}
         */
        S setCloseHandlers(@Nullable List<? extends Consumer<? super Reason>> closeHandlers);
        
        /**
         * Adds a close handler to the {@link Window}.
         *
         * @param closeHandler The close handler, receiving the {@link Reason} why the {@link Window} was closed
         * @return This {@link Builder Window Builder}
         */
        S addCloseHandler(Consumer<? super Reason> closeHandler);
        
        /**
         * Sets the outside click handlers of the {@link Window}.
         *
         * @param outsideClickHandlers The outside click handlers of the {@link Window}
         * @return This {@link Builder Window Builder}
         */
        S setOutsideClickHandlers(@Nullable List<? extends Consumer<? super ClickEvent>> outsideClickHandlers);
        
        /**
         * Adds an outside click handler to the {@link Window}.
         *
         * @param outsideClickHandler The outside click handler to add
         * @return This {@link Builder Window Builder}
         */
        S addOutsideClickHandler(Consumer<? super ClickEvent> outsideClickHandler);
        
        /**
         * Sets the fallback {@link Window} that is opened when this {@link Window} is closed
         * by the viewer pressing the close inventory key ({@code E} or {@code ESC} by default).
         *
         * @param fallbackWindow The fallback {@link Window}
         * @return This {@link Builder Window Builder}
         */
        default S setFallbackWindow(@Nullable Window fallbackWindow) {
            return setFallbackWindow(() -> fallbackWindow);
        }
        
        /**
         * Sets a {@link Supplier} that provides the fallback {@link Window} that is opened when this {@link Window} is
         * closed by the viewer pressing the close inventory key ({@code E} or {@code ESC} by default).
         *
         * @param fallbackWindow The fallback {@link Window} supplier
         * @return This {@link Builder Window Builder}
         */
        S setFallbackWindow(Supplier<? extends @Nullable Window> fallbackWindow);
        
        /**
         * Sets the modifiers of the {@link Window}.
         *
         * @param modifiers The modifiers of the {@link Window}
         * @return This {@link Builder Window Builder}
         */
        S setModifiers(@Nullable List<? extends Consumer<? super W>> modifiers);
        
        /**
         * Adds a modifier to the {@link Window}.
         *
         * @param modifier The modifier to add
         * @return This {@link Builder Window Builder}
         */
        S addModifier(Consumer<? super W> modifier);
        
        /**
         * Builds the {@link Window}.
         *
         * @return The built {@link Window}
         */
        W build();
        
        /**
         * Builds the {@link Window} with the specified viewer.
         * If this method is used, the viewer does not need to be set using {@link #setViewer(Player)}.
         *
         * @param viewer The {@link Player} to build the {@link Window} for.
         * @return The built {@link Window}.
         */
        W build(Player viewer);
        
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
        S clone();
        
        /**
         * A single {@link Window} builder. Single Windows only have on {@link Gui}.
         *
         * @param <W> The window type
         * @param <S> The builder type
         */
        sealed interface Merged<W extends Window, S extends Merged<W, S>>
            extends Builder<W, S>
            permits AbstractMergedWindow.AbstractBuilder, Normal.Merged
        {
            
            /**
             * Sets the {@link Gui} of the {@link Window}.
             *
             * @param gui The {@link Gui} of the {@link Window}
             * @return This {@link Merged Window Builder}
             */
            S setGui(Gui gui);
            
            /**
             * Sets the {@link Gui.Builder} for this {@link Merged Window Builder}.
             * The {@link Gui.Builder} will be called every time a new {@link Window} is created using this builder.
             *
             * @param builder The {@link Gui.Builder} for this {@link Merged Window Builder}
             * @return This {@link Merged Window Builder}
             */
            S setGui(Gui.Builder<?, ?> builder);
            
            /**
             * Sets the {@link Gui} {@link Supplier} for this {@link Merged Window Builder}.
             * The {@link Supplier} will be called every time a new {@link Window} is created using this builder.
             *
             * @param guiSupplier The {@link Gui} {@link Supplier}
             * @return This {@link Merged Window Builder}
             */
            S setGui(Supplier<? extends Gui> guiSupplier);
            
        }
        
        /**
         * A double {@link Window} builder. Double Windows have two {@link Gui Guis}.
         *
         * @param <W> The window type
         * @param <S> The builder type
         */
        sealed interface Split<W extends Window, S extends Split<W, S>>
            extends Builder<W, S>
            permits AbstractSplitWindow.AbstractBuilder, AnvilWindow.Builder, CartographyWindow.Builder, CrafterWindow.Builder, CraftingTableWindow.Builder, FurnaceWindow.Builder, MerchantWindow.Builder, StonecutterWindow.Builder, Normal.Split
        {
            
            /**
             * Sets the lower {@link Gui} of the {@link Window}.
             *
             * @param gui The lower {@link Gui} of the {@link Window}
             * @return This {@link Split Window Builder}
             */
            default S setLowerGui(Gui gui) {
                return setLowerGui(() -> gui);
            }
            
            /**
             * Sets the {@link Gui.Builder} for the lower {@link Gui} of this {@link Split Window Builder}.
             * The {@link Gui.Builder} will be called every time a new {@link Window} is created using this builder.
             *
             * @param builder The {@link Gui.Builder} for the lower {@link Gui} of this {@link Split Window Builder}
             * @return This {@link Split Window Builder}
             */
            default S setLowerGui(Gui.Builder<?, ?> builder) {
                return setLowerGui(builder::build);
            }
            
            /**
             * Sets the {@link Gui} {@link Supplier} for the lower {@link Gui} of this {@link Split Window Builder}.
             * The {@link Supplier} will be called every time a new {@link Window} is created using this builder.
             *
             * @param guiSupplier The {@link Gui} {@link Supplier} for the lower {@link Gui} of this {@link Split Window Builder}
             * @return This {@link Split Window Builder}
             */
            S setLowerGui(Supplier<? extends Gui> guiSupplier);
            
        }
        
        /**
         * A normal {@link Window} builder for {@link Window Windows} of inventories with no special functionality, such
         * as chests, hoppers and droppers.
         *
         * @param <V> The viewer type
         * @param <S> The builder type
         */
        sealed interface Normal<V, S extends Normal<V, S>> extends Builder<Window, S> {
            
            /**
             * A normal split {@link Window} builder. Combines both {@link Builder.Split} and {@link Builder.Normal}
             * for a normal {@link Window} with two {@link Gui Guis}, where the lower {@link Gui} is used to fill the
             * {@link Player Player's} inventory.
             *
             * @see AnvilWindow.Builder.Split
             * @see CartographyWindow.Builder.Split
             */
            sealed interface Split extends Builder.Normal<Player, Split>, Builder.Split<Window, Split> permits NormalSplitWindowImpl.BuilderImpl {
                
                /**
                 * Sets the upper {@link Gui} of the {@link Window}.
                 *
                 * @param gui The upper {@link Gui} of the {@link Window}
                 * @return This {@link Normal.Split}
                 */
                default Normal.Split setUpperGui(Gui gui) {
                    return setUpperGui(() -> gui);
                }
                
                /**
                 * Sets the {@link Gui.Builder} for the upper {@link Gui} of this {@link Normal.Split}.
                 * The {@link Gui.Builder} will be called every time a new {@link Window} is created using this builder.
                 *
                 * @param builder The {@link Gui.Builder} for the upper {@link Gui}
                 * @return This {@link Normal.Split}
                 */
                default Normal.Split setUpperGui(Gui.Builder<?, ?> builder) {
                    return setUpperGui(builder::build);
                }
                
                /**
                 * Sets the {@link Gui} {@link Supplier} for the upper {@link Gui} of this {@link Normal.Split}.
                 * The {@link Supplier} will be called every time a new {@link Window} is created using this builder.
                 *
                 * @param guiSupplier The {@link Gui} {@link Supplier} for the upper {@link Gui}
                 * @return This {@link Normal.Split}
                 */
                Normal.Split setUpperGui(Supplier<? extends Gui> guiSupplier);
                
            }
            
            /**
             * A normal merged {@link Window} builder. Combines both {@link Builder.Merged} and {@link Builder.Normal}
             * for a normal {@link Window} with one {@link Gui}, which fills both the upper inventory and the
             * {@link Player Player's} inventory.
             */
            sealed interface Merged extends Builder.Normal<Player, Merged>, Builder.Merged<Window, Merged>
                permits NormalMergedWindowImpl.BuilderImpl {}
            
        }
        
    }
    
}
