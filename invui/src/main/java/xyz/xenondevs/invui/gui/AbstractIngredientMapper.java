package xyz.xenondevs.invui.gui;

import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.inventory.Inventory;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.ItemWrapper;

import java.util.HashMap;
import java.util.function.Supplier;

@SuppressWarnings("unchecked")
abstract class AbstractIngredientMapper<S extends AbstractIngredientMapper<S>> implements IngredientMapper<S> {
    
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
    public S addIngredient(char key, SlotElement element) {
        handleUpdate();
        ingredientMap.put(key, new Ingredient(element));
        return (S) this;
    }
    
    @Override
    public S addIngredientElementSupplier(char key, Supplier<? extends SlotElement> elementSupplier) {
        handleUpdate();
        ingredientMap.put(key, new Ingredient(elementSupplier));
        return (S) this;
    }
    
    @Override
    public S addIngredient(char key, Marker marker) {
        handleUpdate();
        ingredientMap.put(key, new Ingredient(marker));
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
