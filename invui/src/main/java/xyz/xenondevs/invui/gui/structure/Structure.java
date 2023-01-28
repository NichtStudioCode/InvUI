package xyz.xenondevs.invui.gui.structure;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.SlotElement;
import xyz.xenondevs.invui.gui.SlotElement.ItemSlotElement;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.ItemWrapper;
import xyz.xenondevs.invui.item.impl.SimpleItem;
import xyz.xenondevs.invui.virtualinventory.VirtualInventory;

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
    
    public Structure(String... structureData) {
        this(sanitize(structureData[0]).length(), structureData.length, String.join("", structureData));
    }
    
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
    
    public static void addGlobalIngredient(char key, @NotNull Marker marker) {
        globalIngredientMap.put(key, new Ingredient(marker));
    }
    
    public static void addGlobalIngredientElementSupplier(char key, @NotNull Supplier<? extends SlotElement> elementSupplier) {
        globalIngredientMap.put(key, new Ingredient(elementSupplier));
    }
    
    public Structure addIngredient(char key, @NotNull ItemStack itemStack) {
        if (ingredientList != null) throw new IllegalStateException("Structure is locked");
        return addIngredient(key, new ItemWrapper(itemStack));
    }
    
    public Structure addIngredient(char key, @NotNull ItemProvider itemProvider) {
        if (ingredientList != null) throw new IllegalStateException("Structure is locked");
        return addIngredient(key, new SimpleItem(itemProvider));
    }
    
    public Structure addIngredient(char key, @NotNull Item item) {
        if (ingredientList != null) throw new IllegalStateException("Structure is locked");
        return addIngredient(key, new ItemSlotElement(item));
    }
    
    public Structure addIngredient(char key, @NotNull VirtualInventory inventory) {
        if (ingredientList != null) throw new IllegalStateException("Structure is locked");
        return addIngredientElementSupplier(key, new VISlotElementSupplier(inventory));
    }
    
    public Structure addIngredient(char key, @NotNull VirtualInventory inventory, @Nullable ItemProvider background) {
        if (ingredientList != null) throw new IllegalStateException("Structure is locked");
        return addIngredientElementSupplier(key, new VISlotElementSupplier(inventory, background));
    }
    
    public Structure addIngredient(char key, @NotNull SlotElement element) {
        if (ingredientList != null) throw new IllegalStateException("Structure is locked");
        ingredientMap.put(key, new Ingredient(element));
        return this;
    }
    
    public Structure addIngredient(char key, @NotNull Marker marker) {
        if (ingredientList != null) throw new IllegalStateException("Structure is locked");
        ingredientMap.put(key, new Ingredient(marker));
        return this;
    }
    
    public Structure addIngredient(char key, @NotNull Supplier<? extends Item> itemSupplier) {
        if (ingredientList != null) throw new IllegalStateException("Structure is locked");
        ingredientMap.put(key, new Ingredient(() -> new ItemSlotElement(itemSupplier.get())));
        return this;
    }
    
    public Structure addIngredientElementSupplier(char key, @NotNull Supplier<? extends SlotElement> elementSupplier) {
        if (ingredientList != null) throw new IllegalStateException("Structure is locked");
        ingredientMap.put(key, new Ingredient(elementSupplier));
        return this;
    }
    
    public IngredientList getIngredientList() {
        if (ingredientList != null) return ingredientList;
        
        HashMap<Character, Ingredient> ingredients = new HashMap<>(globalIngredientMap);
        ingredients.putAll(this.ingredientMap);
        return ingredientList = new IngredientList(width, height, structureData, ingredients);
    }
    
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
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
