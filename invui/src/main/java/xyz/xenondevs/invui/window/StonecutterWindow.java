package xyz.xenondevs.invui.window;

import org.jetbrains.annotations.UnmodifiableView;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.state.MutableProperty;

import java.util.List;
import java.util.function.BiConsumer;
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
    static Builder builder() {
        return new StonecutterWindowImpl.BuilderImpl();
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
     * Gets the handlers that are called when the selected slot is changed by the player.
     *
     * @return The selected slot change handlers, each receiving the old and new selected slot.
     */
    @UnmodifiableView
    List<BiConsumer<Integer, Integer>> getSelectedSlotChangeHandlers();
    
    /**
     * Sets the handlers that are called when the selected slot is changed by the player.
     *
     * @param handlers The selected slot change handlers, each receiving the old and new selected slot.
     */
    void setSelectedSlotChangeHandlers(List<? extends BiConsumer<Integer, Integer>> handlers);
    
    /**
     * Adds a handler that is called when the selected slot is changed by the player.
     *
     * @param handler The selected slot change handler, receiving the old and new selected slot.
     */
    void addSelectedSlotChangeHandler(BiConsumer<? super Integer, ? super Integer> handler);
    
    /**
     * A {@link StonecutterWindow} builder.
     */
    sealed interface Builder extends Window.Builder.Split<StonecutterWindow, Builder> permits StonecutterWindowImpl.BuilderImpl {
        
        /**
         * Sets the upper {@link Gui} of the {@link StonecutterWindow}.
         *
         * @param gui The upper {@link Gui}
         * @return This {@link Builder}
         */
        default Builder setUpperGui(Gui gui) {
            return setUpperGui(() -> gui);
        }
        
        /**
         * Sets the {@link Gui.Builder} for the upper {@link Gui} of this {@link Builder}.
         * The {@link Gui.Builder} will be called every time a new {@link StonecutterWindow} is created using this builder.
         *
         * @param builder The {@link Gui.Builder} for the upper {@link Gui}
         * @return This {@link Builder}
         */
        default Builder setUpperGui(Gui.Builder<?, ?> builder) {
            return setUpperGui(builder::build);
        }
        
        /**
         * Sets the {@link Gui} {@link Supplier} for the upper {@link Gui} of this {@link Builder}.
         * The {@link Supplier} will be called every time a new {@link StonecutterWindow} is created using this builder.
         *
         * @param guiSupplier The {@link Gui} {@link Supplier} for the upper {@link Gui}
         * @return This {@link Builder}
         */
        Builder setUpperGui(Supplier<? extends Gui> guiSupplier);
        
        /**
         * Sets the 4xN buttons {@link Gui} of the {@link StonecutterWindow}, which will
         * be displayed via the recipe buttons.
         *
         * @param gui The buttons {@link Gui}
         * @return This {@link Builder}
         */
        default Builder setButtonsGui(Gui gui) {
            return setButtonsGui(() -> gui);
        }
        
        /**
         * Sets the 4xN buttons {@link Gui} {@link Supplier} of the {@link StonecutterWindow}, which will
         * be displayed via the recipe buttons.
         * The {@link Gui.Builder} will be called every time a new {@link StonecutterWindow} is created using this builder.
         *
         * @param builder The {@link Gui.Builder} for the buttons {@link Gui}
         * @return This {@link Builder}
         */
        default Builder setButtonsGui(Gui.Builder<?, ?> builder) {
            return setButtonsGui(builder::build);
        }
        
        /**
         * Sets the 4xN buttons {@link Gui} {@link Supplier} of the {@link StonecutterWindow}, which will
         * be displayed via the recipe buttons.
         * The {@link Supplier} will be called every time a new {@link StonecutterWindow} is created using this builder.
         *
         * @param guiSupplier The {@link Gui} {@link Supplier} for the buttons {@link Gui}
         * @return This {@link Builder}
         */
        Builder setButtonsGui(Supplier<? extends Gui> guiSupplier);
        
        /**
         * Sets the handlers that are called when the selected slot is changed by the player.
         *
         * @param handlers The selected slot change handlers, each receiving the old and new selected slot.
         * @return This {@link Builder}
         */
        Builder setSelectedSlotChangeHandlers(List<? extends BiConsumer<? super Integer, ? super Integer>> handlers);
        
        /**
         * Adds a handler that is called when the selected slot is changed by the player.
         *
         * @param handler The selected slot change handler, receiving the old and new selected slot.
         * @return This {@link Builder}
         */
        Builder addSelectedSlotChangeHandler(BiConsumer<? super Integer, ? super Integer> handler);
        
        /**
         * Sets the property containing the selected slot.
         *
         * @param slot The property containing the selected slot, or -1 if no slot is selected.
         * @return This {@link Builder}
         */
        Builder setSelectedSlot(MutableProperty<Integer> slot);
        
    }
    
}
