package de.studiocode.invgui.gui.impl;

import de.studiocode.invgui.gui.GUI;
import de.studiocode.invgui.gui.SlotElement;
import de.studiocode.invgui.gui.SlotElement.ItemStackHolder;
import de.studiocode.invgui.gui.SlotElement.LinkedSlotElement;
import de.studiocode.invgui.gui.SlotElement.VISlotElement;
import de.studiocode.invgui.item.Item;
import de.studiocode.invgui.util.SlotUtils;
import de.studiocode.invgui.virtualinventory.VirtualInventory;
import org.jetbrains.annotations.NotNull;

import java.util.SortedSet;

/**
 * A subclass of {@link IndexedGUI} which contains all the
 * coordinate-based methods as well as all filling methods.
 */
public abstract class BaseGUI extends IndexedGUI {
    
    protected final int width;
    protected final int height;
    
    public BaseGUI(int width, int height) {
        super(width * height);
        this.width = width;
        this.height = height;
    }
    
    @Override
    public void setSlotElement(int x, int y, @NotNull SlotElement slotElement) {
        setSlotElement(convToIndex(x, y), slotElement);
    }
    
    @Override
    public SlotElement getSlotElement(int x, int y) {
        return getSlotElement(convToIndex(x, y));
    }
    
    @Override
    public boolean hasSlotElement(int x, int y) {
        return hasSlotElement(convToIndex(x, y));
    }
    
    @Override
    public ItemStackHolder getItemStackHolder(int x, int y) {
        return getItemStackHolder(convToIndex(x, y));
    }
    
    @Override
    public void setItem(int x, int y, Item item) {
        setItem(convToIndex(x, y), item);
    }
    
    @Override
    public Item getItem(int x, int y) {
        return getItem(convToIndex(x, y));
    }
    
    @Override
    public void remove(int x, int y) {
        remove(convToIndex(x, y));
    }
    
    @Override
    public int getWidth() {
        return width;
    }
    
    @Override
    public int getHeight() {
        return height;
    }
    
    private int convToIndex(int x, int y) {
        if (x >= width || y >= height) throw new IllegalArgumentException("Coordinates out of bounds");
        return SlotUtils.convertToIndex(x, y, width);
    }
    
    // filling methods
    
    public void fill(@NotNull SortedSet<Integer> slots, Item item, boolean replaceExisting) {
        for (int slot : slots) {
            if (!replaceExisting && slotElements[slot] != null) continue;
            setItem(slot, item);
        }
    }
    
    @Override
    public void fill(int start, int end, Item item, boolean replaceExisting) {
        for (int i = start; i < end; i++) {
            if (!replaceExisting && slotElements[i] != null) continue;
            setItem(i, item);
        }
    }
    
    @Override
    public void fill(Item item, boolean replaceExisting) {
        fill(0, size, item, replaceExisting);
    }
    
    @Override
    public void fillRow(int row, Item item, boolean replaceExisting) {
        if (row >= height) throw new IllegalArgumentException("Row out of bounds");
        fill(SlotUtils.getSlotsRow(row, width), item, replaceExisting);
    }
    
    @Override
    public void fillColumn(int column, Item item, boolean replaceExisting) {
        if (column >= width) throw new IllegalArgumentException("Column out of bounds");
        fill(SlotUtils.getSlotsColumn(column, width, height), item, replaceExisting);
    }
    
    @Override
    public void fillBorders(Item item, boolean replaceExisting) {
        fill(SlotUtils.getSlotsBorders(width, height), item, replaceExisting);
    }
    
    @Override
    public void fillRectangle(int x, int y, int width, int height, Item item, boolean replaceExisting) {
        fill(SlotUtils.getSlotsRect(x, y, width, height, this.width), item, replaceExisting);
    }
    
    @Override
    public void fillRectangle(int x, int y, GUI gui, boolean replaceExisting) {
        int slotIndex = 0;
        for (int slot : SlotUtils.getSlotsRect(x, y, gui.getWidth(), gui.getHeight(), this.width)) {
            if (hasSlotElement(slot) && !replaceExisting) continue;
            setSlotElement(slot, new LinkedSlotElement(gui, slotIndex));
            slotIndex++;
        }
    }
    
    @Override
    public void fillRectangle(int x, int y, int width, VirtualInventory virtualInventory, boolean replaceExisting) {
        int height = (int) Math.ceil((double) virtualInventory.getSize() / (double) width);
        
        int slotIndex = 0;
        for (int slot : SlotUtils.getSlotsRect(x, y, width, height, this.width)) {
            if (slotIndex >= virtualInventory.getSize()) return;
            if (hasSlotElement(slot) && !replaceExisting) continue;
            setSlotElement(slot, new VISlotElement(virtualInventory, slotIndex));
            slotIndex++;
        }
    }
    
}
