package xyz.xenondevs.invui.window;

import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.state.Property;

import java.util.function.Supplier;

/**
 * A {@link Window} that uses a brewing stand inventory.
 */
public sealed interface BrewingWindow extends Window permits BrewingWindowImpl {
    
    /**
     * Creates a new {@link Builder} for a split {@link BrewingWindow}.
     *
     * @return The new {@link Builder}
     */
    static Builder builder() {
        return new BrewingWindowImpl.BuilderImpl();
    }
    
    /**
     * Sets the brew progress, i.e. how much of the arrow is white.
     *
     * @param progress The brew progress, between 0.0 and 1.0.
     * @throws IllegalArgumentException If the progress is not between 0.0 and 1.0.
     */
    void setBrewProgress(double progress);
    
    /**
     * Gets the brew progress, i.e. how much of the arrow is white.
     *
     * @return The brew progress, between 0.0 and 1.0.
     */
    double getBrewProgress();
    
    /**
     * Sets the fuel progress, i.e. how much of the blaze bar is filled in.
     *
     * @param progress The fuel progress, between 0.0 and 1.0.
     */
    void setFuelProgress(double progress);
    
    /**
     * Gets the fuel progress, i.e. how much of the blaze bar is filled in.
     *
     * @return The fuel progress, between 0.0 and 1.0.
     */
    double getFuelProgress();
    
    /**
     * A {@link BrewingWindow} builder.
     */
    sealed interface Builder extends Window.Builder.Split<BrewingWindow, BrewingWindow.Builder> permits BrewingWindowImpl.BuilderImpl {
        
        /**
         * Sets the 1x1 input {@link Gui} of the {@link BrewingWindow}.
         *
         * @param gui The 1x1 input {@link Gui}
         * @return This {@link Builder}
         */
        default Builder setInputGui(Gui gui) {
            return setInputGui(() -> gui);
        }
        
        /**
         * Sets the {@link Gui.Builder} for the 1x1 input {@link Gui} of this {@link Builder}.
         * The {@link Gui.Builder} will be called every time a new {@link BrewingWindow} is created using this builder.
         *
         * @param builder The {@link Gui.Builder} for the 1x1 input {@link Gui}
         * @return This {@link Builder}
         */
        default Builder setInputGui(Gui.Builder<?, ?> builder) {
            return setInputGui(builder::build);
        }
        
        /**
         * Sets the {@link Gui} {@link Supplier} for the 1x1 input {@link Gui} of this {@link Builder}.
         * The {@link Supplier} will be called every time a new {@link BrewingWindow} is created using this builder.
         *
         * @param guiSupplier The {@link Gui} {@link Supplier} for the 1x1 input {@link Gui}
         * @return This {@link Builder}
         */
        Builder setInputGui(Supplier<? extends Gui> guiSupplier);
        
        /**
         * Sets the 1x1 fuel {@link Gui} of the {@link BrewingWindow}.
         *
         * @param gui The 1x1 fuel {@link Gui}
         * @return This {@link Builder}
         */
        default Builder setFuelGui(Gui gui) {
            return setFuelGui(() -> gui);
        }
        
        /**
         * Sets the {@link Gui.Builder} for the 1x1 fuel {@link Gui} of this {@link Builder}.
         * The {@link Gui.Builder} will be called every time a new {@link BrewingWindow} is created using this builder.
         *
         * @param builder The {@link Gui.Builder} for the 1x1 fuel {@link Gui}
         * @return This {@link Builder}
         */
        default Builder setFuelGui(Gui.Builder<?, ?> builder) {
            return setFuelGui(builder::build);
        }
        
        /**
         * Sets the {@link Gui} {@link Supplier} for the 1x1 fuel {@link Gui} of this {@link Builder}.
         * The {@link Supplier} will be called every time a new {@link BrewingWindow} is created using this builder.
         *
         * @param guiSupplier The {@link Gui} {@link Supplier} for the 1x1 fuel {@link Gui}
         * @return This {@link Builder}
         */
        Builder setFuelGui(Supplier<? extends Gui> guiSupplier);
        
        /**
         * Sets the 3x1 result {@link Gui} of the {@link BrewingWindow}.
         *
         * @param gui The 3x1 result {@link Gui}
         * @return This {@link Builder}
         */
        default Builder setResultGui(Gui gui) {
            return setResultGui(() -> gui);
        }
        
        /**
         * Sets the {@link Gui.Builder} for the 3x1 result {@link Gui} of this {@link Builder}.
         * The {@link Gui.Builder} will be called every time a new {@link BrewingWindow} is created using this builder.
         *
         * @param builder The {@link Gui.Builder} for the 3x1 result {@link Gui}
         * @return This {@link Builder}
         */
        default Builder setResultGui(Gui.Builder<?, ?> builder) {
            return setResultGui(builder::build);
        }
        
        /**
         * Sets the {@link Gui} {@link Supplier} for the 3x1 result {@link Gui} of this {@link Builder}.
         * The {@link Supplier} will be called every time a new {@link BrewingWindow} is created using this builder.
         *
         * @param guiSupplier The {@link Gui} {@link Supplier} for the 3x1 result {@link Gui}
         * @return This {@link Builder}
         */
        Builder setResultGui(Supplier<? extends Gui> guiSupplier);
        
        /**
         * Sets the brew progress, i.e. how much of the arrow is white.
         * @param progress The brew progress, between 0.0 and 1.0.
         * @return This {@link Builder}
         */
        default Builder setBrewProgress(double progress) {
            return setBrewProgress(Property.of(progress));
        }
        
        /**
         * Sets the property containing the brew progress, i.e. how much of the arrow is white.
         * @param progress The brew progress property, between 0.0 and 1.0.
         * @return This {@link Builder}
         */
        Builder setBrewProgress(Property<? extends Double> progress);
        
        /**
         * Sets the fuel progress, i.e. how much of the blaze bar is filled in.
         * @param progress The fuel progress, between 0.0 and 1.0.
         * @return This {@link Builder}
         */
        default Builder setFuelProgress(double progress) {
            return setFuelProgress(Property.of(progress));
        }
        
        /**
         * Sets the property containing the fuel progress, i.e. how much of the blaze bar is filled in.
         * @param progress The fuel progress property, between 0.0 and 1.0.
         * @return This {@link Builder}
         */
        Builder setFuelProgress(Property<? extends Double> progress);
        
    }
    
}
