package xyz.xenondevs.invui.window;

import xyz.xenondevs.invui.gui.Gui;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A {@link Window} that uses a stonecutter inventory.
 */
public sealed interface StonecutterWindow extends Window permits StonecutterWindowImpl {
    
    /**
     * Creates a new {@link Builder Window Builder} for a split {@link StonecutterWindow}.
     *
     * @return The new {@link Builder Window Builder}
     */
    static Builder split() {
        return new StonecutterWindowImpl.BuilderImpl();
    }
    
    /**
     * Creates a new split {@link StonecutterWindow} after configuring a
     * {@link Builder Window Builder} using the given {@link Consumer}.
     *
     * @param consumer The consumer to configure the {@link Builder Window Builder}
     * @return The created {@link StonecutterWindow}
     */
    static StonecutterWindow split(Consumer<Builder> consumer) {
        Builder builder = split();
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
     * @see Window.Builder.Normal
     * @see CartographyWindow.Builder
     */
    sealed interface Builder extends Window.Builder.Split<StonecutterWindow, Builder> permits StonecutterWindowImpl.BuilderImpl {
        
        /**
         * Sets the buttons {@link Gui} of the {@link StonecutterWindow}, which will
         * be displayed via the recipe buttons.
         *
         * @param gui The {@link Gui} of the {@link Window}
         * @return This {@link Merged Window Builder}
         */
        Builder setButtonsGui(Gui gui);
        
        /**
         * Sets the buttons {@link Gui} {@link Supplier} of the {@link StonecutterWindow}, which will
         * be displayed via the recipe buttons.
         * The {@link Gui.Builder} will be called every time a new {@link Window} is created using this builder.
         *
         * @param builder The {@link Gui.Builder} for this {@link Merged Window Builder}
         * @return This {@link Merged Window Builder}
         */
        Builder setButtonsGui(Gui.Builder<?, ?> builder);
        
        /**
         * Sets the buttons {@link Gui} {@link Supplier} of the {@link StonecutterWindow}, which will
         * be displayed via the recipe buttons.
         * The {@link Supplier} will be called every time a new {@link Window} is created using this builder.
         *
         * @param guiSupplier The {@link Gui} {@link Supplier}
         * @return This {@link Merged Window Builder}
         */
        Builder setButtonsGui(Supplier<Gui> guiSupplier);
        
        /**
         * Sets the handlers that are called when the selected slot is changed.
         *
         * @param handlers The selected slot change handlers, each receiving the old and new selected slot.
         * @return This {@link Merged Window Builder}
         */
        Builder setSelectedSlotChangeHandlers(List<BiConsumer<Integer, Integer>> handlers);
        
        /**
         * Adds a handler that is called when the selected slot is changed.
         *
         * @param handler The selected slot change handler, receiving the old and new selected slot.
         * @return This {@link Merged Window Builder}
         */
        Builder addSelectedSlotChangeHandler(BiConsumer<Integer, Integer> handler);
        
    }
    
}
