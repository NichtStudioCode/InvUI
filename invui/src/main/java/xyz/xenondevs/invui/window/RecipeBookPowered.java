package xyz.xenondevs.invui.window;

import net.kyori.adventure.key.Key;

import java.util.List;
import java.util.function.Consumer;

/**
 * An interface for windows that have a recipe book.
 */
public sealed interface RecipeBookPowered permits CraftingTableWindow, FurnaceWindow {
    
    /**
     * Displays a ghost recipe in the window.
     * The ghost recipe will not persist through reopening the window and will disappear if the
     * player interacts with the associated slots.
     *
     * @param recipeId The {@link Key} of the recipe to display as a ghost recipe.
     * @throws IllegalStateException If the window is not currently open.
     */
    void sendGhostRecipe(Key recipeId);
    
    /**
     * Registers a recipe click handler that is called when a recipe is clicked in the recipe book.
     *
     * @param handler The recipe click handler to add.
     */
    void addRecipeClickHandler(Consumer<? super Key> handler);
    
    /**
     * Removes a previously registered recipe click handler.
     *
     * @param handler The recipe click handler to remove.
     */
    void removeRecipeClickHandler(Consumer<? super Key> handler);
    
    /**
     * Replaces all recipe click handlers with the given list.
     *
     * @param handlers The new recipe click handlers.
     */
    void setRecipeClickHandlers(List<? extends Consumer<? super Key>> handlers);
    
    /**
     * A builder for all windows that have a recipe book.
     *
     * @param <S> Self type of the builder, used for method chaining.
     */
    sealed interface Builder<S extends Builder<S>> permits CraftingTableWindow.Builder, FurnaceWindow.Builder {
        
        /**
         * Sets the recipe click handlers of the {@link AnvilWindow}.
         *
         * @param handlers The new recipe click handlers.
         * @return This {@link Builder}
         */
        S setRecipeClickHandlers(List<? extends Consumer<? super Key>> handlers);
        
        /**
         * Adds a recipe click handler to the {@link AnvilWindow}.
         *
         * @param handler The recipe click handler to add.
         * @return This {@link Builder}
         */
        S addRecipeClickHandler(Consumer<? super Key> handler);
        
    }
    
}
