package de.studiocode.invui.gui.builder;

import de.studiocode.invui.gui.GUI;
import de.studiocode.invui.gui.SlotElement;
import de.studiocode.invui.gui.impl.SimpleGUI;
import de.studiocode.invui.item.Item;
import org.bukkit.inventory.ShapedRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A builder class to easily construct {@link GUI}s.<br>
 * It provides similar functionality to Bukkit's {@link ShapedRecipe}, as it
 * allows for a structure String which defines the layout of the {@link GUI}.
 */
public class GUIBuilder {
    
    protected final int width;
    protected final int height;
    private final int size;
    
    private final Map<Character, Ingredient> ingredientMap = new HashMap<>();
    
    private String structure;
    
    public GUIBuilder(int width, int height) {
        this.width = width;
        this.height = height;
        this.size = width * height;
    }
    
    /**
     * Builds the {@link GUI}.
     *
     * @return The built {@link GUI}
     */
    public GUI build() {
        SimpleGUI gui = new SimpleGUI(width, height);
        setSlotElements(gui);
        return gui;
    }
    
    /**
     * Sets the structure of the {@link GUI}.
     * The structure is a {@link String} of characters, like used in {@link ShapedRecipe} to
     * define where which {@link Item} should be.
     *
     * @param structure The structure {@link String}
     */
    public GUIBuilder setStructure(@NotNull String structure) {
        String cleanedStructure = structure.replace(" ", "").replace("\n", "");
        if (cleanedStructure.length() != size) throw new IllegalArgumentException("Structure length does not match size");
        this.structure = cleanedStructure;
        
        return this;
    }
    
    /**
     * Sets an ingredient for the structure String, which will later be
     * used to set up the inventory correctly.
     *
     * @param key  The ingredient key
     * @param item The {@link Item}
     */
    public GUIBuilder setIngredient(char key, @NotNull Item item) {
        ingredientMap.put(key, new Ingredient(item));
        return this;
    }
    
    /**
     * Sets an ingredient for the structure String, which will later be
     * used to set up the inventory correctly.
     *
     * @param key         The ingredient key
     * @param slotElement The {@link SlotElement}
     */
    public GUIBuilder setIngredient(char key, @NotNull SlotElement slotElement) {
        ingredientMap.put(key, new Ingredient(slotElement));
        return this;
    }
    
    protected void setIngredient(char key, int special) {
        ingredientMap.put(key, new Ingredient(special));
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
    
    protected void setSlotElements(GUI gui) {
        Ingredient[] ingredients = parseStructure();
        for (int i = 0; i < gui.getSize(); i++) {
            Ingredient ingredient = ingredients[i];
            if (ingredient != null && ingredient.isSlotElement())
                gui.setSlotElement(i, ingredient.getSlotElement());
        }
    }
    
    protected int[] findIndicesOf(int special) {
        List<Integer> indices = new ArrayList<>();
        
        Ingredient[] ingredients = parseStructure();
        for (int i = 0; i < size; i++) {
            Ingredient ingredient = ingredients[i];
            if (ingredient != null && ingredient.isSpecial() && ingredient.getSpecial() == special)
                indices.add(i);
        }
        
        return indices.stream().mapToInt(Integer::intValue).toArray();
    }
    
    static class Ingredient {
        
        private final SlotElement slotElement;
        private final int special;
        
        public Ingredient(Item item) {
            this.slotElement = new SlotElement.ItemSlotElement(item);
            this.special = -1;
        }
        
        public Ingredient(SlotElement slotElement) {
            this.slotElement = slotElement;
            this.special = -1;
        }
        
        public Ingredient(int special) {
            this.special = special;
            this.slotElement = null;
        }
        
        public SlotElement getSlotElement() {
            if (isSpecial()) throw new IllegalStateException("Ingredient is special");
            return slotElement;
        }
        
        public int getSpecial() {
            if (isSlotElement()) throw new IllegalStateException("Ingredient is item");
            return special;
        }
        
        public boolean isSlotElement() {
            return slotElement != null;
        }
        
        public boolean isSpecial() {
            return slotElement == null;
        }
        
    }
    
}
