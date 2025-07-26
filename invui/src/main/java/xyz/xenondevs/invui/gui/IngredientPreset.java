package xyz.xenondevs.invui.gui;

import java.util.Map;

/**
 * A predefined set of ingredients that can be used in {@link Structure Structures}.
 */
public final class IngredientPreset {
    
    private final Map<Character, Ingredient> ingredientMap;
    
    private IngredientPreset(Map<Character, Ingredient> ingredientMap) {
        this.ingredientMap = ingredientMap;
    }
    
    /**
     * Creates a new {@link Builder} for creating {@link IngredientPreset IngredientPresets}.
     *
     * @return The new {@link Builder}.
     */
    public static Builder builder() {
        return new Builder();
    }
    
    Map<Character, Ingredient> getIngredientMap() {
        return ingredientMap;
    }
    
    /**
     * A builder for creating {@link IngredientPreset IngredientPresets}.
     */
    public static final class Builder extends AbstractIngredientMapper<Builder> {
        
        private Builder() {}
        
        /**
         * Builds the {@link IngredientPreset}.
         *
         * @return The created {@link IngredientPreset}.
         * @see IngredientMapper#applyPreset(IngredientPreset)
         */
        public IngredientPreset build() {
            return new IngredientPreset(ingredientMap);
        }
        
    }
    
}
