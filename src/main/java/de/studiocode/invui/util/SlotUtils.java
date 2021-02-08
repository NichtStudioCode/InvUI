package de.studiocode.invui.util;

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
        }
        
        return slots;
    }
    
    public static int convertToIndex(Point2D point, int width) {
        return convertToIndex(point.getX(), point.getY(), width);
    }
    
    public static int convertToIndex(int x, int y, int width) {
        return y * width + x;
    }
    
    public static Point2D convertFromIndex(int index, int width) {
        return new Point2D(index % width, index / width);
    }
    
    public static int translatePlayerInvToGui(int slot) {
        if (slot > 8) return slot - 9;
        else return slot + 27;
    }
    
    public static int translateGuiToPlayerInv(int slot) {
        if (slot > 26) return slot - 27;
        else return slot + 9;
    }
    
    public enum Order {
        
        HORIZONTAL,
        VERTICAL
        
    }
    
}
