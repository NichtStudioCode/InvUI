package xyz.xenondevs.invui.window;

import xyz.xenondevs.invui.gui.Gui;

import java.util.function.Supplier;

/**
 * A {@link Window} that uses a crafter inventory.
 */
public sealed interface CraftingWindow extends Window, RecipeBookPowered permits CraftingWindowImpl {
    
    /**
     * Creates a new {@link Builder} for a split {@link CraftingWindow}.
     *
     * @return The new {@link Builder}
     */
    static Builder builder() {
        return new CraftingWindowImpl.BuilderImpl();
    }
    
    /**
     * A {@link CraftingWindow} builder.
     */
    sealed interface Builder extends Window.Builder.Split<CraftingWindow, Builder>, RecipeBookPowered.Builder<CraftingWindow.Builder> permits CraftingWindowImpl.BuilderImpl {
        
        /**
         * Sets the 3x3 crafting input {@link Gui} of the {@link CraftingWindow}.
         *
         * @param gui The 3x3 crafting input {@link Gui}
         * @return This {@link Builder}
         */
        default Builder setCraftingGui(Gui gui) {
            return setCraftingGui(() -> gui);
        }
        
        /**
         * Sets the {@link Gui.Builder} for the 3x3 crafting input {@link Gui} of this {@link Builder}.
         * The {@link Gui.Builder} will be called every time a new {@link CraftingWindow} is created using this builder.
         *
         * @param builder The {@link Gui.Builder} for the 3x3 crafting input {@link Gui}
         * @return This {@link Builder}
         */
        default Builder setCraftingGui(Gui.Builder<?, ?> builder) {
            return setCraftingGui(builder::build);
        }
        
        /**
         * Sets the {@link Gui} {@link Supplier} for the 3x3 crafting input {@link Gui} of this {@link Builder}.
         * The {@link Supplier} will be called every time a new {@link CraftingWindow} is created using this builder.
         *
         * @param guiSupplier The {@link Gui} {@link Supplier} for the 3x3 crafting input {@link Gui}
         * @return This {@link Builder}
         */
        Builder setCraftingGui(Supplier<? extends Gui> guiSupplier);
        
        /**
         * Sets the result {@link Gui} of the {@link CraftingWindow}.
         *
         * @param gui The result input {@link Gui} of the {@link CraftingWindow}
         * @return This {@link Builder}
         */
        default Builder setResultGui(Gui gui) {
            return setResultGui(() -> gui);
        }
        
        /**
         * Sets the {@link Gui.Builder} for the result input {@link Gui} of this {@link Builder}.
         * The {@link Gui.Builder} will be called every time a new {@link CraftingWindow} is created using this builder.
         *
         * @param builder The {@link Gui.Builder} for the result input {@link Gui}
         * @return This {@link Builder}
         */
        default Builder setResultGui(Gui.Builder<?, ?> builder) {
            return setResultGui(builder::build);
        }
        
        /**
         * Sets the {@link Gui} {@link Supplier} for the result input {@link Gui} of this {@link Builder}.
         * The {@link Supplier} will be called every time a new {@link CraftingWindow} is created using this builder.
         *
         * @param guiSupplier The {@link Gui} {@link Supplier} for the result input {@link Gui}
         * @return This {@link Builder}
         */
        Builder setResultGui(Supplier<? extends Gui> guiSupplier);
        
    }
    
}
