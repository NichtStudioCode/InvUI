package xyz.xenondevs.invui.gui.structure;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.SlotElement;
import xyz.xenondevs.invui.gui.SlotElement.ItemSlotElement;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.ItemWrapper;
import xyz.xenondevs.invui.item.impl.SimpleItem;
import xyz.xenondevs.invui.inventory.Inventory;

import java.util.HashMap;
import java.util.function.Supplier;

/**
 * Provides an easy way to design {@link Gui}s.
 * Inspired by Bukkit's {@link ShapedRecipe}, {@link Structure Structures} will let you
 * design a {@link Gui} in a similar way.
 */
public class Structure implements Cloneable {
    
    private static final HashMap<Character, Ingredient> globalIngredientMap = new HashMap<>();
    
    private final String structureData;
    private final int width;
    private final int height;
    
    private HashMap<Character, Ingredient> ingredientMap = new HashMap<>();
    private IngredientList ingredientList;
    
    /**
     * Sets the {@link Structure} of the {@link Gui} using the given structure data Strings.
     * Each String is interpreted as a row of the {@link Gui}. All Strings must have the same length.
     *
     * @param structureData The structure data
     */
    public Structure(@NotNull String @NotNull ... structureData) {
        this(sanitize(structureData[0]).length(), structureData.length, String.join("", structureData));
    }
    
