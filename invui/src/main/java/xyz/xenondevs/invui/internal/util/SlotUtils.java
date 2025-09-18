package xyz.xenondevs.invui.internal.util;

import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.Slot;
import xyz.xenondevs.invui.gui.SlotElement;

import java.util.*;

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
     * Converts the given slot indices array to a sequenced slot set.
     *
     * @param slots the slot indices
     * @param width the width of the flattened 2D array
     * @return the sequenced slot set
     */
    public static List<Slot> toSlotList(int[] slots, int width) {
        var set = new ArrayList<Slot>();
        for (int i : slots) {
            set.add(new Slot(i % width, i / width));
        }
        return set;
    }
    
    /**
     * Determines the length of the longest, possibly non-continuous vertical line of slots.
     *
     * @param slots the slots
     * @param width the width of the slot container, i.e. the maximum x value + 1
     * @return The length of the longest vertical line of slots
     */
    public static int determineLongestVerticalLineLength(Collection<? extends Slot> slots, int width) {
        int longestLength = 0;
        int[][] lineLengths = new int[width][2];
        for (int i = 0; i < width; i++) {
            lineLengths[i][0] = Integer.MAX_VALUE; // minY
            lineLengths[i][1] = Integer.MIN_VALUE; // maxY
        }
        for (Slot slot : slots) {
            int[] dim = lineLengths[slot.x()]; // [minY, maxY] on x
            
            if (slot.y() < dim[0])
                dim[0] = slot.y();
            if (slot.y() > dim[1])
                dim[1] = slot.y();
            
            int length = dim[1] - dim[0] + 1;
            if (length > longestLength)
                longestLength = length;
        }
        
        return longestLength;
    }
    
    /**
     * Determines the length of the longest, possibly non-continuous horizontal line of slots.
     *
     * @param slots  the slots
     * @param height the height of the slot container, i.e. the maximum y value + 1
     * @return The length of the longest horizontal line of slots
     */
    public static int determineLongestHorizontalLineLength(Collection<? extends Slot> slots, int height) {
        int longestLength = 0;
        int[][] lineLengths = new int[height][2];
        for (int i = 0; i < height; i++) {
            lineLengths[i][0] = Integer.MAX_VALUE; // minX
            lineLengths[i][1] = Integer.MIN_VALUE; // maxX
        }
        for (Slot slot : slots) {
            int[] range = lineLengths[slot.y()]; // [minX, maxX] on y
            
            if (slot.x() < range[0])
                range[0] = slot.x();
            if (slot.x() > range[1])
                range[1] = slot.x();
            
            int length = range[1] - range[0] + 1;
            if (length > longestLength)
                longestLength = length;
        }
        
        return longestLength;
    }
    
    /**
     * Creates a new {@link Slot} that is the minimum x and y of the given slots.
     *
     * @param slots the slots
     * @return the minimum slot
     */
    public static Slot min(Collection<? extends Slot> slots) {
        if (slots.isEmpty())
            return new Slot(0, 0);
        
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        
        for (Slot slot : slots) {
            if (slot.x() < minX) minX = slot.x();
            if (slot.y() < minY) minY = slot.y();
        }
        
        return new Slot(minX, minY);
    }
    
    /**
     * Creates a new {@link Slot} that is the maximum x and y of the given slots.
     *
     * @param slots the slots
     * @return the maximum slot
     */
    public static Slot max(Collection<? extends Slot> slots) {
        if (slots.isEmpty())
            return new Slot(0, 0);
        
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        
        for (Slot slot : slots) {
            if (slot.x() > maxX) maxX = slot.x();
            if (slot.y() > maxY) maxY = slot.y();
        }
        
        return new Slot(maxX, maxY);
    }
    
    /**
     * Creates a new {@link SlotElement.GuiLink} for the given coordinates in the given {@link Gui}, or returns null
     * if the coordinates are out of bounds.
     *
     * @param gui the gui
     * @param x   the x coordinate
     * @param y   the y coordinate
     * @return the gui link, or null if out of bounds
     */
    public static SlotElement.@Nullable GuiLink getGuiLinkOrNull(@Nullable Gui gui, int x, int y) {
        if (gui == null || x < 0 || y < 0 || x >= gui.getWidth() || y >= gui.getHeight())
            return null;
        return new SlotElement.GuiLink(gui, y * gui.getWidth() + x);
    }
    
    public enum Order {
        
        HORIZONTAL,
        VERTICAL
        
    }
    
}
