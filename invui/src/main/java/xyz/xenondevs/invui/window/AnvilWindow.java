package xyz.xenondevs.invui.window;

import org.jetbrains.annotations.UnmodifiableView;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.state.Property;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A {@link Window} that uses an anvil inventory.
 */
public sealed interface AnvilWindow extends Window permits AnvilWindowImpl {
    
    /**
     * Creates a new {@link Builder Window Builder} for a split {@link AnvilWindow}.
     *
     * @return The new {@link Builder Window Builder}.
     */
    static Builder builder() {
        return new AnvilWindowImpl.BuilderImpl();
    }
    
    /**
     * Gets the current rename text.
     *
     * @return The current rename text.
     */
    String getRenameText();
    
    /**
     * Gets the configured enchantment cost.
     *
     * @return The configured enchantment cost.
     */
    int getEnchantmentCost();
    
    /**
     * Sets the enchantment cost.
     *
     * @param enchantmentCost The new enchantment cost.
     */
    void setEnchantmentCost(int enchantmentCost);
    
    /**
     * Gets whether the text field is always enabled, even if the input slot is empty.
     *
     * @return True if the text field is always enabled, false otherwise.Add AnvilWindow#setTextFieldAlwaysEnabled
     */
    boolean getTextFieldAlwaysEnabled();
    
    /**
     * Sets whether the text field is always enabled, even if the input slot is empty.
     *
     * @param textFieldAlwaysEnabled Whether the text field should always be enabled.
     */
    void setTextFieldAlwaysEnabled(boolean textFieldAlwaysEnabled);
    
    /**
     * Gets whether the result of the anvil is always valid, i.e. the arrow is never crossed out.
     *
     * @return True if the result is always valid, false otherwise.
     */
    boolean getResultAlwaysValid();
    
    /**
     * Sets whether the result of the anvil is always valid, i.e. the arrow is never crossed out.
     *
     * @param resultAlwaysValid Whether the result should always be valid.
     */
    void setResultAlwaysValid(boolean resultAlwaysValid);
    
    /**
     * Registers a rename handler that is called when the input text changes.
     * @param handler The rename handler to add.
     */
    void addRenameHandler(Consumer<? super String> handler);
    
    /**
     * Removes a previously registered rename handler.
     * @param handler The rename handler to remove.
     */
    void removeRenameHandler(Consumer<? super String> handler);
    
    /**
     * Replaces all rename handlers with the given list.
     * @param handlers The new rename handlers.
     */
    void setRenameHandlers(List<? extends Consumer<String>> handlers);
    
    /**
     * Gets the registered rename handlers.
     *
     * @return The registered rename handlers.
     */
    @UnmodifiableView
    List<Consumer<String>> getRenameHandlers();
    
    /**
     * An {@link AnvilWindow} builder.
     */
    sealed interface Builder extends Window.Builder.Split<AnvilWindow, Builder> permits AnvilWindowImpl.BuilderImpl {
        
        /**
         * Sets the upper {@link Gui} of the {@link AnvilWindow}.
         *
         * @param gui The upper {@link Gui}
         * @return This {@link Builder}
         */
        default Builder setUpperGui(Gui gui) {
            return setUpperGui(() -> gui);
        }
        
        /**
         * Sets the {@link Gui.Builder} for the upper {@link Gui} of this {@link Builder}.
         * The {@link Gui.Builder} will be called every time a new {@link AnvilWindow} is created using this builder.
         *
         * @param builder The {@link Gui.Builder} for the upper {@link Gui}
         * @return This {@link Builder}
         */
        default Builder setUpperGui(Gui.Builder<?, ?> builder) {
            return setUpperGui(builder::build);
        }
        
        /**
         * Sets the {@link Gui} {@link Supplier} for the upper {@link Gui} of this {@link Builder}.
         * The {@link Supplier} will be called every time a new {@link AnvilWindow} is created using this builder.
         *
         * @param guiSupplier The {@link Gui} {@link Supplier} for the upper {@link Gui}
         * @return This {@link Builder}
         */
        Builder setUpperGui(Supplier<? extends Gui> guiSupplier);
        
        /**
         * Sets the rename handlers of the {@link AnvilWindow}.
         *
         * @param handlers The new rename handlers.
         * @return This {@link Builder}
         */
        Builder setRenameHandlers(List<? extends Consumer<? super String>> handlers);
        
        /**
         * Adds a rename handler to the {@link AnvilWindow}.
         *
         * @param handlers The rename handler to add.
         * @return This {@link Builder}
         */
        Builder addRenameHandler(Consumer<? super String> handlers);
        
        /**
         * Sets whether the text field is always enabled, even if the input slot is empty.
         *
         * @param textFieldAlwaysEnabled Whether the text field should always be enabled.
         * @return This {@link Builder}
         */
        default Builder setTextFieldAlwaysEnabled(boolean textFieldAlwaysEnabled) {
            return setTextFieldAlwaysEnabled(Property.of(textFieldAlwaysEnabled));
        }
        
        /**
         * Sets the property containing whether the text field is always enabled, even if the input slot is empty.
         *
         * @param textFieldAlwaysEnabled The property containing whether the text field should always be enabled.
         * @return This {@link Builder}
         */
        Builder setTextFieldAlwaysEnabled(Property<? extends Boolean> textFieldAlwaysEnabled);
        
        /**
         * Sets whether the result of the anvil is always valid, i.e. the arrow is never crossed out.
         * Defaults to false.
         *
         * @param resultAlwaysValid Whether the result should always be valid.
         * @return This {@link Builder}
         */
        default Builder setResultAlwaysValid(boolean resultAlwaysValid) {
            return setResultAlwaysValid(Property.of(resultAlwaysValid));
        }
        
        /**
         * Sets the property containing whether the result of the anvil is always valid, i.e. the arrow is never crossed out.
         * Defaults to false.
         *
         * @param resultAlwaysValid The property containing whether the result should always be valid.
         * @return This {@link Builder}
         */
        Builder setResultAlwaysValid(Property<? extends Boolean> resultAlwaysValid);
        
    }
    
}
