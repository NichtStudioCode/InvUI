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
    
    public IngredientList(int width, int height, String structure, HashMap<Character, Ingredient> ingredientMap) {
        this.width = width;
        this.height = height;
        
        for (char c : structure.toCharArray()) {
            Ingredient ingredient = null;
            if (ingredientMap.containsKey(c)) ingredient = ingredientMap.get(c);
            add(ingredient);
        }
    }
    
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
    
    
    public List<Integer> findIndicesOfVerticalMarker(Marker marker) {
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
    
    public int[] findIndicesOfMarker(Marker marker) {
        List<Integer> indices;
        if (marker.isHorizontal()) {
            indices = findIndicesOfHorizontalMarker(marker);
        } else {
            indices = findIndicesOfVerticalMarker(marker);
        }
        
        return indices.stream().mapToInt(Integer::intValue).toArray();
    }
    
    public int[] findContentListSlots() {
        return Stream.concat(
            findIndicesOfHorizontalMarker(Markers.CONTENT_LIST_SLOT_HORIZONTAL).stream(),
            findIndicesOfVerticalMarker(Markers.CONTENT_LIST_SLOT_VERTICAL).stream()
        ).mapToInt(Integer::intValue).toArray();
    }
    
}
