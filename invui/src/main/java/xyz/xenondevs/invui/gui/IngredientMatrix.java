package xyz.xenondevs.invui.gui;

import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import org.jspecify.annotations.Nullable;

import java.util.*;

/**
 * Immutable matrix of {@link SlotElement SlotElements} and {@link Marker Markers}.
 */
class IngredientMatrix {
    
    private final int width;
    private final int height;
    private final String structure;
    private final @Nullable SlotElement[] slotElements;
    private final @Nullable Marker[] markers;
    private final Char2ObjectMap<List<Slot>> slots;
    
    /**
     * Creates a new {@link IngredientMatrix} with the given width, height, structure, and ingredient map.
     * This also invokes ingredient slot element suppliers, if there are any.
     *
     * @param width         the width
     * @param height        the height
     * @param structure     a string of characters of length width*height that represents the order of ingredients
     * @param ingredientMap maps characters from the structure string to ingredients
     * @throws IllegalArgumentException if the length of the structure does not match width * height
     */
    IngredientMatrix(int width, int height, String structure, HashMap<Character, Ingredient> ingredientMap) {
        if (structure.length() != width * height)
            throw new IllegalArgumentException("Length of structure does not match width * height");
        
        this.width = width;
        this.height = height;
        this.structure = structure;
        this.slotElements = new SlotElement[structure.length()];
        this.markers = new Marker[structure.length()];
        this.slots = new Char2ObjectOpenHashMap<>();
        
        // for loop order is important to invoke slot element suppliers left-to-right, top-to-bottom
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int i = y * width + x;
                
                char key = structure.charAt(i);
                
                var ingredient = ingredientMap.get(key);
                if (ingredient != null) {
                    slotElements[i] = ingredient.getSlotElement();
                    markers[i] = ingredient.getMarker();
                }
                
                slots.computeIfAbsent(key, ArrayList::new).add(new Slot(x, y));
            }
        }
    }
    
    /**
     * Gets the ingredient key at the given index.
     *
     * @param i the index
     * @return the ingredient key at the given index
     * @throws ArrayIndexOutOfBoundsException if the index is out of bounds
     */
    char getKey(int i) {
        return structure.charAt(i);
    }
    
    /**
     * Gets the ingredient key at the given coordinates.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @return the ingredient key at the given coordinates
     * @throws IndexOutOfBoundsException if the coordinates are out of bounds
     */
    char getKey(int x, int y) {
        checkBounds(x, y);
        return structure.charAt(y * width + x);
    }
    
    /**
     * Gets the {@link SlotElement} at the given index.
     *
     * @param i the index
     * @return the slot element at the given index
     * @throws ArrayIndexOutOfBoundsException if the index is out of bounds
     */
    @Nullable
    SlotElement getSlotElement(int i) {
        return slotElements[i];
    }
    
    /**
     * Gets the {@link SlotElement} at the given coordinates.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @return the slot element at the given coordinates
     * @throws IndexOutOfBoundsException if the coordinates are out of bounds
     */
    @Nullable
    SlotElement getSlotElement(int x, int y) {
        checkBounds(x, y);
        return slotElements[y * width + x];
    }
    
    /**
     * Gets the {@link Marker} at the given index.
     *
     * @param i the index
     * @return the marker at the given index
     * @throws ArrayIndexOutOfBoundsException if the index is out of bounds
     */
    @Nullable
    Marker getMarker(int i) {
        return markers[i];
    }
    
    /**
     * Gets the {@link Marker} at the given coordinates.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @return the marker at the given coordinates
     * @throws IndexOutOfBoundsException if the coordinates are out of bounds
     */
    @Nullable
    Marker getMarker(int x, int y) {
        checkBounds(x, y);
        return markers[y * width + x];
    }
    
    /**
     * Finds all indices of the given marker, in the order defined by the marker.
     *
     * @param marker the marker to find
     * @return an array of indices of the marker
     */
    int[] findIndices(Marker marker) {
        var indices = new IntArrayList();
        marker.iterate(width, height, (x, y) -> {
            if (getMarker(x, y) == marker)
                indices.add(y * width + x);
        });
        return indices.toIntArray();
    }
    
    /**
     * Finds all indices of {@link Markers#CONTENT_LIST_SLOT_HORIZONTAL} and {@link Markers#CONTENT_LIST_SLOT_VERTICAL}.
     *
     * @return an array of indices of the content list slots
     */
    int[] findContentListSlots() {
        int[] horizontal = findIndices(Markers.CONTENT_LIST_SLOT_HORIZONTAL);
        int[] vertical = findIndices(Markers.CONTENT_LIST_SLOT_VERTICAL);
        int[] indices = new int[horizontal.length + vertical.length];
        System.arraycopy(horizontal, 0, indices, 0, horizontal.length);
        System.arraycopy(vertical, 0, indices, horizontal.length, vertical.length);
        return indices;
    }
    
    /**
     * Gets an unmodifiable view of all {@link Slot Slots} related to the given key,
     * in order left-to-right, top-to-bottom.
     *
     * @param key the key
     * @return a collection of slots for the given key
     */
    SequencedCollection<? extends Slot> getSlots(char key) {
        return Collections.unmodifiableList(slots.getOrDefault(key, Collections.emptyList()));
    }
    
    /**
     * Checks if the given coordinates are within the bounds of the matrix.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @throws IndexOutOfBoundsException if the coordinates are out of bounds
     */
    private void checkBounds(int x, int y) {
        if (x < 0 || x >= width)
            throw new IndexOutOfBoundsException("x coordinate " + x + " is out of bounds for width " + width);
        if (y < 0 || y >= height)
            throw new IndexOutOfBoundsException("y coordinate " + y + " is out of bounds for height " + height);
    }
    
}
