package xyz.xenondevs.invui.internal.util;

import xyz.xenondevs.invui.gui.Slot;

import java.util.LinkedHashSet;
import java.util.SequencedSet;

public class SlotUtils {
    
    public static SequencedSet<Integer> getSlotsRow(int row, int width) {
        var slots = new LinkedHashSet<Integer>();
        for (int x = 0; x < width; x++) slots.add(convertToIndex(x, row, width));
        return slots;
    }
    
    public static SequencedSet<Integer> getSlotsColumn(int column, int width, int height) {
        var slots = new LinkedHashSet<Integer>();
        for (int y = 0; y < height; y++) slots.add(convertToIndex(column, y, width));
        return slots;
    }
    
    public static SequencedSet<Integer> getSlotsBorders(int width, int height) {
        var slots = new LinkedHashSet<Integer>();
        if (height > 0) slots.addAll(getSlotsRow(0, width));
        if (height - 1 > 0) slots.addAll(getSlotsRow(height - 1, width));
        if (width > 0) slots.addAll(getSlotsColumn(0, width, height));
        if (width - 1 > 0) slots.addAll(getSlotsColumn(width - 1, width, height));
        return slots;
    }
    
    public static SequencedSet<Integer> getSlotsRect(int x, int y, int width, int height, int frameWidth) {
        return getSlotsRect(Order.HORIZONTAL, x, y, width, height, frameWidth);
    }
    
    public static SequencedSet<Integer> getSlotsRect(Order order, int x, int y, int width, int height, int frameWidth) {
        var slots = new LinkedHashSet<Integer>();
        
        switch (order) {
            case HORIZONTAL:
                for (int y1 = y; y1 < height + y; y1++) {
                    for (int x1 = x; x1 < width + x; x1++) {
                        slots.add(convertToIndex(x1, y1, frameWidth));
                    }
                }
                break;
            
            case VERTICAL:
                for (int x1 = x; x1 < width + x; x1++) {
                    for (int y1 = y; y1 < height + y; y1++) {
                        slots.add(convertToIndex(x1, y1, frameWidth));
                    }
                }
                break;
        }
        
        return slots;
    }
    
    public static int convertToIndex(int x, int y, int width) {
        return y * width + x;
    }
    
    /**
     * Converts the given slot array to a slot indices array.
     *
     * @param slots the slots
     * @param width the width of the flattened 2D array
     * @return the slot indices
     */
    public static int[] toSlotIndices(Slot[] slots, int width) {
        int[] indices = new int[slots.length];
        for (int i = 0; i < slots.length; i++) {
            Slot slot = slots[i];
            indices[i] = convertToIndex(slot.x(), slot.y(), width);
        }
        return indices;
    }
    
    /**
     * Converts the given slot set to a slot indices array.
     *
     * @param slots the slots
     * @param width the width of the flattened 2D array
     * @return the slot indices
     */
    public static int[] toSlotIndices(SequencedSet<? extends Slot> slots, int width) {
        int[] indices = new int[slots.size()];
        int i = 0;
        for (Slot slot : slots) {
            indices[i++] = convertToIndex(slot.x(), slot.y(), width);
        }
        return indices;
    }
    
    /**
     * Converts the given slot indices array to a sequenced slot set.
     *
     * @param slots the slot indices
     * @param width the width of the flattened 2D array
     * @return the sequenced slot set
     */
    public static SequencedSet<Slot> toSlotSet(int[] slots, int width) {
        var set = new LinkedHashSet<Slot>();
        for (int i : slots) {
            set.add(new Slot(i % width, i / width));
        }
        return set;
    }
    
    /**
     * Converts the given slot set to a slot indices set.
     *
     * @param slots the slots
     * @param width the width of the flattened 2D array
     * @return the slot indices set
     */
    public static SequencedSet<Integer> toSlotIndicesSet(SequencedSet<? extends Slot> slots, int width) {
        var set = new LinkedHashSet<Integer>();
        for (Slot slot : slots) {
            set.add(convertToIndex(slot.x(), slot.y(), width));
        }
        return set;
    }
    
    /**
     * Finds the length of the horizontal lines in the given slots, assuming slots is a flattened 2D array with the
     * given width. If there are differing line lengths, an exception is thrown.
     *
     * @param slots the slots
     * @param width the width of the 2D array
     * @return the length of the longest horizontal line
     * @throws IllegalArgumentException if there are multiple horizontal lines with differing lengths
     */
    public static int determineHorizontalLinesLength(SequencedSet<? extends Integer> slots, int width) {
        int longestLineLength = -1;
        int currentLineLength = 0;
        int previous = -1;
        
        for (int slot : slots) {
            // is first slot || (slots are one unit apart && on the same line)
            if (previous == -1 || (Math.abs(previous - slot) == 1 && (previous / width) == (slot / width))) {
                currentLineLength++;
            } else {
                // line ended
                if (longestLineLength != -1 && longestLineLength != currentLineLength)
                    throw new IllegalArgumentException("Differing line lengths");
                
                longestLineLength = currentLineLength;
                currentLineLength = 1;
            }
            
            previous = slot;
        }
        
        // line ended
        if (longestLineLength != -1 && longestLineLength != currentLineLength)
            throw new IllegalArgumentException("Differing line lengths");
        
        return currentLineLength;
    }
    
    /**
     * Finds the length of the vertical lines in the given slots, assuming slots is a flattened 2D array with the given
     * width. If there are differing line lengths, an exception is thrown.
     *
     * @param slots the slots
     * @param width the width of the 2D array
     * @return the length of the longest vertical line
     * @throws IllegalArgumentException if there are multiple vertical lines with differing lengths
     */
    public static int determineVerticalLinesLength(SequencedSet<? extends Integer> slots, int width) {
        int longestLineLength = -1;
        int currentLineLength = 0;
        int previous = -1;
        
        for (int slot : slots) {
            // is first slot || (slots are on same x && slots are one line apart)
            if (previous == -1 || ((previous % width) == (slot % width) && Math.abs((previous / width) - (slot / width)) == 1)) {
                currentLineLength++;
            } else {
                // line ended
                if (longestLineLength != -1 && longestLineLength != currentLineLength)
                    throw new IllegalArgumentException("Differing line lengths");
                
                longestLineLength = currentLineLength;
                currentLineLength = 1;
            }
            
            previous = slot;
        }
        
        // line ended
        if (longestLineLength != -1 && longestLineLength != currentLineLength)
            throw new IllegalArgumentException("Differing line lengths");
        
        return currentLineLength;
    }
    
    public enum Order {
        
        HORIZONTAL,
        VERTICAL
        
    }
    
}
