package de.studiocode.invui.gui.builder;

import de.studiocode.invui.gui.GUI;
import de.studiocode.invui.gui.SlotElement;
import de.studiocode.invui.gui.SlotElement.ItemSlotElement;
import de.studiocode.invui.gui.impl.*;
import de.studiocode.invui.item.Item;
import de.studiocode.invui.item.impl.SimpleItem;
import de.studiocode.invui.item.itembuilder.ItemBuilder;
import org.bukkit.inventory.ShapedRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static de.studiocode.invui.gui.builder.GUIType.*;

/**
 * A builder class to easily construct {@link GUI}s.<br>
 * It provides similar functionality to Bukkit's {@link ShapedRecipe}, as it
 * allows for a structure String which defines the layout of the {@link GUI}.
 */
public class GUIBuilder {
    
    private final GUIType guiType;
    private final int width;
    private final int height;
    private final HashMap<Character, Ingredient> ingredientMap = new HashMap<>();
    
    private String structure;
    
    private List<Item> items = new ArrayList<>();
    private List<GUI> guis = new ArrayList<>();
    
    public GUIBuilder(@NotNull GUIType guiType, int width, int height) {
        this.guiType = guiType;
        this.width = width;
        this.height = height;
    }
    
    public GUIBuilder setStructure(@NotNull String structure) {
        String trimmedStructure = structure
            .replace(" ", "")
            .replace("\n", "");
        
        if (trimmedStructure.length() != width * height)
            throw new IllegalArgumentException("Structure size does not match GUI size");
        
        this.structure = trimmedStructure;
        return this;
    }
    
    public GUIBuilder setIngredient(char key, @NotNull ItemBuilder itemBuilder) {
        return setIngredient(key, new SimpleItem(itemBuilder));
    }
    
    public GUIBuilder setIngredient(char key, @NotNull Item item) {
        return setIngredient(key, new ItemSlotElement(item));
    }
    
    public GUIBuilder setIngredient(char key, @NotNull SlotElement element) {
        ingredientMap.put(key, new Ingredient(element));
        return this;
    }
    
    public GUIBuilder setMarker(char key, @NotNull Marker marker) {
        ingredientMap.put(key, new Ingredient(marker));
        return this;
    }
    
    public GUIBuilder setItems(@NotNull List<Item> items) {
        if (guiType != PAGED_ITEMS && guiType != SCROLL)
            throw new UnsupportedOperationException("Items cannot be set in this gui type.");
        this.items = items;
        return this;
    }
    
    public GUIBuilder addItem(@NotNull Item item) {
        if (guiType != PAGED_ITEMS && guiType != SCROLL)
            throw new UnsupportedOperationException("Items cannot be set in this gui type.");
        items.add(item);
        return this;
    }
    
    public GUIBuilder setGUIs(@NotNull List<GUI> guis) {
        if (guiType != PAGED_GUIs)
            throw new UnsupportedOperationException("GUIs cannot be set in this gui type.");
        this.guis = guis;
        return this;
    }
    
    public GUIBuilder addGUI(@NotNull GUI gui) {
        if (guiType != PAGED_GUIs)
            throw new UnsupportedOperationException("GUIs cannot be set in this gui type.");
        guis.add(gui);
        return this;
    }
    
    public GUI build() {
        IngredientList ingredients = new IngredientList(structure, ingredientMap);
        
        switch (guiType) {
            
            case NORMAL:
                return buildSimpleGUI(ingredients);
            
            case PAGED_ITEMS:
                return buildSimplePagedItemsGUI(ingredients);
            
            case PAGED_GUIs:
                return buildSimplePagedGUIsGUI(ingredients);
            
            case TAB:
                return buildSimpleTabGUI(ingredients);
            
            case SCROLL:
                return buildSimpleScrollGUI(ingredients);
            
            default:
                throw new UnsupportedOperationException("Unknown GUI type");
        }
    }
    
    private SimpleGUI buildSimpleGUI(IngredientList ingredients) {
        SimpleGUI gui = new SimpleGUI(width, height);
        ingredients.insertIntoGUI(gui);
        
        return gui;
    }
    
    private SimplePagedItemsGUI buildSimplePagedItemsGUI(IngredientList ingredients) {
        SimplePagedItemsGUI gui = new SimplePagedItemsGUI(width, height, items, ingredients.findIndicesOfMarkerAsArray(Marker.ITEM_LIST_SLOT));
        ingredients.insertIntoGUI(gui);
        
        return gui;
    }
    
    private SimplePagedGUIsGUI buildSimplePagedGUIsGUI(IngredientList ingredients) {
        SimplePagedGUIsGUI gui = new SimplePagedGUIsGUI(width, height, guis, ingredients.findIndicesOfMarkerAsArray(Marker.ITEM_LIST_SLOT));
        ingredients.insertIntoGUI(gui);
    
        return gui;
    }
    
    private SimpleTabGUI buildSimpleTabGUI(IngredientList ingredients) {
        SimpleTabGUI gui = new SimpleTabGUI(width, height, guis, ingredients.findIndicesOfMarkerAsArray(Marker.ITEM_LIST_SLOT));
        ingredients.insertIntoGUI(gui);
        
        return gui;
    }
    
    private SimpleScrollGUI buildSimpleScrollGUI(IngredientList ingredients) {
        SimpleScrollGUI gui = new SimpleScrollGUI(width, height, items, ingredients.findIndicesOfMarkerAsArray(Marker.ITEM_LIST_SLOT));
        ingredients.insertIntoGUI(gui);
        
        return gui;
    }
    
}
