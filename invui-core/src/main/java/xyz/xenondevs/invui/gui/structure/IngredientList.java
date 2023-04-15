package xyz.xenondevs.invui.gui.structure;

import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.util.SlotUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

public class IngredientList extends ArrayList<Ingredient> {
    
    private final int width;
    private final int height;
    
    /**
     * Creates a new {@link IngredientList}.
     *
     * @param width         The width of the structure.
     * @param height        The height of the structure.
     * @param structure     The structure string.
     * @param ingredientMap The {@link HashMap} containing the {@link Ingredient Ingredients}.
     */
    public IngredientList(int width, int height, String structure, HashMap<Character, Ingredient> ingredientMap) {
        this.width = width;
        this.height = height;
        
        for (char c : structure.toCharArray()) {
            Ingredient ingredient = null;
            if (ingredientMap.containsKey(c)) ingredient = ingredientMap.get(c);
            add(ingredient);
        }
    }
    
    /**
     * Inserts the {@link Ingredient Ingredients} into the specified {@link Gui}.
     *
     * @param gui The {@link Gui}.
     */
    public void insertIntoGui(Gui gui) {
        if (size() != gui.getSize())
            throw new IllegalArgumentException("Structure size does not match Gui size");
        
        for (int i = 0; i < size(); i++) {
            Ingredient ingredient = get(i);
            if (ingredient != null && ingredient.isSlotElement())
                gui.setSlotElement(i, ingredient.getSlotElement());
        }
    }
    
    private List<Integer> findIndicesOfHorizontalMarker(Marker marker) {
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < size(); i++) {
            Ingredient ingredient = get(i);
            if (ingredient != null && ingredient.isMarker() && ingredient.getMarker() == marker)
                indices.add(i);
        }
        
        return indices;
    }
    
    
    private List<Integer> findIndicesOfVerticalMarker(Marker marker) {
        List<Integer> indices = new ArrayList<>();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int i = SlotUtils.convertToIndex(x, y, width);
                Ingredient ingredient = get(i);
                if (ingredient != null && ingredient.isMarker() && ingredient.getMarker() == marker)
                    indices.add(i);
            }
        }
        
        return indices;
    }
    
    /**
     * Finds all indices of the specified {@link Marker}.
     *
     * @param marker The {@link Marker}.
     * @return The indices.
     */
    public int[] findIndicesOfMarker(Marker marker) {
        List<Integer> indices;
        if (marker.isHorizontal()) {
            indices = findIndicesOfHorizontalMarker(marker);
        } else {
            indices = findIndicesOfVerticalMarker(marker);
        }
        
        return indices.stream().mapToInt(Integer::intValue).toArray();
    }
    
    /**
     * Finds all indices of the {@link Markers#CONTENT_LIST_SLOT_HORIZONTAL} and {@link Markers#CONTENT_LIST_SLOT_VERTICAL} {@link Marker Markers}.
     *
     * @return The indices.
     */
    public int[] findContentListSlots() {
        return Stream.concat(
            findIndicesOfHorizontalMarker(Markers.CONTENT_LIST_SLOT_HORIZONTAL).stream(),
            findIndicesOfVerticalMarker(Markers.CONTENT_LIST_SLOT_VERTICAL).stream()
        ).mapToInt(Integer::intValue).toArray();
    }
    
}