    /**
     * Sets the {@link Structure} of the {@link Gui} using the given structure data, width and height.
     *
     * @param width         The width of the {@link Gui}
     * @param height        The height of the {@link Gui}
     * @param structureData The structure data
     */
    public Structure(int width, int height, @NotNull String structureData) {
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
    public static void addGlobalIngredient(char key, @NotNull ItemStack itemStack) {
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
    public static void addGlobalIngredient(char key, @NotNull ItemProvider itemProvider) {
        addGlobalIngredient(key, new SimpleItem(itemProvider));
    }
    
    /**
     * Adds a global {@link Item} ingredient under the given key.
     * Global ingredients will be used for all {@link Structure Structures} which do not have an ingredient defined for
     * that key.
     *
     * @param key  The key of the ingredient
     * @param item The {@link Item} ingredient
     */
    public static void addGlobalIngredient(char key, @NotNull Item item) {
        addGlobalIngredient(key, new ItemSlotElement(item));
    }
    
    /**
     * Adds a global {@link Item} {@link Supplier} ingredient under the given key.
     * Global ingredients will be used for all {@link Structure Structures} which do not have an ingredient defined for
     * that key.
     *
     * @param key          The key of the ingredient
     * @param itemSupplier The {@link Item} {@link Supplier} ingredient
     */
    public static void addGlobalIngredient(char key, @NotNull Supplier<? extends Item> itemSupplier) {
        addGlobalIngredientElementSupplier(key, () -> new ItemSlotElement(itemSupplier.get()));
    }
    
    /**
     * Adds a global {@link SlotElement} ingredient under the given key.
     * Global ingredients will be used for all {@link Structure Structures} which do not have an ingredient defined for
     * that key.
     *
     * @param key     The key of the ingredient
     * @param element The {@link SlotElement} ingredient
     */
    public static void addGlobalIngredient(char key, @NotNull SlotElement element) {
        globalIngredientMap.put(key, new Ingredient(element));
    }
    
    /**
     * Adds a global {@link Marker} ingredient under the given key.
     * Global ingredients will be used for all {@link Structure Structures} which do not have an ingredient defined for
     * that key.
     *
     * @param key    The key of the ingredient
     * @param marker The {@link Marker} ingredient
     */
    public static void addGlobalIngredient(char key, @NotNull Marker marker) {
        globalIngredientMap.put(key, new Ingredient(marker));
    }
    
    /**
     * Adds a global {@link SlotElement} {@link Supplier} ingredient under the given key.
     * Global ingredients will be used for all {@link Structure Structures} which do not have an ingredient defined for
     * that key.
     *
     * @param key             The key of the ingredient
     * @param elementSupplier The {@link SlotElement} {@link Supplier} ingredient
     */
    public static void addGlobalIngredientElementSupplier(char key, @NotNull Supplier<? extends SlotElement> elementSupplier) {
        globalIngredientMap.put(key, new Ingredient(elementSupplier));
    }
    
    /**
     * Adds an {@link ItemStack} ingredient under the given key.
     *
     * @param key       The key of the ingredient
     * @param itemStack The {@link ItemStack} ingredient
     * @return This {@link Structure}
     */
    @Contract("_, _ -> this")
    public @NotNull Structure addIngredient(char key, @NotNull ItemStack itemStack) {
        if (ingredientList != null) throw new IllegalStateException("Structure is locked");
        return addIngredient(key, new ItemWrapper(itemStack));
    }
    
    /**
     * Adds an {@link ItemProvider} ingredient under the given key.
     *
     * @param key          The key of the ingredient
     * @param itemProvider The {@link ItemProvider} ingredient
     * @return This {@link Structure}
     */
    @Contract("_, _ -> this")
    public @NotNull Structure addIngredient(char key, @NotNull ItemProvider itemProvider) {
        if (ingredientList != null) throw new IllegalStateException("Structure is locked");
        return addIngredient(key, new SimpleItem(itemProvider));
    }
    
    /**
     * Adds an {@link Item} ingredient under the given key.
     *
     * @param key  The key of the ingredient
     * @param item The {@link Item} ingredient
     * @return This {@link Structure}
     */
    @Contract("_, _ -> this")
    public @NotNull Structure addIngredient(char key, @NotNull Item item) {
        if (ingredientList != null) throw new IllegalStateException("Structure is locked");
        return addIngredient(key, new ItemSlotElement(item));
    }
    
    /**
     * Adds a {@link Inventory} ingredient under the given key.
     *
     * @param key       The key of the ingredient
     * @param inventory The {@link Inventory} ingredient
     * @return This {@link Structure}
     */
    @Contract("_, _ -> this")
    public @NotNull Structure addIngredient(char key, @NotNull Inventory inventory) {
        if (ingredientList != null) throw new IllegalStateException("Structure is locked");
        return addIngredientElementSupplier(key, new InventorySlotElementSupplier(inventory));
    }
    
    /**
     * Adds a {@link Inventory} ingredient under the given key.
     *
     * @param key        The key of the ingredient
     * @param inventory  The {@link Inventory} ingredient
     * @param background The background {@link ItemProvider} for the {@link Inventory}
     * @return This {@link Structure}
     */
    @Contract("_, _, _ -> this")
    public @NotNull Structure addIngredient(char key, @NotNull Inventory inventory, @Nullable ItemProvider background) {
        if (ingredientList != null) throw new IllegalStateException("Structure is locked");
        return addIngredientElementSupplier(key, new InventorySlotElementSupplier(inventory, background));
    }
    
    /**
     * Adds a {@link Inventory} ingredient under the given key.
     *
     * @param key     The key of the ingredient
     * @param element The {@link SlotElement} ingredient
     * @return This {@link Structure}
     */
    @Contract("_, _ -> this")
    public @NotNull Structure addIngredient(char key, @NotNull SlotElement element) {
        if (ingredientList != null) throw new IllegalStateException("Structure is locked");
        ingredientMap.put(key, new Ingredient(element));
        return this;
    }
    
    /**
     * Adds a {@link Marker} ingredient under the given key.
     *
     * @param key    The key of the ingredient
     * @param marker The {@link Marker} ingredient
     * @return This {@link Structure}
     */
    @Contract("_, _ -> this")
    public @NotNull Structure addIngredient(char key, @NotNull Marker marker) {
        if (ingredientList != null) throw new IllegalStateException("Structure is locked");
        ingredientMap.put(key, new Ingredient(marker));
        return this;
    }
    
    /**
     * Adds an {@link ItemStack} {@link Supplier} ingredient under the given key.
     *
     * @param key          The key of the ingredient
     * @param itemSupplier The {@link ItemStack} {@link Supplier} ingredient
     * @return This {@link Structure}
     */
    @Contract("_, _ -> this")
    public @NotNull Structure addIngredient(char key, @NotNull Supplier<? extends Item> itemSupplier) {
        if (ingredientList != null) throw new IllegalStateException("Structure is locked");
        ingredientMap.put(key, new Ingredient(() -> new ItemSlotElement(itemSupplier.get())));
        return this;
    }
    
    /**
     * Adds a {@link SlotElement} {@link Supplier} ingredient under the given key.
     *
     * @param key             The key of the ingredient
     * @param elementSupplier The {@link SlotElement} {@link Supplier} ingredient
     * @return This {@link Structure}
     */
    @Contract("_, _ -> this")
    public @NotNull Structure addIngredientElementSupplier(char key, @NotNull Supplier<? extends SlotElement> elementSupplier) {
        if (ingredientList != null) throw new IllegalStateException("Structure is locked");
        ingredientMap.put(key, new Ingredient(elementSupplier));
        return this;
    }
    
    /**
     * Gets the {@link IngredientList} for this {@link Structure}.
     * Calling this method will lock the {@link Structure} and prevent further changes.
     *
     * @return The {@link IngredientList}
     */
    public @NotNull IngredientList getIngredientList() {
        if (ingredientList != null) return ingredientList;
        
        HashMap<Character, Ingredient> ingredients = new HashMap<>(globalIngredientMap);
        ingredients.putAll(this.ingredientMap);
        return ingredientList = new IngredientList(width, height, structureData, ingredients);
    }
    
    /**
     * Gets the width of this {@link Structure}.
     *
     * @return The width
     */
    public int getWidth() {
        return width;
    }
    
    /**
     * Gets the height of this {@link Structure}.
     *
     * @return The height
     */
    public int getHeight() {
        return height;
    }
    
    /**
     * Clones this {@link Structure}.
     *
     * @return The cloned {@link Structure}
     */
    @Contract(value = "-> new", pure = true)
    @Override
    public @NotNull Structure clone() {
        try {
            Structure clone = (Structure) super.clone();
            clone.ingredientMap = new HashMap<>(ingredientMap);
            clone.ingredientList = null;
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
    
}
