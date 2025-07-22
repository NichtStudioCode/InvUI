package xyz.xenondevs.invui.gui;

import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.inventory.Inventory;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.ItemWrapper;

import java.util.function.Supplier;

/**
 * Maps keys to ingredients for building {@link Gui Guis}.
 *
 * @param <S> The type of the {@link IngredientMapper}
 */
public interface IngredientMapper<S extends IngredientMapper<S>> extends Cloneable {
    
    /**
     * Applies the given {@link IngredientPreset} to this {@link IngredientMapper}.
     *
     * @param preset The {@link IngredientPreset} to apply
     * @return This {@link IngredientMapper}
     */
    S applyPreset(IngredientPreset preset);
    
    /**
     * Adds an {@link ItemStack} ingredient under the given key.
     *
     * @param key       The key of the ingredient
     * @param itemStack The {@link ItemStack} ingredient
     * @return This {@link IngredientMapper}
     */
    default S addIngredient(char key, ItemStack itemStack) {
        return addIngredient(key, new ItemWrapper(itemStack));
    }
    
    /**
     * Adds an {@link ItemProvider} ingredient under the given key.
     *
     * @param key          The key of the ingredient
     * @param itemProvider The {@link ItemProvider} ingredient
     * @return This {@link IngredientMapper}
     */
    default S addIngredient(char key, ItemProvider itemProvider) {
        return addIngredient(key, Item.simple(itemProvider));
    }
    
    /**
     * Adds an {@link Item} ingredient under the given key.
     *
     * @param key  The key of the ingredient
     * @param item The {@link Item} ingredient
     * @return This {@link IngredientMapper}
     */
    default S addIngredient(char key, Item item) {
        return addIngredient(key, new SlotElement.Item(item));
    }
    
    /**
     * Adds an {@link Item.Builder} ingredient under the given key.
     *
     * @param key     The key of the ingredient
     * @param builder The {@link Item.Builder} ingredient
     * @return This {@link IngredientMapper}
     */
    default S addIngredient(char key, Item.Builder<?> builder) {
        return addIngredient(key, builder::build);
    }
    
    /**
     * Adds an {@link Item} {@link Supplier} ingredient under the given key.
     *
     * @param key          The key of the ingredient
     * @param itemSupplier The {@link Item} {@link Supplier} ingredient
     * @return This {@link IngredientMapper}
     */
    default S addIngredient(char key, Supplier<? extends Item> itemSupplier) {
        return addIngredientElementSupplier(key, () -> new SlotElement.Item(itemSupplier.get()));
    }
    
    /**
     * Adds a {@link Inventory} ingredient under the given key.
     *
     * @param key       The key of the ingredient
     * @param inventory The {@link Inventory} ingredient
     * @return This {@link IngredientMapper}
     */
    default S addIngredient(char key, Inventory inventory) {
        return addIngredient(key, inventory, 0);
    }
    
    /**
     * Adds a {@link Inventory} ingredient with the given offset under the given key.
     *
     * @param key       The key of the ingredient
     * @param inventory The {@link Inventory} ingredient
     * @param offset    The slot offset inside the {@link Inventory} to start from
     * @return This {@link IngredientMapper}
     */
    default S addIngredient(char key, Inventory inventory, int offset) {
        return addIngredient(key, inventory, null, offset);
    }
    
    /**
     * Adds a {@link Inventory} ingredient under the given key.
     *
     * @param key        The key of the ingredient
     * @param inventory  The {@link Inventory} ingredient
     * @param background The background {@link ItemProvider} for the {@link Inventory}
     * @return This {@link IngredientMapper}
     */
    default S addIngredient(char key, Inventory inventory, @Nullable ItemProvider background) {
        return addIngredient(key, inventory, background, 0);
    }
    
    /**
     * Adds a {@link Inventory} ingredient with the given offset and background under the given key.
     *
     * @param key        The key of the ingredient
     * @param inventory  The {@link Inventory} ingredient
     * @param background The background {@link ItemProvider} for the {@link Inventory}
     * @param offset     The slot offset inside the {@link Inventory} to start from
     * @return This {@link IngredientMapper}
     */
    default S addIngredient(char key, Inventory inventory, @Nullable ItemProvider background, int offset) {
        return addIngredientElementSupplier(key, new InventorySlotElementSupplier(inventory, background, offset));
    }
    
    /**
     * Adds a {@link Gui} ingredient under the given key.
     *
     * @param key The key of the ingredient
     * @param gui The {@link Gui} ingredient
     * @return This {@link IngredientMapper}
     */
    default S addIngredient(char key, Gui gui) {
        return addIngredientElementSupplier(key, new GuiSlotElementSupplier(gui));
    }
    
    /**
     * Adds a {@link SlotElement} ingredient under the given key.
     *
     * @param key     The key of the ingredient
     * @param element The {@link SlotElement} ingredient
     * @return This {@link IngredientMapper}
     */
    S addIngredient(char key, SlotElement element);
    
    /**
     * Adds a {@link SlotElement} {@link Supplier} ingredient under the given key.
     *
     * @param key             The key of the ingredient
     * @param elementSupplier The {@link SlotElement} {@link Supplier} ingredient
     * @return This {@link IngredientMapper}
     */
    S addIngredientElementSupplier(char key, Supplier<? extends SlotElement> elementSupplier);
    
    /**
     * Adds a {@link Marker} ingredient under the given key.
     *
     * @param key    The key of the ingredient
     * @param marker The {@link Marker} ingredient
     * @return This {@link IngredientMapper}
     */
    S addIngredient(char key, Marker marker);
    
    /**
     * Clones the {@link IngredientMapper}.
     *
     * @return The cloned {@link IngredientMapper}
     */
    S clone();
    
}
