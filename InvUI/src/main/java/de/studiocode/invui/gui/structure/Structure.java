package de.studiocode.invui.gui.structure;

import de.studiocode.invui.gui.GUI;
import de.studiocode.invui.gui.SlotElement;
import de.studiocode.invui.gui.SlotElement.ItemSlotElement;
import de.studiocode.invui.item.Item;
import de.studiocode.invui.item.ItemProvider;
import de.studiocode.invui.item.ItemWrapper;
import de.studiocode.invui.item.impl.SimpleItem;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.function.Supplier;

/**
 * Provides an easy way to design {@link GUI}s.
 * Inspired by Bukkit's {@link ShapedRecipe}, {@link Structure Structures} will let you
 * design a {@link GUI} in a similar way.
 */
public class Structure implements Cloneable {
    
    private static final HashMap<Character, Ingredient> globalIngredientMap = new HashMap<>();
    
    private final String structureData;
    private HashMap<Character, Ingredient> ingredientMap = new HashMap<>();
    private IngredientList ingredientList;
    
    public Structure(String structureData) {
        this.structureData = structureData
            .replace(" ", "")
            .replace("\n", "");
    }
    
    public static void addGlobalIngredient(char key, @NotNull ItemStack itemStack) {
        addGlobalIngredient(key, new ItemWrapper(itemStack));
    }
    
    public static void addGlobalIngredient(char key, @NotNull ItemProvider itemProvider) {
        addGlobalIngredient(key, new SimpleItem(itemProvider));
    }
    
    public static void addGlobalIngredient(char key, @NotNull Item item) {
        addGlobalIngredient(key, new ItemSlotElement(item));
    }
    
    public static void addGlobalIngredient(char key, @NotNull Supplier<? extends Item> itemSupplier) {
        addGlobalIngredientElementSupplier(key, () -> new ItemSlotElement(itemSupplier.get()));
    }
    
    public static void addGlobalIngredient(char key, @NotNull SlotElement element) {
        globalIngredientMap.put(key, new Ingredient(element));
    }
    
    public static void addGlobalIngredient(char key, @NotNull String marker) {
        globalIngredientMap.put(key, new Ingredient(marker));
    }
    
    public static void addGlobalIngredientElementSupplier(char key, @NotNull Supplier<? extends SlotElement> elementSupplier) {
        globalIngredientMap.put(key, new Ingredient(elementSupplier));
    }
    
    public Structure addIngredient(char key, @NotNull ItemStack itemStack) {
        if (ingredientList != null) throw new UnsupportedOperationException("Structure is locked");
        return addIngredient(key, new ItemWrapper(itemStack));
    }
    
    public Structure addIngredient(char key, @NotNull ItemProvider itemProvider) {
        if (ingredientList != null) throw new UnsupportedOperationException("Structure is locked");
        return addIngredient(key, new SimpleItem(itemProvider));
    }
    
    public Structure addIngredient(char key, @NotNull Item item) {
        if (ingredientList != null) throw new UnsupportedOperationException("Structure is locked");
        return addIngredient(key, new ItemSlotElement(item));
    }
    
    public Structure addIngredient(char key, @NotNull SlotElement element) {
        if (ingredientList != null) throw new UnsupportedOperationException("Structure is locked");
        ingredientMap.put(key, new Ingredient(element));
        return this;
    }
    
    public Structure addIngredient(char key, @NotNull String marker) {
        if (ingredientList != null) throw new UnsupportedOperationException("Structure is locked");
        ingredientMap.put(key, new Ingredient(marker));
        return this;
    }
    
    public Structure addIngredient(char key, @NotNull Supplier<? extends Item> itemSupplier) {
        if (ingredientList != null) throw new UnsupportedOperationException("Structure is locked");
        ingredientMap.put(key, new Ingredient(() -> new ItemSlotElement(itemSupplier.get())));
        return this;
    }
    
    public Structure addIngredientElementSupplier(char key, @NotNull Supplier<? extends SlotElement> elementSupplier) {
        if (ingredientList != null) throw new UnsupportedOperationException("Structure is locked");
        ingredientMap.put(key, new Ingredient(elementSupplier));
        return this;
    }
    
    public IngredientList getIngredientList() {
        if (ingredientList != null) return ingredientList;
        
        HashMap<Character, Ingredient> ingredients = new HashMap<>(globalIngredientMap);
        ingredients.putAll(this.ingredientMap);
        return ingredientList = new IngredientList(structureData, ingredients);
    }
    
    @Override
    public Structure clone() {
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
