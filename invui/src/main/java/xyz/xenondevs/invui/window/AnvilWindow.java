package xyz.xenondevs.invui.window;

import org.jetbrains.annotations.UnmodifiableView;
import xyz.xenondevs.invui.gui.Gui;

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
    static Builder split() {
        return new AnvilWindowImpl.BuilderImpl();
    }
    
    /**
     * Creates a new split {@link AnvilWindow} after configuring a {@link Builder Window Builder} using the given {@link Consumer}.
     *
     * @param consumer The {@link Consumer} to configure the {@link Builder Window Builder}.
     * @return The created {@link AnvilWindow}.
     */
    static AnvilWindow split(Consumer<? super Builder> consumer) {
        Builder builder = split();
        consumer.accept(builder);
        return builder.build();
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
    void setRenameHandlers(List<? extends Consumer<? super String>> handlers);
    
    /**
     * Gets the registered rename handlers.
     * @return The registered rename handlers.
     */
    @UnmodifiableView
    List<Consumer<? super String>> getRenameHandlers();
    
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
        
    }
    
}
