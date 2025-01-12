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
    
    protected HashMap<Character, Ingredient> ingredientMap = new HashMap<>();
    
    @Override
    public S applyPreset(IngredientPreset preset) {
        ingredientMap.putAll(preset.getIngredientMap());
        return (S) this;
    }
    
    @Override
    public S addIngredient(char key, ItemStack itemStack) {
        return addIngredient(key, new ItemWrapper(itemStack));
    }
    
    @Override
    public S addIngredient(char key, ItemProvider itemProvider) {
        return addIngredient(key, Item.simple(itemProvider));
    }
    
    @Override
    public S addIngredient(char key, Item item) {
        return addIngredient(key, new SlotElement.Item(item));
    }
    
    @Override
    public S addIngredient(char key, Item.Builder<?> builder) {
        return addIngredient(key, builder::build);
    }
    
    @Override
    public S addIngredient(char key, Supplier<? extends Item> itemSupplier) {
        return addIngredientElementSupplier(key, () -> new SlotElement.Item(itemSupplier.get()));
    }
    
    @Override
    public S addIngredient(char key, Inventory inventory) {
        return addIngredientElementSupplier(key, new InventorySlotElementSupplier(inventory));
    }
    
    @Override
    public S addIngredient(char key, Inventory inventory, @Nullable ItemProvider background) {
        return addIngredientElementSupplier(key, new InventorySlotElementSupplier(inventory, background));
    }
    
    @Override
    public S addIngredient(char key, Gui gui) {
        return addIngredientElementSupplier(key, new GuiSlotElementSupplier(gui));
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
    
    protected void handleUpdate() {}
    
}
