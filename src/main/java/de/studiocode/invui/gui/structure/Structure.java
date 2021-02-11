package de.studiocode.invui.gui.structure;

import de.studiocode.invui.gui.GUI;
import de.studiocode.invui.gui.SlotElement;
import de.studiocode.invui.gui.SlotElement.ItemSlotElement;
import de.studiocode.invui.item.Item;
import de.studiocode.invui.item.impl.SimpleItem;
import de.studiocode.invui.item.itembuilder.ItemBuilder;
import org.bukkit.inventory.ShapedRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.function.Supplier;

/**
 * Provides an easy way to design {@link GUI}s.
 * Inspired by Bukkit's {@link ShapedRecipe}, this class will let you
 * design a {@link GUI} in a similar way.
 */
public class Structure {
    
    private static final HashMap<Character, Ingredient> globalIngredientMap = new HashMap<>();
    
    private final HashMap<Character, Ingredient> ingredientMap = new HashMap<>();
    private final String structureData;
    
    public Structure(String structureData) {
        this.structureData = structureData
            .replace(" ", "")
            .replace("\n", "");
    }
    
    public static void addGlobalIngredient(char key, @NotNull ItemBuilder itemBuilder) {
        addGlobalIngredient(key, new SimpleItem(itemBuilder));
    }
    
    public static void addGlobalIngredient(char key, @NotNull Item item) {
        addGlobalIngredient(key, new ItemSlotElement(item));
    }
    
    public static void addGlobalIngredient(char key, @NotNull SlotElement element) {
        globalIngredientMap.put(key, new Ingredient(element));
    }
    
    public static void addGlobalIngredient(char key, @NotNull Marker marker) {
        globalIngredientMap.put(key, new Ingredient(marker));
    }
    
    public static void addGlobalIngredient(char key, @NotNull Supplier<Item> itemSupplier) {
        globalIngredientMap.put(key, new Ingredient(itemSupplier));
    }
    
    public Structure addIngredient(char key, @NotNull ItemBuilder itemBuilder) {
        return addIngredient(key, new SimpleItem(itemBuilder));
    }
    
    public Structure addIngredient(char key, @NotNull Item item) {
        return addIngredient(key, new ItemSlotElement(item));
    }
    
    public Structure addIngredient(char key, @NotNull SlotElement element) {
        ingredientMap.put(key, new Ingredient(element));
        return this;
    }
    
    public Structure addIngredient(char key, @NotNull Marker marker) {
        ingredientMap.put(key, new Ingredient(marker));
        return this;
    }
    
    public Structure addIngredient(char key, @NotNull Supplier<Item> itemSupplier) {
        ingredientMap.put(key, new Ingredient(itemSupplier));
        return this;
    }
    
    public IngredientList createIngredientList() {
        HashMap<Character, Ingredient> ingredients = new HashMap<>(globalIngredientMap);
        this.ingredientMap.forEach(ingredients::put);
        return new IngredientList(structureData, ingredients);
    }
    
}
