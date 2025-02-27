package xyz.xenondevs.invui.window;

import java.util.List;
import java.util.function.Consumer;

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
    static AnvilWindow split(Consumer<Builder> consumer) {
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
    
    // TODO: add/set/remove rename handler
    
    /**
     * An {@link AnvilWindow} builder.
     *
     * @see Window.Builder.Normal
     * @see CartographyWindow.Builder
     */
    sealed interface Builder extends Window.Builder.Split<AnvilWindow, Builder> permits AnvilWindowImpl.BuilderImpl {
        
        /**
         * Sets the rename handlers of the {@link AnvilWindow}.
         *
         * @param renameHandlers The new rename handlers.
         * @return The current builder.
         */
        Builder setRenameHandlers(List<Consumer<String>> renameHandlers);
        
        /**
         * Adds a rename handler to the {@link AnvilWindow}.
         *
         * @param renameHandler The rename handler to add.
         * @return The current builder.
         */
        Builder addRenameHandler(Consumer<String> renameHandler);
        
    }
    
}
