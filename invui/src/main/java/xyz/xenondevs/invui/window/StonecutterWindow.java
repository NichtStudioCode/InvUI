package xyz.xenondevs.invui.window;

import org.bukkit.entity.Player;
import xyz.xenondevs.invui.gui.Gui;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A {@link Window} that uses a stonecutter inventory.
 */
public sealed interface StonecutterWindow
    extends Window
    permits StonecutterSingleWindowImpl, StonecutterSplitWindowImpl
{
    
    /**
     * Creates a new {@link Builder.Single Window Builder} for a single {@link AnvilWindow}.
     *
     * @return The new {@link Builder.Single Window Builder}
     */
    static Builder.Single single() {
        return new StonecutterSingleWindowImpl.BuilderImpl();
    }
    
    /**
     * Creates a new single {@link AnvilWindow} after configuring a
     * {@link Builder.Single Window Builder} using the given {@link Consumer}.
     *
     * @param consumer The consumer to configure the {@link Builder.Single Window Builder}
     * @return The created {@link AnvilWindow}
     */
    static StonecutterWindow single(Consumer<Builder.Single> consumer) {
        Builder.Single builder = single();
        consumer.accept(builder);
        return builder.build();
    }
    
    /**
     * Creates a new {@link Builder.Split Window Builder} for a split {@link AnvilWindow}.
     *
     * @return The new {@link Builder.Split Window Builder}
     */
    static Builder.Split split() {
        return new StonecutterSplitWindowImpl.BuilderImpl();
    }
    
    /**
     * Creates a new split {@link AnvilWindow} after configuring a
     * {@link Builder.Split Window Builder} using the given {@link Consumer}.
     *
     * @param consumer The consumer to configure the {@link Builder.Split Window Builder}
     * @return The created {@link AnvilWindow}
     */
    static StonecutterWindow split(Consumer<Builder.Split> consumer) {
        Builder.Split builder = split();
        consumer.accept(builder);
        return builder.build();
    }
    
    /**
     * Gets the currently selected slot.
     *
     * @return The selected slot, or -1 if no slot is selected.
     */
    int getSelectedSlot();
    
    /**
     * Sets the selected slot to the given value.
     *
     * @param i The slot to select, or -1 to deselect.
     */
    void setSelectedSlot(int i);
    
    /**
     * Gets the handlers that are called when the selected slot is changed.
     *
     * @return The selected slot change handlers, each receiving the old and new selected slot.
     */
    List<? extends BiConsumer<Integer, Integer>> getSelectedSlotChangeHandlers();
    
    /**
     * Sets the handlers that are called when the selected slot is changed.
     *
     * @param handlers The selected slot change handlers, each receiving the old and new selected slot.
     */
    void setSelectedSlotChangeHandlers(List<BiConsumer<Integer, Integer>> handlers);
    
    /**
     * Adds a handler that is called when the selected slot is changed.
     *
     * @param handler The selected slot change handler, receiving the old and new selected slot.
     */
    void addSelectedSlotChangeHandler(BiConsumer<Integer, Integer> handler);
    
    /**
     * An {@link StonecutterWindow} builder.
     *
     * @param <S> The builder type.
     * @see Window.Builder.Normal
     * @see CartographyWindow.Builder
     */
    sealed interface Builder<S extends Builder<S>> extends Window.Builder<StonecutterWindow, S> {
        
        /**
         * Sets the buttons {@link Gui} of the {@link StonecutterWindow}, which will
         * be displayed via the recipe buttons.
         *
         * @param gui The {@link Gui} of the {@link Window}
         * @return This {@link Window.Builder.Single Window Builder}
         */
        S setButtonsGui(Gui gui);
        
        /**
         * Sets the buttons {@link Gui} {@link Supplier} of the {@link StonecutterWindow}, which will
         * be displayed via the recipe buttons.
         * The {@link Gui.Builder} will be called every time a new {@link Window} is created using this builder.
         *
         * @param builder The {@link Gui.Builder} for this {@link Window.Builder.Single Window Builder}
         * @return This {@link Window.Builder.Single Window Builder}
         */
        S setButtonsGui(Gui.Builder<?, ?> builder);
        
        /**
         * Sets the buttons {@link Gui} {@link Supplier} of the {@link StonecutterWindow}, which will
         * be displayed via the recipe buttons.
         * The {@link Supplier} will be called every time a new {@link Window} is created using this builder.
         *
         * @param guiSupplier The {@link Gui} {@link Supplier}
         * @return This {@link Window.Builder.Single Window Builder}
         */
        S setButtonsGui(Supplier<Gui> guiSupplier);
        
        /**
         * Sets the handlers that are called when the selected slot is changed.
         *
         * @param handlers The selected slot change handlers, each receiving the old and new selected slot.
         * @return This {@link Window.Builder.Single Window Builder}
         */
        S setSelectedSlotChangeHandlers(List<BiConsumer<Integer, Integer>> handlers);
        
        /**
         * Adds a handler that is called when the selected slot is changed.
         *
         * @param handler The selected slot change handler, receiving the old and new selected slot.
         * @return This {@link Window.Builder.Single Window Builder}
         */
        S addSelectedSlotChangeHandler(BiConsumer<Integer, Integer> handler);
        
        /**
         * A single {@link StonecutterWindow} builder. Combines both {@link StonecutterWindow.Builder} and {@link Window.Builder.Single}
         * for an {@link StonecutterWindow} with only one {@link Gui} that does not access the {@link Player Player's} inventory.
         *
         * @see Window.Builder.Normal.Single
         * @see CartographyWindow.Builder.Single
         */
        sealed interface Single extends Builder<Single>, Window.Builder.Single<StonecutterWindow, Single>
            permits StonecutterSingleWindowImpl.BuilderImpl {}
        
        /**
         * A split {@link StonecutterWindow} builder. Combines both {@link StonecutterWindow.Builder} and {@link Window.Builder.Double}
         * for an {@link StonecutterWindow} with two {@link Gui Guis}, where the lower {@link Gui} is used to fill the
         * {@link Player Player's} inventory.
         *
         * @see Window.Builder.Normal.Split
         * @see CartographyWindow.Builder.Split
         */
        sealed interface Split extends Builder<Split>, Window.Builder.Double<StonecutterWindow, Split>
            permits StonecutterSplitWindowImpl.BuilderImpl {}
        
    }
    
}
