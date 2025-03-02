package xyz.xenondevs.invui.window;

import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.item.Item;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A {@link Window} that uses a merchant (villager trade) inventory.
 */
public sealed interface MerchantWindow extends Window permits MerchantWindowImpl {
    
    /**
     * Creates a new {@link Builder} for a split {@link MerchantWindow}.
     *
     * @return The new {@link Builder}
     */
    static Builder split() {
        return new MerchantWindowImpl.BuilderImpl();
    }
    
    /**
     * Creates a new split {@link MerchantWindow} after configuring a
     * {@link Builder} using the given {@link Consumer}.
     *
     * @param consumer The consumer to configure the {@link Builder}
     * @return The created {@link MerchantWindow}
     */
    static MerchantWindow split(Consumer<Builder> consumer) {
        Builder builder = split();
        consumer.accept(builder);
        return builder.build();
    }
    
    /**
     * Sets the level of the merchant.
     * This level appears after the {@link Window#setTitle(String) title}, using the translation found
     * under {@code merchant.level.<level>}.
     * The following levels exist: 1 (Novice), 2 (Apprentice), 3 (Journeyman), 4 (Expert), 5 (Master).
     * <br>
     * If the level is set to <= 0, no level name and an always-empty progress bar will be displayed.
     * If the level is set to > 5, no level name and no progress bar will be displayed.
     *
     * @param level The level of the merchant
     */
    default void setLevel(int level) {
        setLevelSupplier(() -> level);
    }
    
    /**
     * Sets a supplier to retrieve the level of the merchant.
     * This level appears after the {@link Window#setTitle(String) title}, using the translation found
     * under {@code merchant.level.<level>}.
     * The following levels exist: 1 (Novice), 2 (Apprentice), 3 (Journeyman), 4 (Expert), 5 (Master).
     * <br>
     * If the level is set to <= 0, no level name and an always-empty progress bar will be displayed.
     * If the level is set to > 5, no level name and no progress bar will be displayed.
     *
     * @param levelSupplier The supplier to retrieve the level of the merchant
     */
    void setLevelSupplier(Supplier<Integer> levelSupplier);
    
    /**
     * Gets the level of the merchant.
     *
     * @return The level of the merchant
     */
    int getLevel();
    
    /**
     * Sets the progress of the experience bar (from 0 to 1).
     * If set to any value < 0, the progress bar and the merchant level name will be hidden.
     *
     * @param progress The progress of the experience bar
     */
    default void setProgress(double progress) {
        setProgressSupplier(() -> progress);
    }
    
    /**
     * Sets a supplier to retrieve the progress of the experience bar (from 0 to 1).
     * If set to any value < 0, the progress bar and the merchant level name will be hidden.
     *
     * @param progressSupplier The supplier to retrieve the progress of the experience bar
     */
    void setProgressSupplier(Supplier<Double> progressSupplier);
    
    /**
     * Gets the progress of the experience bar.
     *
     * @return The progress of the experience bar
     */
    double getProgress();
    
    /**
     * Sets whether the message "Villagers restock up to two times per day" should be displayed when hovering over
     * the arrow of disabled trades.
     *
     * @param enabled Whether the restocking message should be displayed
     */
    default void setRestockMessageEnabled(boolean enabled) {
        setRestockMessageEnabledSupplier(() -> enabled);
    }
    
    /**
     * Sets a supplier to retrieve whether the message "Villagers restock up to two times per day" should be displayed
     * when hovering over the arrow of disabled trades.
     *
     * @param restockMessageEnabledSupplier The supplier to retrieve whether the restocking message should be displayed
     */
    void setRestockMessageEnabledSupplier(Supplier<Boolean> restockMessageEnabledSupplier);
    
    /**
     * Gets whether the restocking message is enabled.
     *
     * @return Whether the restocking message is enabled
     */
    boolean isRestockMessageEnabled();
    
    /**
     * Sets the trades of the window, which are visualized as buttons on the left-hand side.
     *
     * @param trades The trades of the window
     */
    default void setTrades(List<? extends Trade> trades) {
        setTradesSupplier(() -> trades);
    }
    
    /**
     * Sets a supplier to retrieve the trades of the window, which are visualized as buttons on the left-hand side.
     *
     * @param tradesSupplier The supplier to retrieve the trades of the window
     */
    void setTradesSupplier(Supplier<List<? extends Trade>> tradesSupplier);
    
    /**
     * Gets the trades of the window.
     *
     * @return The trades of the window
     */
    List<? extends Trade> getTrades();
    
    /**
     * Updates the trades of the window by retrieving them from the registered supplier.
     */
    void updateTrades();
    
