package xyz.xenondevs.invui.window;

import xyz.xenondevs.invui.gui.Gui;

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
        
    }
    
}
