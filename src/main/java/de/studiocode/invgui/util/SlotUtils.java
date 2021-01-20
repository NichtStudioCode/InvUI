package de.studiocode.invgui.util;

import java.util.SortedSet;
import java.util.TreeSet;

public class SlotUtils {
    
    public static SortedSet<Integer> getSlotsRow(int row, int width) {
        SortedSet<Integer> slots = new TreeSet<>();
        for (int x = 0; x < width; x++) slots.add(convertToIndex(x, row, width));
        return slots;
    }
    
    public static SortedSet<Integer> getSlotsColumn(int column, int width, int height) {
        SortedSet<Integer> slots = new TreeSet<>();
        for (int y = 0; y < height; y++) slots.add(convertToIndex(column, y, width));
        return slots;
    }
    
    public static SortedSet<Integer> getSlotsBorders(int width, int height) {
        SortedSet<Integer> slots = new TreeSet<>();
        if (height > 0) slots.addAll(getSlotsRow(0, width));
        if (height - 1 > 0) slots.addAll(getSlotsRow(height - 1, width));
        if (width > 0) slots.addAll(getSlotsColumn(0, width, height));
        if (width - 1 > 0) slots.addAll(getSlotsColumn(width - 1, width, height));
        return slots;
    }
    
    public static SortedSet<Integer> getSlotsRect(int x, int y, int width, int height, int frameWidth) {
        SortedSet<Integer> slots = new TreeSet<>();
        for (int y1 = y; y1 <= height; y1++) {
            for (int x1 = x; x1 <= width; x1++) {
                slots.add(convertToIndex(x1, y1, frameWidth));
            }
        }
        
        return slots;
    }
    
    public static int convertToIndex(int x, int y, int width) {
        return y * width + x;
    }
    
}
