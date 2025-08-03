package xyz.xenondevs.invui.window;

import org.jetbrains.annotations.UnmodifiableView;
import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.state.Property;

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
    static Builder builder() {
        return new MerchantWindowImpl.BuilderImpl();
    }
    
    /**
     * Sets the level of the merchant.
     * This level appears after the {@link Window#setTitle(String) title}, using the translation found
     * under {@code merchant.level.<level>}.
     * The following levels exist: 1 (Novice), 2 (Apprentice), 3 (Journeyman), 4 (Expert), 5 (Master).
     * <br>
     * If the level is set to {@code <= 0}, no level name and an always-empty progress bar will be displayed.
     * If the level is set to {@code > 5}, no level name and no progress bar will be displayed.
     *
     * @param level The level of the merchant
     */
    void setLevel(int level);
    
    /**
     * Gets the level of the merchant.
     *
     * @return The level of the merchant
     */
    int getLevel();
    
    /**
     * Sets the progress of the experience bar (from 0 to 1).
     * If set to any value {@code < 0}, the progress bar and the merchant level name will be hidden.
     *
     * @param progress The progress of the experience bar
     */
    void setProgress(double progress);
    
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
    void setRestockMessageEnabled(boolean enabled);
    
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
    void setTrades(List<? extends Trade> trades);
    
    /**
     * Gets the trades of the window.
     *
     * @return The trades of the window
     */
    @UnmodifiableView
    List<Trade> getTrades();
    
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
         *
         * @return Whether the trade is available
         */
        boolean isAvailable();
        
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
             * @param discount The discount of the trade
             * @return This {@link Builder}
             */
            default Builder setDiscount(int discount) {
                return setDiscount(Property.of(discount));
            }
            
            /**
             * Sets the property containing the discount of the trade.
             * This will be visualized as crossed out amount on the first input item
             * and the discounted amount displayed next to it (discounted amount = amount - discount).
             * The second input item is unaffected by this.
             *
             * @param discount The discount property
             * @return This {@link Builder}
             */
            Builder setDiscount(Property<? extends Integer> discount);
            
            /**
             * Sets whether the trade is available. If the trade is unavailable, the arrow will be crossed out.
             *
             * @param available Whether the trade is available
             * @return This {@link Builder}
             */
            default Builder setAvailable(boolean available) {
                return setAvailable(Property.of(available));
            }
            
            /**
             * Sets the property containing the availability status of the trade.
             * If the trade is unavailable, the arrow will be crossed out.
             *
             * @param available Whether the trade is available
             * @return This {@link Builder}
             */
            Builder setAvailable(Property<? extends Boolean> available);
            
            /**
             * Adds a consumer that is run when the trade is built.
             *
             * @param modifier The modifier
             * @return This {@link Builder}
             */
            Builder addModifier(Consumer<? super Trade> modifier);
            
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
         * Sets the 3x1 upper {@link Gui} of the {@link MerchantWindow}.
         *
         * @param gui The 3x1 upper {@link Gui} of the {@link MerchantWindow}
         * @return This {@link Builder}
         */
        default Builder setUpperGui(Gui gui) {
            return setUpperGui(() -> gui);
        }
        
        /**
         * Sets the {@link Gui.Builder} for the 3x1 upper {@link Gui} of this {@link Builder}.
         * The {@link Gui.Builder} will be called every time a new {@link MerchantWindow} is created using this builder.
         *
         * @param builder The {@link Gui.Builder} for the 3x1 upper {@link Gui}
         * @return This {@link Builder}
         */
        default Builder setUpperGui(Gui.Builder<?, ?> builder) {
            return setUpperGui(builder::build);
        }
        
        /**
         * Sets the {@link Gui} {@link Supplier} for the 3x1 upper {@link Gui} of this {@link Builder}.
         * The {@link Supplier} will be called every time a new {@link MerchantWindow} is created using this builder.
         *
         * @param guiSupplier The {@link Gui} {@link Supplier} for the 3x1 upper {@link Gui}
         * @return This {@link Builder}
         */
        Builder setUpperGui(Supplier<? extends Gui> guiSupplier);
        
        /**
         * Sets the level of the merchant.
         * This level appears after the {@link Window.Builder#setTitle(String) title}, using the translation found
         * under {@code merchant.level.<level>}.
         * The following levels exist: 1 (Novice), 2 (Apprentice), 3 (Journeyman), 4 (Expert), 5 (Master).
         * <br>
         * If the level is set to {@code <= 0}, no level name and an always-empty progress bar will be displayed.
         * If the level is set to {@code > 5}, no level name and no progress bar will be displayed.
         * <br>
         * Defaults to 0.
         *
         * @param level The level of the merchant
         * @return This {@link Builder}
         */
        default Builder setLevel(int level) {
            return setLevel(Property.of(level));
        }
        
        /**
         * Sets the property containing the level of the merchant.
         * This level appears after the {@link Window.Builder#setTitle(String) title}, using the translation found
         * under {@code merchant.level.<level>}.
         * The following levels exist: 1 (Novice), 2 (Apprentice), 3 (Journeyman), 4 (Expert), 5 (Master).
         * <br>
         * If the level is set to {@code <= 0}, no level name and an always-empty progress bar will be displayed.
         * If the level is set to {@code > 5}, no level name and no progress bar will be displayed.
         * <br>
         * Defaults to 0.
         *
         * @param level The property containing the level of the merchant
         * @return This {@link Builder}
         */
        Builder setLevel(Property<? extends Integer> level);
        
        /**
         * Sets the progress of the experience bar (from 0 to 1).
         * If set to any value {@code < 0}, the progress bar and the merchant level name will be hidden.
         * <br>
         * Defaults to -1 (hidden).
         *
         * @param progress The progress of the experience bar
         * @return This {@link Builder}
         */
        default Builder setProgress(double progress) {
            return setProgress(Property.of(progress));
        }
        
        /**
         * Sets the property containing the progress of the experience bar (from 0 to 1).
         * If set to any value {@code < 0}, the progress bar and the merchant level name will be hidden.
         * <br>
         * Defaults to -1 (hidden).
         *
         * @param progress The property containing the progress of the experience bar
         * @return This {@link Builder}
         */
        Builder setProgress(Property<? extends Double> progress);
        
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
            return setRestockMessageEnabled(Property.of(enabled));
        }
        
        /**
         * Sets the property containing whether the message "Villagers restock up to two times per day" should
         * be displayed when hovering over the arrow of disabled trades.
         *
         * @param enabled The property containing whether the restocking message should be displayed
         * @return This {@link Builder}
         */
        Builder setRestockMessageEnabled(Property<? extends Boolean> enabled);
        
        /**
         * Sets the trades of the window, which are visualized as buttons on the left-hand side.
         *
         * @param trades The trades of the window
         * @return This {@link Builder}
         */
        default Builder setTrades(List<? extends Trade> trades) {
            return setTrades(Property.of(trades));
        }
        
        /**
         * Sets the property containing the trades of the window, which are visualized as buttons on the left-hand side.
         *
         * @param trades The property containing the trades of the window
         * @return This {@link Builder}
         */
        Builder setTrades(Property<? extends List<? extends Trade>> trades);
        
    }
    
}