    /**
     * A trade in a {@link MerchantWindow}.
     * <br>
     * A trade can have three {@link Item Items}: the first input, the second input, and the output,
     * where the first input and output cannot be empty. When a player clicks on a trade, the click
     * handlers of all three items are fired.
     */
    sealed interface Trade permits MerchantWindowImpl.TradeImpl {
        
        /**
         * Creates a new {@link Builder} for a {@link Trade}.
         *
         * @return The new {@link Builder}
         */
        static Builder builder() {
            return new MerchantWindowImpl.TradeImpl.BuilderImpl();
        }
        
        /**
         * Gets the first input {@link Item} of the trade.
         *
         * @return The first input {@link Item}
         */
        @Nullable
        Item getFirstInput();
        
        /**
         * Gets the second input {@link Item} of the trade.
         *
         * @return The second input {@link Item}
         */
        @Nullable
        Item getSecondInput();
        
        /**
         * Gets the output {@link Item} of the trade.
         *
         * @return The output {@link Item}
         */
        @Nullable
        Item getOutput();
        
        /**
         * Gets the discount of the trade. This will be visualized as a crossed out amount on the first input item
         * and the discounted amount displayed next to it (discounted amount = amount - discount).
         * The second input item is unaffected by this.
         *
         * @return The discount of the trade
         */
        int getDiscount();
        
        /**
         * Gets whether the trade is available. If the trade is unavailable, the arrow will be crossed out.
         */
        boolean isAvailable();
        
        /**
         * Notifies all {@link MerchantWindow Windows} displaying this {@link Trade} to update their trade buttons.
         */
        void notifyWindows();
        
        /**
         * A {@link Trade} builder.
         */
        sealed interface Builder permits MerchantWindowImpl.TradeImpl.BuilderImpl {
            
            /**
             * Sets the first input {@link Item} of the trade.
             *
             * @param item The first input {@link Item}
             * @return This {@link Builder}
             */
            Builder setFirstInput(Item item);
            
            /**
             * Sets the second input {@link Item} of the trade.
             *
             * @param item The second input {@link Item}
             * @return This {@link Builder}
             */
            Builder setSecondInput(Item item);
            
            /**
             * Sets the output {@link Item} of the trade.
             *
             * @param item The output {@link Item}
             * @return This {@link Builder}
             */
            Builder setResult(Item item);
            
            /**
             * Sets the discount of the trade. This will be visualized as crossed out amount on the first input item
             * and the discounted amount displayed next to it (discounted amount = amount - discount).
             * The second input item is unaffected by this.
             *
             * @return This {@link Builder}
             */
            Builder setDiscount(int discount);
            
            /**
             * Sets a supplier to retrieve the discount of the trade.
             * This will be visualized as crossed out amount on the first input item  and the discounted amount
             * displayed next to it (discounted amount = amount - discount).
             * The second input item is unaffected by this.
             * <br>
             * The supplier will be called every time the trade is {@link Trade#notifyWindows()} refreshed.
             *
             * @param discountSupplier The supplier to retrieve the discount
             * @return This {@link Builder}
             */
            Builder setDiscountSupplier(Supplier<Integer> discountSupplier);
            
            /**
             * Sets whether the trade is available. If the trade is unavailable, the arrow will be crossed out.
             *
             * @param available Whether the trade is available
             * @return This {@link Builder}
             */
            Builder setAvailable(boolean available);
            
            /**
             * Sets a supplier to retrieve whether the trade is available. If the trade is unavailable,
             * the arrow will be crossed out.
             * <br>
             * The supplier will be called every time the trade is {@link Trade#notifyWindows()} refreshed.
             *
             * @param availableSupplier The supplier to retrieve whether the trade is available
             * @return This {@link Builder}
             */
            Builder setAvailableSupplier(Supplier<Boolean> availableSupplier);
            
            /**
             * Adds a consumer that is run when the trade is built.
             *
             * @param modifier The modifier
             * @return This {@link Builder}
             */
            Builder addModifier(Consumer<Trade> modifier);
            
            /**
             * Builds the {@link Trade}.
             *
             * @return The built {@link Trade}
             */
            Trade build();
            
        }
        
    }
    
    /**
     * A {@link MerchantWindow} builder.
     */
    sealed interface Builder extends Window.Builder.Split<MerchantWindow, Builder> permits MerchantWindowImpl.BuilderImpl {
        
        /**
         * Sets the result {@link Gui} of the {@link MerchantWindow}.
         *
         * @param gui The result input {@link Gui} of the {@link MerchantWindow}
         * @return This {@link Builder}
         */
        default Builder setUpperGui(Gui gui) {
            return setUpperGui(() -> gui);
        }
        
