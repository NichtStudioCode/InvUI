package xyz.xenondevs.invui.window;

import xyz.xenondevs.invui.gui.Gui;

import java.util.function.Supplier;

/**
 * A {@link Window} that uses a brewing stand inventory.
 */
public sealed interface BrewerWindow extends Window permits BrewerWindowImpl {
    
    /**
     * Creates a new {@link Builder} for a split {@link BrewerWindow}.
     *
     * @return The new {@link Builder}
     */
    static Builder builder() {
        return new BrewerWindowImpl.BuilderImpl();
    }
    
    /**
     * A {@link BrewerWindow} builder.
     */
    sealed interface Builder extends Window.Builder.Split<BrewerWindow, BrewerWindow.Builder> permits BrewerWindowImpl.BuilderImpl {
        
        /**
         * Sets the 1x1 input {@link Gui} of the {@link BrewerWindow}.
         *
         * @param gui The 1x1 input {@link Gui}
         * @return This {@link Builder}
         */
        default Builder setInputGui(Gui gui) {
            return setInputGui(() -> gui);
        }
        
        /**
         * Sets the {@link Gui.Builder} for the 1x1 input {@link Gui} of this {@link Builder}.
         * The {@link Gui.Builder} will be called every time a new {@link BrewerWindow} is created using this builder.
         *
         * @param builder The {@link Gui.Builder} for the 1x1 input {@link Gui}
         * @return This {@link Builder}
         */
        default Builder setInputGui(Gui.Builder<?, ?> builder) {
            return setInputGui(builder::build);
        }
        
        /**
         * Sets the {@link Gui} {@link Supplier} for the 1x1 input {@link Gui} of this {@link Builder}.
         * The {@link Supplier} will be called every time a new {@link BrewerWindow} is created using this builder.
         *
         * @param guiSupplier The {@link Gui} {@link Supplier} for the 1x1 input {@link Gui}
         * @return This {@link Builder}
         */
        Builder setInputGui(Supplier<? extends Gui> guiSupplier);
        
        /**
         * Sets the 1x1 fuel {@link Gui} of the {@link BrewerWindow}.
         *
         * @param gui The 1x1 fuel {@link Gui}
         * @return This {@link Builder}
         */
        default Builder setFuelGui(Gui gui) {
            return setFuelGui(() -> gui);
        }
        
        /**
         * Sets the {@link Gui.Builder} for the 1x1 fuel {@link Gui} of this {@link Builder}.
         * The {@link Gui.Builder} will be called every time a new {@link BrewerWindow} is created using this builder.
         *
         * @param builder The {@link Gui.Builder} for the 1x1 fuel {@link Gui}
         * @return This {@link Builder}
         */
        default Builder setFuelGui(Gui.Builder<?, ?> builder) {
            return setFuelGui(builder::build);
        }
        
        /**
         * Sets the {@link Gui} {@link Supplier} for the 1x1 fuel {@link Gui} of this {@link Builder}.
         * The {@link Supplier} will be called every time a new {@link BrewerWindow} is created using this builder.
         *
         * @param guiSupplier The {@link Gui} {@link Supplier} for the 1x1 fuel {@link Gui}
         * @return This {@link Builder}
         */
        Builder setFuelGui(Supplier<? extends Gui> guiSupplier);
        
        /**
         * Sets the 3x1 result {@link Gui} of the {@link BrewerWindow}.
         *
         * @param gui The 3x1 result {@link Gui}
         * @return This {@link Builder}
         */
        default Builder setResultGui(Gui gui) {
            return setResultGui(() -> gui);
        }
        
        /**
         * Sets the {@link Gui.Builder} for the 3x1 result {@link Gui} of this {@link Builder}.
         * The {@link Gui.Builder} will be called every time a new {@link BrewerWindow} is created using this builder.
         *
         * @param builder The {@link Gui.Builder} for the 3x1 result {@link Gui}
         * @return This {@link Builder}
         */
        default Builder setResultGui(Gui.Builder<?, ?> builder) {
            return setResultGui(builder::build);
        }
        
        /**
         * Sets the {@link Gui} {@link Supplier} for the 3x1 result {@link Gui} of this {@link Builder}.
         * The {@link Supplier} will be called every time a new {@link BrewerWindow} is created using this builder.
         *
         * @param guiSupplier The {@link Gui} {@link Supplier} for the 3x1 result {@link Gui}
         * @return This {@link Builder}
         */
        Builder setResultGui(Supplier<? extends Gui> guiSupplier);
        
    }
    
}
