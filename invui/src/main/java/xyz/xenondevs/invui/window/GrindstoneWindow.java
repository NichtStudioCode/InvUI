package xyz.xenondevs.invui.window;

import xyz.xenondevs.invui.gui.Gui;

import java.util.function.Supplier;

/**
 * A {@link Window} that uses a grindstone inventory.
 */
public sealed interface GrindstoneWindow extends Window permits GrindstoneWindowImpl {
    
    /**
     * Creates a new {@link Builder} for a split {@link GrindstoneWindow}.
     *
     * @return The new {@link Builder}
     */
    static Builder builder() {
        return new GrindstoneWindowImpl.BuilderImpl();
    }
    
    /**
     * A {@link GrindstoneWindow} builder.
     */
    sealed interface Builder extends Window.Builder.Split<GrindstoneWindow, GrindstoneWindow.Builder> permits GrindstoneWindowImpl.BuilderImpl {
        
        /**
         * Sets the 1x2 input {@link Gui} of the {@link GrindstoneWindow}.
         *
         * @param gui The 1x2 input {@link Gui}
         * @return This {@link Builder}
         */
        default Builder setInputGui(Gui gui) {
            return setInputGui(() -> gui);
        }
        
        /**
         * Sets the {@link Gui.Builder} for the 1x2 input {@link Gui} of this {@link Builder}.
         * The {@link Gui.Builder} will be called every time a new {@link GrindstoneWindow} is created using this builder.
         *
         * @param builder The {@link Gui.Builder} for the 1x2 input {@link Gui}
         * @return This {@link Builder}
         */
        default Builder setInputGui(Gui.Builder<?, ?> builder) {
            return setInputGui(builder::build);
        }
        
        /**
         * Sets the {@link Gui} {@link Supplier} for the 1x2 input {@link Gui} of this {@link Builder}.
         * The {@link Supplier} will be called every time a new {@link GrindstoneWindow} is created using this builder.
         *
         * @param guiSupplier The {@link Gui} {@link Supplier} for the 1x2 input {@link Gui}
         * @return This {@link Builder}
         */
        Builder setInputGui(Supplier<? extends Gui> guiSupplier);
        
        /**
         * Sets the 1x1 result {@link Gui} of the {@link GrindstoneWindow}.
         *
         * @param gui The 1x1 result {@link Gui}
         * @return This {@link Builder}
         */
        default Builder setResultGui(Gui gui) {
            return setResultGui(() -> gui);
        }
        
        /**
         * Sets the {@link Gui.Builder} for the 1x1 result {@link Gui} of this {@link Builder}.
         * The {@link Gui.Builder} will be called every time a new {@link GrindstoneWindow} is created using this builder.
         *
         * @param builder The {@link Gui.Builder} for the 1x1 result {@link Gui}
         * @return This {@link Builder}
         */
        default Builder setResultGui(Gui.Builder<?, ?> builder) {
            return setResultGui(builder::build);
        }
        
        /**
         * Sets the {@link Gui} {@link Supplier} for the 1x1 result {@link Gui} of this {@link Builder}.
         * The {@link Supplier} will be called every time a new {@link GrindstoneWindow} is created using this builder.
         *
         * @param guiSupplier The {@link Gui} {@link Supplier} for the 1x1 result {@link Gui}
         * @return This {@link Builder}
         */
        Builder setResultGui(Supplier<? extends Gui> guiSupplier);
        
    }
    
}
