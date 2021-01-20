package de.studiocode.invgui.gui.builder.impl;

import de.studiocode.invgui.gui.GUI;
import de.studiocode.invgui.gui.builder.GUIBuilder;
import de.studiocode.invgui.item.Item;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseGUIBuilder implements GUIBuilder {
    
    private final int width;
    private final int height;
    private final int size;
    
    private final Map<Character, Ingredient> ingredientMap = new HashMap<>();
    
    private String structure;
    
    public BaseGUIBuilder(int width, int height) {
        this.width = width;
        this.height = height;
        this.size = width * height;
    }
    
    @Override
    public void setStructure(@NotNull String structure) {
        structure = structure.replace(" ", "");
        if (structure.length() != size) throw new IllegalArgumentException("Structure length does not match size");
        this.structure = structure;
    }
    
    @Override
    public void setIngredient(char key, @NotNull Item item) {
        ingredientMap.put(key, new Ingredient(item));
    }
    
    public void setIngredient(char key, int special) {
        ingredientMap.put(key, new Ingredient(special));
    }
    
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }
    
    private Ingredient[] parseStructure() {
        if (structure == null) throw new IllegalStateException("Structure has not been set yet.");
        
        Ingredient[] ingredients = new Ingredient[size];
        int i = 0;
        for (char c : structure.toCharArray()) {
            if (c != '.') ingredients[i] = this.ingredientMap.get(c);
            i++;
        }
        
        return ingredients;
    }
    
    protected void setItems(GUI gui) {
        Ingredient[] ingredients = parseStructure();
        for (int i = 0; i < gui.getSize(); i++) {
            Ingredient ingredient = ingredients[i];
            if (ingredient.isItem()) gui.setItem(i, ingredient.getItem());
        }
    }
    
    public List<Integer> findIndicesOf(int special) {
        List<Integer> indices = new ArrayList<>();
        
        Ingredient[] ingredients = parseStructure();
        for (int i = 0; i < size; i++) {
            Ingredient ingredient = ingredients[i];
            if (ingredient != null && ingredient.isSpecial() && ingredient.getSpecial() == special)
                indices.add(i);
        }
        
        return indices;
    }
    
    public abstract GUI build();
    
    static class Ingredient {
        
        private final Item item;
        private final int special;
        
        public Ingredient(Item item) {
            this.item = item;
            this.special = -1;
        }
        
        public Ingredient(int special) {
            this.special = special;
            this.item = null;
        }
        
        public Item getItem() {
            if (isSpecial()) throw new IllegalStateException("Ingredient is special");
            return item;
        }
        
        public int getSpecial() {
            if (isItem()) throw new IllegalStateException("Ingredient is item");
            return special;
        }
        
        public boolean isItem() {
            return item != null;
        }
        
        public boolean isSpecial() {
            return item == null;
        }
        
    }
    
}
