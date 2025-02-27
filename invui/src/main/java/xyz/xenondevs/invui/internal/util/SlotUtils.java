package xyz.xenondevs.invui.internal.util;

import xyz.xenondevs.invui.gui.Slot;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public class SlotUtils {
    
    public static Set<Integer> getSlotsRow(int row, int width) {
        Set<Integer> slots = new LinkedHashSet<>();
        for (int x = 0; x < width; x++) slots.add(convertToIndex(x, row, width));
        return slots;
    }
    
    public static Set<Integer> getSlotsColumn(int column, int width, int height) {
        Set<Integer> slots = new LinkedHashSet<>();
        for (int y = 0; y < height; y++) slots.add(convertToIndex(column, y, width));
        return slots;
    }
    
    public static Set<Integer> getSlotsBorders(int width, int height) {
        Set<Integer> slots = new LinkedHashSet<>();
        if (height > 0) slots.addAll(getSlotsRow(0, width));
        if (height - 1 > 0) slots.addAll(getSlotsRow(height - 1, width));
        if (width > 0) slots.addAll(getSlotsColumn(0, width, height));
        if (width - 1 > 0) slots.addAll(getSlotsColumn(width - 1, width, height));
        return slots;
    }
    
    public static Set<Integer> getSlotsRect(int x, int y, int width, int height, int frameWidth) {
        return getSlotsRect(Order.HORIZONTAL, x, y, width, height, frameWidth);
    }
    
    public static Set<Integer> getSlotsRect(Order order, int x, int y, int width, int height, int frameWidth) {
        Set<Integer> slots = new LinkedHashSet<>();
        
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
    
    public static Slot convertFromIndex(int index, int width) {
        return new Slot(index % width, index / width);
    }
    
    public static int getLongestLineLength(int[] slots, int frameWidth) {
        int longestLength = 0;
        int currentLength = 1;
        Slot previous = null;
        
        for (Slot point : Arrays.stream(slots)
            .mapToObj(index -> convertFromIndex(index, frameWidth))
            .toList()) {
            if (previous != null) {
                if (isNeighbor(previous, point)) currentLength++;
                else currentLength = 1;
            }
            
            previous = point;
            longestLength = Math.max(longestLength, currentLength);
        }
        
        return longestLength;
    }
    
    public static boolean isNeighbor(Slot point1, Slot point2) {
        return Math.abs(point1.x() - point2.x()) + Math.abs(point1.y() - point2.y()) == 1;
    }
    
    public static int[] toSlotIndices(Slot[] slots, int width) {
        int[] indices = new int[slots.length];
        for (int i = 0; i < slots.length; i++) {
            Slot slot = slots[i];
            indices[i] = convertToIndex(slot.x(), slot.y(), width);
        }
        return indices;
    }
    
    public enum Order {
        
        HORIZONTAL,
        VERTICAL
        
    }
    
}
