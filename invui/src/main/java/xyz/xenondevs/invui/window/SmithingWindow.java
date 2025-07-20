package xyz.xenondevs.invui.window;

import xyz.xenondevs.invui.gui.Gui;

import java.util.function.Supplier;

/**
 * A {@link Window} that uses a smithing table inventory.
 */
public sealed interface SmithingWindow extends Window permits SmithingWindowImpl {
    
    /**
     * Creates a new {@link Builder} for a split {@link SmithingWindow}.
     *
     * @return The new {@link Builder}
     */
    static Builder builder() {
        return new SmithingWindowImpl.BuilderImpl();
    }
    
    /**
     * A {@link SmithingWindow} builder.
     */
    sealed interface Builder extends Window.Builder.Split<SmithingWindow, SmithingWindow.Builder> permits SmithingWindowImpl.BuilderImpl {
        
        /**
         * Sets the 4x1 upper {@link Gui} of the {@link SmithingWindow}.
         *
         * @param gui The 4x1 upper {@link Gui}
         * @return This {@link Builder}
         */
        default Builder setUpperGui(Gui gui) {
            return setUpperGui(() -> gui);
        }
        
        /**
         * Sets the {@link Gui.Builder} for the 4x1 upper {@link Gui} of this {@link Builder}.
         * The {@link Gui.Builder} will be called every time a new {@link SmithingWindow} is created using this builder.
         *
         * @param builder The {@link Gui.Builder} for the 4x1 upper {@link Gui}
         * @return This {@link Builder}
         */
        default Builder setUpperGui(Gui.Builder<?, ?> builder) {
            return setUpperGui(builder::build);
        }
        
        /**
         * Sets the {@link Gui} {@link Supplier} for the 4x1 upper {@link Gui} of this {@link Builder}.
         * The {@link Supplier} will be called every time a new {@link SmithingWindow} is created using this builder.
         *
         * @param guiSupplier The {@link Gui} {@link Supplier} for the 4x1 upper {@link Gui}
         * @return This {@link Builder}
         */
        Builder setUpperGui(Supplier<? extends Gui> guiSupplier);
        
    }
    
}
