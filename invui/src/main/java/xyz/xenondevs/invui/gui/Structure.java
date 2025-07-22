package xyz.xenondevs.invui.gui;

import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.ItemWrapper;

import java.util.HashMap;
import java.util.function.Supplier;

/**
 * Provides an easy way to design {@link Gui Guis} via a pattern string and ingredients.
 */
public class Structure extends AbstractIngredientMapper<Structure> {
    
    private static final HashMap<Character, Ingredient> globalIngredientMap = new HashMap<>();
    private static boolean globalIngredientsFrozen = false;
    
    private final String structureData;
    private final int width;
    private final int height;
    
    private @Nullable IngredientMatrix cachedIngredientMatrix;
    
    /**
     * Creates a new structure, with each string representing a row of the gui.
     * All strings must have the same length.
     *
     * @param structureData The structure data
     */
    public Structure(String... structureData) {
        this(sanitize(structureData[0]).length(), structureData.length, String.join("", structureData));
    }
    
    /**
     * Creates a new structure of the given width, height, and structure data.
     *
     * @param width         The width of the {@link Gui}
     * @param height        The height of the {@link Gui}
     * @param structureData The structure data
     */
    public Structure(int width, int height, String structureData) {
        this.width = width;
        this.height = height;
        this.structureData = sanitize(structureData);
        
        if (width * height != this.structureData.length())
            throw new IllegalArgumentException("Length of structure data does not match width * height");
    }
    
    private static String sanitize(String s) {
        return s.replace(" ", "").replace("\n", "");
    }
    
    /**
     * Adds a global {@link ItemStack} ingredient under the given key.
     * Global ingredients will be used for all {@link Structure Structures} which do not have an ingredient defined for
     * that key.
     *
     * @param key       The key of the ingredient
     * @param itemStack The {@link ItemStack} ingredient
     */
    public static void addGlobalIngredient(char key, ItemStack itemStack) {
        addGlobalIngredient(key, new ItemWrapper(itemStack));
    }
    
    /**
     * Adds a global {@link ItemProvider} ingredient under the given key.
     * Global ingredients will be used for all {@link Structure Structures} which do not have an ingredient defined for
     * that key.
     *
     * @param key          The key of the ingredient
     * @param itemProvider The {@link ItemProvider} ingredient
     */
    public static void addGlobalIngredient(char key, ItemProvider itemProvider) {
        addGlobalIngredient(key, Item.simple(itemProvider));
    }
    
    /**
     * Adds a global {@link Item} ingredient under the given key.
     * Global ingredients will be used for all {@link Structure Structures} which do not have an ingredient defined for
     * that key.
     *
     * @param key  The key of the ingredient
     * @param item The {@link Item} ingredient
     */
    public static void addGlobalIngredient(char key, Item item) {
        addGlobalIngredient(key, new SlotElement.Item(item));
    }
    
    /**
     * Adds a global {@link Item.Builder} ingredient under the given key.
     * Global ingredients will be used for all {@link Structure Structures} which do not have an ingredient defined for
     * that key.
     *
     * @param key     The key of the ingredient
     * @param builder The {@link Item.Builder} ingredient
     */
    public static void addGlobalIngredient(char key, Item.Builder<?> builder) {
        addGlobalIngredient(key, builder::build);
    }
    
    /**
     * Adds a global {@link Item} {@link Supplier} ingredient under the given key.
     * Global ingredients will be used for all {@link Structure Structures} which do not have an ingredient defined for
     * that key.
     *
     * @param key          The key of the ingredient
     * @param itemSupplier The {@link Item} {@link Supplier} ingredient
     */
    public static void addGlobalIngredient(char key, Supplier<? extends Item> itemSupplier) {
        addGlobalIngredientElementSupplier(key, () -> new SlotElement.Item(itemSupplier.get()));
    }
    
    /**
     * Adds a global {@link SlotElement} ingredient under the given key.
     * Global ingredients will be used for all {@link Structure Structures} which do not have an ingredient defined for
     * that key.
     *
     * @param key     The key of the ingredient
     * @param element The {@link SlotElement} ingredient
     */
    public static void addGlobalIngredient(char key, SlotElement element) {
        if (globalIngredientsFrozen)
            throw new IllegalStateException("Global ingredients are frozen");
        globalIngredientMap.put(key, new Ingredient(element));
    }
    
    /**
     * Adds a global {@link SlotElement} {@link Supplier} ingredient under the given key.
     * Global ingredients will be used for all {@link Structure Structures} which do not have an ingredient defined for
     * that key.
     *
     * @param key             The key of the ingredient
     * @param elementSupplier The {@link SlotElement} {@link Supplier} ingredient
     */
    public static void addGlobalIngredientElementSupplier(char key, Supplier<? extends SlotElement> elementSupplier) {
        if (globalIngredientsFrozen)
            throw new IllegalStateException("Global ingredients are frozen");
        globalIngredientMap.put(key, new Ingredient(elementSupplier));
    }
    
    /**
     * Adds a global {@link Marker} ingredient under the given key.
     * Global ingredients will be used for all {@link Structure Structures} which do not have an ingredient defined for
     * that key.
     *
     * @param key    The key of the ingredient
     * @param marker The {@link Marker} ingredient
     */
    public static void addGlobalIngredient(char key, Marker marker) {
        if (globalIngredientsFrozen)
            throw new IllegalStateException("Global ingredients are frozen");
        globalIngredientMap.put(key, new Ingredient(marker));
    }
    
    /**
     * Freezes the global ingredients, preventing further changes.
     */
    public static void freezeGlobalIngredients() {
        globalIngredientsFrozen = true;
    }
    
    @Override
    protected void handleUpdate() {
        cachedIngredientMatrix = null;
    }
    
    /**
     * Gets the {@link IngredientMatrix} for this {@link Structure}.
     *
     * @return The {@link IngredientMatrix}
     */
    IngredientMatrix getIngredientMatrix() {
        if (cachedIngredientMatrix != null)
            return cachedIngredientMatrix;
        
        HashMap<Character, Ingredient> ingredients = new HashMap<>(ingredientMap.size() + globalIngredientMap.size());
        ingredients.putAll(globalIngredientMap);
        ingredients.putAll(ingredientMap);
        ingredients.values().forEach(Ingredient::reset);
        return cachedIngredientMatrix = new IngredientMatrix(width, height, structureData, ingredients);
    }
    
    /**
     * Gets the width of this {@link Structure}.
     *
     * @return The width
     */
    int getWidth() {
        return width;
    }
    
    /**
     * Gets the height of this {@link Structure}.
     *
     * @return The height
     */
    int getHeight() {
        return height;
    }
    
    @Override
    public Structure clone() {
        Structure clone = super.clone();
        clone.cachedIngredientMatrix = null;
        return clone;
    }
    
}