        /**
         * Sets the {@link Gui.Builder} for the result input {@link Gui} of this {@link Builder}.
         * The {@link Gui.Builder} will be called every time a new {@link MerchantWindow} is created using this builder.
         *
         * @param builder The {@link Gui.Builder} for the result input {@link Gui}
         * @return This {@link Builder}
         */
        default Builder setUpperGui(Gui.Builder<?, ?> builder) {
            return setUpperGui(builder::build);
        }
        
        /**
         * Sets the {@link Gui} {@link Supplier} for the result input {@link Gui} of this {@link Builder}.
         * The {@link Supplier} will be called every time a new {@link MerchantWindow} is created using this builder.
         *
         * @param guiSupplier The {@link Gui} {@link Supplier} for the result input {@link Gui}
         * @return This {@link Builder}
         */
        Builder setUpperGui(Supplier<Gui> guiSupplier);
        
        /**
         * Sets the level of the merchant.
         * This level appears after the {@link Window.Builder#setTitle(String) title}, using the translation found
         * under {@code merchant.level.<level>}.
         * The following levels exist: 1 (Novice), 2 (Apprentice), 3 (Journeyman), 4 (Expert), 5 (Master).
         * <br>
         * If the level is set to <= 0, no level name and an always-empty progress bar will be displayed.
         * If the level is set to > 5, no level name and no progress bar will be displayed.
         * <br>
         * Defaults to 0.
         *
         * @param level The level of the merchant
         * @return This {@link Builder}
         */
        default Builder setLevel(int level) {
            return setLevelSupplier(() -> level);
        }
        
        /**
         * Sets a supplier to retrieve the level of the merchant.
         * This level appears after the {@link Window.Builder#setTitle(String) title}, using the translation found
         * under {@code merchant.level.<level>}.
         * The following levels exist: 1 (Novice), 2 (Apprentice), 3 (Journeyman), 4 (Expert), 5 (Master).
         * <br>
         * If the level is set to <= 0, no level name and an always-empty progress bar will be displayed.
         * If the level is set to > 5, no level name and no progress bar will be displayed.
         * <br>
         * Defaults to 0.
         *
         * @param levelSupplier The supplier to retrieve the level of the merchant
         * @return This {@link Builder}
         */
        Builder setLevelSupplier(Supplier<Integer> levelSupplier);
        
        /**
         * Sets the progress of the experience bar (from 0 to 1).
         * If set to any value < 0, the progress bar and the merchant level name will be hidden.
         * <br>
         * Defaults to -1 (hidden).
         *
         * @param progress The progress of the experience bar
         * @return This {@link Builder}
         */
        default Builder setProgress(double progress) {
            return setProgressSupplier(() -> progress);
        }
        
        /**
         * Sets a supplier to retrieve the progress of the experience bar (from 0 to 1).
         * If set to any value < 0, the progress bar and the merchant level name will be hidden.
         * <br>
         * Defaults to -1 (hidden).
         *
         * @param progressSupplier The supplier to retrieve the progress of the experience bar
         * @return This {@link Builder}
         */
        Builder setProgressSupplier(Supplier<Double> progressSupplier);
        
        /**
         * Sets whether the message "Villagers restock up to two times per day" should be displayed when hovering over
         * the arrow of disabled trades.
         * <br>
         * Defaults to false.
         *
         * @param enabled Whether the restocking message should be displayed
         * @return This {@link Builder}
         */
        default Builder setRestockMessageEnabled(boolean enabled) {
            return setRestockMessageEnabledSupplier(() -> enabled);
        }
        
        /**
         * Sets a supplier to retrieve whether the message "Villagers restock up to two times per day" should be displayed
         * when hovering over the arrow of disabled trades.
         * <br>
         * Defaults to false.
         *
         * @param restockMessageEnabledSupplier The supplier to retrieve whether the restocking message should be displayed
         * @return This {@link Builder}
         */
        Builder setRestockMessageEnabledSupplier(Supplier<Boolean> restockMessageEnabledSupplier);
        
        /**
         * Sets the trades of the window, which are visualized as buttons on the left-hand side.
         *
         * @param trades The trades of the window
         * @return This {@link Builder}
         */
        default Builder setTrades(List<? extends Trade> trades) {
            return setTradesSupplier(() -> trades);
        }
        
        /**
         * Sets a supplier to retrieve the trades of the window, which are visualized as buttons on the left-hand side.
         *
         * @param tradesSupplier The supplier to retrieve the trades of the window
         * @return This {@link Builder}
         */
        Builder setTradesSupplier(Supplier<List<? extends Trade>> tradesSupplier);
        
    }
    
}
