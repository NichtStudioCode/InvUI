package de.studiocode.invgui.gui.impl;

import de.studiocode.invgui.gui.GUI;
import de.studiocode.invgui.gui.SlotElement;
import de.studiocode.invgui.item.Item;
import de.studiocode.invgui.util.ArrayUtils;
import de.studiocode.invgui.util.SlotUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.SortedSet;

public abstract class BaseGUI implements GUI {
    
    protected final int width;
    protected final int height;
    protected final int size;
    protected final SlotElement[] slotElements;
    
    public BaseGUI(int width, int height) {
        this.width = width;
        this.height = height;
        this.size = width * height;
        slotElements = new SlotElement[size];
    }
    
    @Override
    public void setItem(int x, int y, Item item) {
        setItem(convToIndex(x, y), item);
    }
    
    @Override
    public void setItem(int index, Item item) {
        remove(index);
        if (item != null) slotElements[index] = new SlotElement(item);
    }
    
    @Override
    public Item getItem(int x, int y) {
        return getItem(convToIndex(x, y));
    }
    
    @Override
    public Item getItem(int index) {
        SlotElement slotElement = slotElements[index];
        if (slotElement == null) return null;
        return slotElement.isItem() ? slotElement.getItem() : slotElement.getItemFromGui();
    }
    
    @Override
    public void addItems(@NotNull Item... items) {
        for (Item item : items) {
            int emptyIndex = ArrayUtils.findFirstEmptyIndex(items);
            if (emptyIndex == -1) break;
            setItem(emptyIndex, item);
        }
    }
    
    @Override
    public void remove(int x, int y) {
        remove(convToIndex(x, y));
    }
    
    @Override
    public void remove(int index) {
        SlotElement slotElement = slotElements[index];
        if (slotElement == null) return;
        if (slotElement.isItem()) {
            slotElements[index] = null;
        } else throw new IllegalArgumentException("Slot " + index + " is part of a nested GUI");
    }
    
    @Override
    public void nest(int offset, @NotNull GUI gui) {
        for (int i = 0; i < gui.getSize(); i++) slotElements[i + offset] = new SlotElement(gui, i);
    }
    
    @Override
    public void handleClick(int slotNumber, Player player, ClickType clickType, InventoryClickEvent event) {
        SlotElement slotElement = slotElements[slotNumber];
        if (slotElement == null) return;
        if (slotElement.isGui())
            slotElement.getGui().handleClick(slotElement.getSlotNumber(), player, clickType, event);
        else slotElement.getItem().handleClick(clickType, player, event);
    }
    
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
    public void setSlotElement(int x, int y, @NotNull SlotElement slotElement) {
        setSlotElement(convToIndex(x, y), slotElement);
    }
    
    @Override
    public void setSlotElement(int index, @NotNull SlotElement slotElement) {
        slotElements[index] = slotElement;
    }
    
    @Override
    public SlotElement getSlotElement(int x, int y) {
        return getSlotElement(convToIndex(x, y));
    }
    
    @Override
    public SlotElement getSlotElement(int index) {
        return slotElements[index];
    }
    
    @Override
    public SlotElement[] getSlotElements() {
        return slotElements.clone();
    }
    
    @Override
    public int getSize() {
        return size;
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
    
}
