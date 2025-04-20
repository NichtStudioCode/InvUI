package xyz.xenondevs.invui.window;

import org.jetbrains.annotations.UnmodifiableView;
import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.Slot;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * A {@link Window} that uses a crafter inventory.
 */
public sealed interface CrafterWindow extends Window permits CrafterWindowImpl {
    
    /**
     * Creates a new {@link Builder} for a split {@link CrafterWindow}.
     *
     * @return The new {@link Builder}
     */
    static Builder builder() {
        return new CrafterWindowImpl.BuilderImpl();
    }
    
    /**
     * Sets the disabled state of the specified crafting slot.
     *
     * @param slot     The slot to set the disabled state of
     * @param disabled Whether the slot should be disabled
     */
    void setSlotDisabled(int slot, boolean disabled);
    
    /**
     * Sets the disabled state of the specified crafting slot.
     *
     * @param x        The x coordinate of the slot
     * @param y        The y coordinate of the slot
     * @param disabled Whether the slot should be disabled
     */
    default void setSlotDisabled(int x, int y, boolean disabled) {
        setSlotDisabled(y * 3 + x, disabled);
    }
    
    /**
     * Sets the disabled state of the specified crafting slot.
     *
     * @param slot     The slot to set the disabled state of
     * @param disabled Whether the slot should be disabled
     */
    default void setSlotDisabled(Slot slot, boolean disabled) {
        setSlotDisabled(slot.x(), slot.y(), disabled);
    }
    
    /**
     * Gets the disabled state of the specified crafting slot.
     *
     * @param slot The slot to get the disabled state of
     * @return Whether the slot is disabled
     */
    boolean isSlotDisabled(int slot);
    
    /**
     * Gets the disabled state of the specified crafting slot.
     *
     * @param x The x coordinate of the slot
     * @param y The y coordinate of the slot
     * @return Whether the slot is disabled
     */
    default boolean isSlotDisabled(int x, int y) {
        return isSlotDisabled(y * 3 + x);
    }
    
    /**
     * Gets the disabled state of the specified crafting slot.
     *
     * @param slot The slot to get the disabled state of
     * @return Whether the slot is disabled
     */
    default boolean isSlotDisabled(Slot slot) {
        return isSlotDisabled(slot.x(), slot.y());
    }
    
    /**
     * Gets the handlers that are called when the viewer enables/disables a slot.
     *
     * @return The slot toggle handlers, each receiving the slot and the new state
     */
    @UnmodifiableView
    List<BiConsumer<? super Integer, ? super Boolean>> getSlotToggleHandlers();
    
    /**
     * Sets the handlers that are called when the viewer enables/disables a slot.
     *
     * @param handlers The slot toggle handlers, each receiving the slot and the new state
     */
    void setSlotToggleHandlers(@Nullable List<? extends BiConsumer<? super Integer, ? super Boolean>> handlers);
    
    /**
     * Adds a handler that is called when the viewer enables/disables a slot.
     *
     * @param handler The slot toggle handler, receiving the slot and the new state
     */
    void addSlotToggleHandler(BiConsumer<? super Integer, ? super Boolean> handler);
    
    /**
     * Removes a previously registered slot toggle handler.
     *
     * @param handler The slot toggle handler to remove
     */
    void removeSlotToggleHandler(BiConsumer<? super Integer, ? super Boolean> handler);
    
    /**
     * A {@link CrafterWindow} builder.
     */
    sealed interface Builder extends Window.Builder.Split<CrafterWindow, Builder> permits CrafterWindowImpl.BuilderImpl {
        
        /**
         * Sets the 3x3 crafting input {@link Gui} of the {@link CrafterWindow}.
         *
         * @param gui The 3x3 crafting input {@link Gui}
         * @return This {@link Builder}
         */
        default Builder setCraftingGui(Gui gui) {
            return setCraftingGui(() -> gui);
        }
        
        /**
         * Sets the {@link Gui.Builder} for the 3x3 crafting input {@link Gui} of this {@link Builder}.
         * The {@link Gui.Builder} will be called every time a new {@link CrafterWindow} is created using this builder.
         *
         * @param builder The {@link Gui.Builder} for the 3x3 crafting input {@link Gui}
         * @return This {@link Builder}
         */
        default Builder setCraftingGui(Gui.Builder<?, ?> builder) {
            return setCraftingGui(builder::build);
        }
        
        /**
         * Sets the {@link Gui} {@link Supplier} for the 3x3 crafting input {@link Gui} of this {@link Builder}.
         * The {@link Supplier} will be called every time a new {@link CrafterWindow} is created using this builder.
         *
         * @param guiSupplier The {@link Gui} {@link Supplier} for the 3x3 crafting input {@link Gui}
         * @return This {@link Builder}
         */
        Builder setCraftingGui(Supplier<? extends Gui> guiSupplier);
        
        /**
         * Sets the result {@link Gui} of the {@link CrafterWindow}.
         *
         * @param gui The result input {@link Gui} of the {@link CrafterWindow}
         * @return This {@link Builder}
         */
        default Builder setResultGui(Gui gui) {
            return setResultGui(() -> gui);
        }
        
        /**
         * Sets the {@link Gui.Builder} for the result input {@link Gui} of this {@link Builder}.
         * The {@link Gui.Builder} will be called every time a new {@link CrafterWindow} is created using this builder.
         *
         * @param builder The {@link Gui.Builder} for the result input {@link Gui}
         * @return This {@link Builder}
         */
        default Builder setResultGui(Gui.Builder<?, ?> builder) {
            return setResultGui(builder::build);
        }
        
        /**
         * Sets the {@link Gui} {@link Supplier} for the result input {@link Gui} of this {@link Builder}.
         * The {@link Supplier} will be called every time a new {@link CrafterWindow} is created using this builder.
         *
         * @param guiSupplier The {@link Gui} {@link Supplier} for the result input {@link Gui}
         * @return This {@link Builder}
         */
        Builder setResultGui(Supplier<? extends Gui> guiSupplier);
        
        /**
         * Sets the handlers that are called when the viewer enables/disables a slot.
         *
         * @param handlers The slot toggle handlers, each receiving the slot and the new state
         */
        Builder setSlotToggleHandlers(List<? extends BiConsumer<? super Integer, ? super Boolean>> handlers);
        
        /**
         * Adds a handler that is called when the viewer enables/disables a slot.
         *
         * @param handler The slot toggle handler, receiving the slot and the new state
         */
        Builder addSlotToggleHandler(BiConsumer<? super Integer, ? super Boolean> handler);
        
    }
    
}
