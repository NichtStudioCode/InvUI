package xyz.xenondevs.invui.window;

import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.state.Property;

import java.util.function.Supplier;

/**
 * A {@link Window} that uses a crafter inventory.
 */
public sealed interface FurnaceWindow extends Window, RecipeBookPowered permits FurnaceWindowImpl {
    
    /**
     * Creates a new {@link Builder} for a split {@link FurnaceWindow}.
     *
     * @return The new {@link Builder}
     */
    static Builder builder() {
        return new FurnaceWindowImpl.BuilderImpl();
    }
    
    /**
     * Sets the cook progress, i.e. how much of the arrow is white.
     *
     * @param progress The cook progress, between 0.0 and 1.0.
     * @throws IllegalArgumentException If the progress is not between 0.0 and 1.0.
     */
    void setCookProgress(double progress);
    
    /**
     * Gets the cook progress, i.e. how much of the arrow is white.
     *
     * @return The cook progress, between 0.0 and 1.0.
     */
    double getCookProgress();
    
    /**
     * Sets the burn progress, i.e. how much of the fire is white.
     *
     * @param progress The burn progress, between 0.0 and 1.0.
     * @throws IllegalArgumentException If the progress is not between 0.0 and 1.0.
     */
    void setBurnProgress(double progress);
    
    /**
     * Gets the burn progress, i.e. how much of the fire is white.
     *
     * @return The burn progress, between 0.0 and 1.0.
     */
    double getBurnProgress();
    
    /**
     * A {@link FurnaceWindow} builder.
     */
    sealed interface Builder extends Window.Builder.Split<FurnaceWindow, Builder>, RecipeBookPowered.Builder<FurnaceWindow.Builder> permits FurnaceWindowImpl.BuilderImpl {
        
        /**
         * Sets the 1x2 input {@link Gui} of the {@link FurnaceWindow}.
         *
         * @param gui The 1x2 input {@link Gui}
         * @return This {@link Builder}
         */
        default Builder setInputGui(Gui gui) {
            return setInputGui(() -> gui);
        }
        
        /**
         * Sets the {@link Gui.Builder} for the 1x2 input {@link Gui} of this {@link Builder}.
         * The {@link Gui.Builder} will be called every time a new {@link FurnaceWindow} is created using this builder.
         *
         * @param builder The {@link Gui.Builder} for the 1x2 input {@link Gui}
         * @return This {@link Builder}
         */
        default Builder setInputGui(Gui.Builder<?, ?> builder) {
            return setInputGui(builder::build);
        }
        
        /**
         * Sets the {@link Gui} {@link Supplier} for the 1x2 input {@link Gui} of this {@link Builder}.
         * The {@link Supplier} will be called every time a new {@link FurnaceWindow} is created using this builder.
         *
         * @param guiSupplier The {@link Gui} {@link Supplier} for the 1x2 input {@link Gui}
         * @return This {@link Builder}
         */
        Builder setInputGui(Supplier<? extends Gui> guiSupplier);
        
        /**
         * Sets the result {@link Gui} of the {@link FurnaceWindow}.
         *
         * @param gui The result input {@link Gui} of the {@link FurnaceWindow}
         * @return This {@link Builder}
         */
        default Builder setResultGui(Gui gui) {
            return setResultGui(() -> gui);
        }
        
        /**
         * Sets the {@link Gui.Builder} for the result input {@link Gui} of this {@link Builder}.
         * The {@link Gui.Builder} will be called every time a new {@link FurnaceWindow} is created using this builder.
         *
         * @param builder The {@link Gui.Builder} for the result input {@link Gui}
         * @return This {@link Builder}
         */
        default Builder setResultGui(Gui.Builder<?, ?> builder) {
            return setResultGui(builder::build);
        }
        
        /**
         * Sets the {@link Gui} {@link Supplier} for the result input {@link Gui} of this {@link Builder}.
         * The {@link Supplier} will be called every time a new {@link FurnaceWindow} is created using this builder.
         *
         * @param guiSupplier The {@link Gui} {@link Supplier} for the result input {@link Gui}
         * @return This {@link Builder}
         */
        Builder setResultGui(Supplier<? extends Gui> guiSupplier);
        
        /**
         * Sets the cook progress, i.e. how much of the arrow is white.
         *
         * @param progress The cook progress, between 0.0 and 1.0.
         * @return This {@link Builder}
         */
        default Builder setCookProgress(double progress) {
            return setCookProgress(Property.of(progress));
        }
        
        /**
         * Sets the property containing the cook progress, i.e. how much of the arrow is white.
         *
         * @param progress The cook progress property, between 0.0 and 1.0.
         * @return This {@link Builder}
         */
        Builder setCookProgress(Property<? extends Double> progress);
        
        /**
         * Sets the burn progress, i.e. how much of the fire is white.
         *
         * @param progress The burn progress, between 0.0 and 1.0.
         * @return This {@link Builder}
         */
        default Builder setBurnProgress(double progress) {
            return setBurnProgress(Property.of(progress));
        }
        
        /**
         * Sets the property containing the burn progress, i.e. how much of the fire is white.
         *
         * @param progress The burn progress property, between 0.0 and 1.0.
         * @return This {@link Builder}
         */
        Builder setBurnProgress(Property<? extends Double> progress);
        
    }
    
}
