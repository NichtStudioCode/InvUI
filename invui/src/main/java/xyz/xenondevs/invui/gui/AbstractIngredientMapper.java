package xyz.xenondevs.invui.gui;

import java.util.HashMap;

@SuppressWarnings("unchecked")
abstract sealed class AbstractIngredientMapper<S extends AbstractIngredientMapper<S>> implements IngredientMapper<S> permits IngredientPreset.Builder, Structure {
    
    /**
     * Maps ingredient keys ({@link Character characters}) to their corresponding {@link Ingredient} instances.
     */
    protected HashMap<Character, Ingredient> ingredientMap = new HashMap<>();
    
    @Override
    public S applyPreset(IngredientPreset preset) {
        ingredientMap.putAll(preset.getIngredientMap());
        return (S) this;
    }
    
    @Override
    public S addIngredient(char key, SlotElementSupplier elementSupplier) {
        handleUpdate();
        ingredientMap.put(key, new Ingredient.Element(elementSupplier));
        return (S) this;
    }
    
    @Override
    public S addIngredient(char key, Marker marker) {
        handleUpdate();
        ingredientMap.put(key, new Ingredient.Marker(marker));
        return (S) this;
    }
    
    @Override
    public S clone() {
        try {
            AbstractIngredientMapper<S> clone = (AbstractIngredientMapper<S>) super.clone();
            clone.ingredientMap = new HashMap<>(ingredientMap);
            return (S) clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }
    
    /**
     * Called when the ingredient mapping is updated.
     */
    protected void handleUpdate() {}
    
}
