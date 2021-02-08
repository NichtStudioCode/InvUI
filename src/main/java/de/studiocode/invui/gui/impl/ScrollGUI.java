package de.studiocode.invui.gui.impl;

import de.studiocode.invui.gui.Controllable;
import de.studiocode.invui.gui.GUI;
import de.studiocode.invui.gui.SlotElement;
import de.studiocode.invui.item.Item;
import de.studiocode.invui.item.impl.controlitem.ControlItem;
import de.studiocode.invui.item.impl.controlitem.ScrollItem;
import de.studiocode.invui.util.SlotUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * A scrollable {@link GUI}
 *
 * @see SimpleScrollGUI
 */
public abstract class ScrollGUI extends BaseGUI implements Controllable {
    
    private final List<Item> controlItems = new ArrayList<>();
    private final boolean infiniteLines;
    private final int lineLength;
    private final int[] itemListSlots;
    
    protected int offset;
    
    public ScrollGUI(int width, int height, boolean infiniteLines, int... itemListSlots) {
        super(width, height);
        this.infiniteLines = infiniteLines;
        this.itemListSlots = itemListSlots;
        this.lineLength = SlotUtils.getLongestLineLength(itemListSlots, width);
        
        if (lineLength == 0)
            throw new IllegalArgumentException("Line length can't be 0");
        if (itemListSlots.length == 0)
            throw new IllegalArgumentException("No item list slots provided");
        if (itemListSlots.length % lineLength != 0)
            throw new IllegalArgumentException("itemListSlots has to be a multiple of lineLength");
    }
    
    @Override
    public void addControlItem(int index, ControlItem<?> controlItem) {
        if (!(controlItem instanceof ScrollItem))
            throw new IllegalArgumentException("controlItem is not an instance of ScrollItem");
    
        ((ScrollItem) controlItem).setGui(this);
        setItem(index, controlItem);
        controlItems.add(controlItem);
    }
    
    public void setCurrentLine(int line) {
        this.offset = line * lineLength;
    }
    
    public int getCurrentLine() {
        return offset / lineLength;
    }
    
    private int getMaxLineIndex() {
        int maxLineIndex = (int) Math.ceil((double) getElementAmount() / (double) lineLength);
        return Math.max(0, maxLineIndex - getLineAmount());
    }
    
    private int getLineAmount() {
        return itemListSlots.length / lineLength;
    }
    
    public boolean canScroll(int lines) {
        if (lines == 0 || (infiniteLines && lines > 0)) return true;
        
        int line = getCurrentLine() + lines;
        int maxLineAmount = getMaxLineIndex();
        return line >= 0 && line <= maxLineAmount;
    }
    
    public void scroll(int lines) {
        if (lines == 0) return;
        
        if (canScroll(lines)) {
            setCurrentLine(getCurrentLine() + lines);
            update();
        } else if (lines > 1) {
            setCurrentLine(getMaxLineIndex());
            update();
        } else if (lines < -1) {
            setCurrentLine(0);
            update();
        }
        
    }
    
    protected void update() {
        correctLine();
        updateControlItems();
        updateContent();
    }
    
    private void correctLine() {
        if (offset == 0 || infiniteLines) return;
        
        if (offset < 0) {
            offset = 0;
        } else {
            int currentLine = getCurrentLine();
            int maxLineIndex = getMaxLineIndex();
            if (currentLine >= maxLineIndex) setCurrentLine(maxLineIndex);
        }
    }
    
    private void updateControlItems() {
        controlItems.forEach(Item::notifyWindows);
    }
    
    private void updateContent() {
        List<SlotElement> slotElements = getElements(offset, itemListSlots.length + offset);
        
        for (int i = 0; i < itemListSlots.length; i++) {
            if (slotElements.size() > i) setSlotElement(itemListSlots[i], slotElements.get(i));
            else remove(itemListSlots[i]);
        }
    }
    
    abstract protected int getElementAmount();
    
    abstract protected List<SlotElement> getElements(int from, int to);
    
}
