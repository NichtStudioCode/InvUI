package xyz.xenondevs.invui.gui;

import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;
import org.jetbrains.annotations.Unmodifiable;
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
    private final Char2ObjectMap<List<Slot>> slots;
    private final Map<Marker, List<Slot>> markedSlots;
    private final List<Slot> contentListSlots;
    
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
    IngredientMatrix(int width, int height, String structure, Map<Character, Ingredient> ingredientMap) {
        if (structure.length() != width * height)
            throw new IllegalArgumentException("Length of structure does not match width * height");
        
        this.width = width;
        this.height = height;
        this.structure = structure;
        this.slotElements = new SlotElement[structure.length()];
        this.slots = new Char2ObjectOpenHashMap<>();
        this.markedSlots = new HashMap<>();
        
        // populate slots map (ingredient key -> slots)
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int i = y * width + x;
                char key = structure.charAt(i);
                
                slots.computeIfAbsent(key, k -> new ArrayList<>()).add(new Slot(x, y));
            }
        }
        
        // generate slot elements
        for (var entry : slots.char2ObjectEntrySet()) {
            char key = entry.getCharKey();
            Ingredient ingredient = ingredientMap.get(key);
            if (!(ingredient instanceof Ingredient.Element(SlotElementSupplier supplier)))
                continue;
            
            List<Slot> slotsForKey = entry.getValue();
            var slotElements = supplier.generateSlotElements(slotsForKey);
            for (int i = 0; i < slotsForKey.size(); i++) {
                var slot = slotsForKey.get(i);
                this.slotElements[slot.y() * width + slot.x()] = slotElements.get(i);
            }
        }
        
        // generate marked slots map (marker -> slots) and aggregate content list slots
        var horizontal = findMarkedSlots(Markers.CONTENT_LIST_SLOT_HORIZONTAL, width, height, structure, ingredientMap);
        var vertical = findMarkedSlots(Markers.CONTENT_LIST_SLOT_VERTICAL, width, height, structure, ingredientMap);
        this.markedSlots.put(Markers.CONTENT_LIST_SLOT_HORIZONTAL, horizontal);
        this.markedSlots.put(Markers.CONTENT_LIST_SLOT_VERTICAL, vertical);
        this.contentListSlots = new ArrayList<>(horizontal.size() + vertical.size());
        this.contentListSlots.addAll(horizontal);
        this.contentListSlots.addAll(vertical);
    }
    
    private static List<Slot> findMarkedSlots(Marker marker, int width, int height, String structure, Map<Character, Ingredient> ingredientMap) {
        var slots = new ArrayList<Slot>();
        marker.iterate(width, height, (x, y) -> {
            Ingredient ingredient = ingredientMap.get(structure.charAt(y * width + x));
            if (ingredient instanceof Ingredient.Marker(Marker m) && m == marker)
                slots.add(new Slot(x, y));
        });
        return slots;
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
    
    @Unmodifiable
    List<Slot> getContentListSlots() {
        return Collections.unmodifiableList(contentListSlots);
    }
    
    @Unmodifiable
    List<Slot> getSlots(Marker marker) {
        return Collections.unmodifiableList(markedSlots.getOrDefault(marker, List.of()));
    }
    
    /**
     * Gets an unmodifiable view of all {@link Slot Slots} related to the given key,
     * in order left-to-right, top-to-bottom.
     *
     * @param key the key
     * @return a collection of slots for the given key
     */
    @Unmodifiable
    List<Slot> getSlots(char key) {
        return Collections.unmodifiableList(slots.getOrDefault(key, List.of()));
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
