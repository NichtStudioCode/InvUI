package de.studiocode.invui.gui;

import de.studiocode.invui.gui.impl.ScrollItemsGuiImpl;
import de.studiocode.invui.gui.impl.ScrollNestedGuiImpl;
import de.studiocode.invui.gui.structure.Structure;
import de.studiocode.invui.util.SlotUtils;

import java.util.List;

/**
 * A scrollable {@link Gui}
 *
 * @see ScrollItemsGuiImpl
 * @see ScrollNestedGuiImpl
 */
public abstract class AbstractScrollGui<C> extends AbstractGui implements ScrollGui<C> {
    
    private final boolean infiniteLines;
    private final int lineLength;
    private final int lineAmount;
    private final int[] contentListSlots;
    
    protected int offset;
    
    public AbstractScrollGui(int width, int height, boolean infiniteLines, int... contentListSlots) {
        super(width, height);
        this.infiniteLines = infiniteLines;
        this.contentListSlots = contentListSlots;
        this.lineLength = SlotUtils.getLongestLineLength(contentListSlots, width);
        this.lineAmount = (int) Math.ceil((double) contentListSlots.length / (double) lineLength);
        
        if (contentListSlots.length == 0)
            throw new IllegalArgumentException("No item list slots provided");
        if (lineLength == 0)
            throw new IllegalArgumentException("Line length can't be 0");
        if (contentListSlots.length % lineLength != 0)
            throw new IllegalArgumentException("contentListSlots has to be a multiple of lineLength");
    }
    
    public AbstractScrollGui(int width, int height, boolean infiniteLines, Structure structure) {
        this(width, height, infiniteLines, structure.getIngredientList().findContentListSlots());
        applyStructure(structure);
    }
    
    public int getLineLength() {
        return lineLength;
    }
    
    @Override
    public int getCurrentLine() {
        return offset / lineLength;
    }
    
    @Override
    public void setCurrentLine(int line) {
        this.offset = line * lineLength;
    }
    
    @Override
    public boolean canScroll(int lines) {
        if (lines == 0 || (infiniteLines && lines > 0) || (lines < 0 && getCurrentLine() > 0)) return true;
        
        int line = getCurrentLine() + lines;
        int maxLineIndex = getMaxLine();
        return line >= 0 && (line + lineAmount - 1) <= maxLineIndex;
    }
    
    @Override
    public void scroll(int lines) {
        if (lines == 0) return;
        
        if (canScroll(lines)) {
            setCurrentLine(getCurrentLine() + lines);
            update();
        } else if (lines > 1) {
            setCurrentLine(getMaxLine());
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
            int maxLineIndex = getMaxLine();
            if (currentLine >= maxLineIndex) setCurrentLine(maxLineIndex);
        }
    }
    
    private void updateContent() {
        List<? extends SlotElement> slotElements = getElements(offset, contentListSlots.length + offset);
        
        for (int i = 0; i < contentListSlots.length; i++) {
            if (slotElements.size() > i) setSlotElement(contentListSlots[i], slotElements.get(i));
            else remove(contentListSlots[i]);
        }
    }
    
    protected abstract List<? extends SlotElement> getElements(int from, int to);
    
}
