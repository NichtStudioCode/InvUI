package de.studiocode.invui.util;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class SlotUtils {
    
    @NotNull
    public static Set<Integer> getSlotsRow(int row, int width) {
        Set<Integer> slots = new LinkedHashSet<>();
        for (int x = 0; x < width; x++) slots.add(convertToIndex(x, row, width));
        return slots;
    }
    
    @NotNull
    public static Set<Integer> getSlotsColumn(int column, int width, int height) {
        Set<Integer> slots = new LinkedHashSet<>();
        for (int y = 0; y < height; y++) slots.add(convertToIndex(column, y, width));
        return slots;
    }
    
    @NotNull
    public static Set<Integer> getSlotsBorders(int width, int height) {
        Set<Integer> slots = new LinkedHashSet<>();
        if (height > 0) slots.addAll(getSlotsRow(0, width));
        if (height - 1 > 0) slots.addAll(getSlotsRow(height - 1, width));
        if (width > 0) slots.addAll(getSlotsColumn(0, width, height));
        if (width - 1 > 0) slots.addAll(getSlotsColumn(width - 1, width, height));
        return slots;
    }
    
    @NotNull
    public static Set<Integer> getSlotsRect(int x, int y, int width, int height, int frameWidth) {
        return getSlotsRect(Order.HORIZONTAL, x, y, width, height, frameWidth);
    }
    
    @NotNull
    public static Set<Integer> getSlotsRect(@NotNull Order order, int x, int y, int width, int height, int frameWidth) {
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
    
    public static int convertToIndex(@NotNull Point2D point, int width) {
        return convertToIndex(point.getX(), point.getY(), width);
    }
    
    public static int convertToIndex(int x, int y, int width) {
        return y * width + x;
    }
    
    @NotNull
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
    
    public static int getLongestLineLength(int[] slots, int frameWidth) {
        int longestLength = 0;
        int currentLength = 1;
        Point2D previous = null;
        
        for (Point2D point : Arrays.stream(slots)
            .mapToObj(index -> convertFromIndex(index, frameWidth))
            .collect(Collectors.toList())) {
            if (previous != null) {
                if (isNeighbor(previous, point)) currentLength++;
                else currentLength = 1;
            }
            
            previous = point;
            longestLength = Math.max(longestLength, currentLength);
        }
        
        return longestLength;
    }
    
    public static boolean isNeighbor(@NotNull Point2D point1, @NotNull Point2D point2) {
        return Math.abs(point1.getX() - point2.getX()) + Math.abs(point1.getY() - point2.getY()) == 1;
    }
    
    public enum Order {
        
        HORIZONTAL,
        VERTICAL
        
    }
    
}
