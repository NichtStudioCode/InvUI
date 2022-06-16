package de.studiocode.invui.gui.impl;

import de.studiocode.invui.gui.GUI;
import de.studiocode.invui.gui.SlotElement;
import de.studiocode.invui.gui.structure.Structure;
import de.studiocode.invui.util.SlotUtils;

import java.util.List;

/**
 * A scrollable {@link GUI}
 *
 * @see SimpleScrollItemsGUI
 * @see SimpleScrollNestedGUI
 */
public abstract class ScrollGUI extends BaseGUI {
    
    private final boolean infiniteLines;
    private final int lineLength;
    private final int lineAmount;
    private final int[] itemListSlots;
    
    protected int offset;
    
    public ScrollGUI(int width, int height, boolean infiniteLines, int... itemListSlots) {
        super(width, height);
        this.infiniteLines = infiniteLines;
        this.itemListSlots = itemListSlots;
        this.lineLength = SlotUtils.getLongestLineLength(itemListSlots, width);
        this.lineAmount = (int) Math.ceil((double) itemListSlots.length / (double) lineLength);
        
        if (itemListSlots.length == 0)
            throw new IllegalArgumentException("No item list slots provided");
        if (lineLength == 0)
            throw new IllegalArgumentException("Line length can't be 0");
        if (itemListSlots.length % lineLength != 0)
            throw new IllegalArgumentException("itemListSlots has to be a multiple of lineLength");
    }
    
    public ScrollGUI(int width, int height, boolean infiniteLines, Structure structure) {
        this(width, height, infiniteLines, structure.getIngredientList().findItemListSlots());
        applyStructure(structure);
    }
    
    public int getLineLength() {
        return lineLength;
    }
    
    public int getCurrentLine() {
        return offset / lineLength;
    }
    
    public void setCurrentLine(int line) {
        this.offset = line * lineLength;
    }
    
    public boolean canScroll(int lines) {
        if (lines == 0 || (infiniteLines && lines > 0) || (lines < 0 && getCurrentLine() > 0)) return true;
        
        int line = getCurrentLine() + lines;
        int maxLineIndex = getMaxLineIndex();
        return line >= 0 && (line + lineAmount - 1) <= maxLineIndex;
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
    
    private void updateContent() {
        List<? extends SlotElement> slotElements = getElements(offset, itemListSlots.length + offset);
        
        for (int i = 0; i < itemListSlots.length; i++) {
            if (slotElements.size() > i) setSlotElement(itemListSlots[i], slotElements.get(i));
            else remove(itemListSlots[i]);
        }
    }
    
    protected abstract int getMaxLineIndex();
    
    protected abstract List<? extends SlotElement> getElements(int from, int to);
    
}
