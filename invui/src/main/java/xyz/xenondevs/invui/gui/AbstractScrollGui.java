package xyz.xenondevs.invui.gui;

import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.structure.Structure;
import xyz.xenondevs.invui.util.SlotUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

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
    private int offset;
    
    private List<BiConsumer<Integer, Integer>> scrollHandlers;
    
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
    
    @Override
    public void setScrollHandlers(@NotNull List<@NotNull BiConsumer<Integer, Integer>> scrollHandlers) {
        this.scrollHandlers = scrollHandlers;
    }
    
    @Override
    public void addScrollHandler(@NotNull BiConsumer<Integer, Integer> scrollHandler) {
        if (scrollHandlers == null)
            scrollHandlers = new ArrayList<>();
        
        scrollHandlers.add(scrollHandler);
    }
    
    @Override
    public void removeScrollHandler(@NotNull BiConsumer<Integer, Integer> scrollHandler) {
        if (scrollHandlers != null)
            scrollHandlers.remove(scrollHandler);
    }
    
    protected abstract List<? extends SlotElement> getElements(int from, int to);
    
    public abstract static class AbstractBuilder<C>
        extends AbstractGui.AbstractBuilder<ScrollGui<C>, ScrollGui.Builder<C>>
        implements ScrollGui.Builder<C>
    {
        
        protected List<C> content;
        protected List<BiConsumer<Integer, Integer>> scrollHandlers;
        
        @Override
        public ScrollGui.@NotNull Builder<C> setContent(@NotNull List<@NotNull C> content) {
            this.content = content;
            return this;
        }
        
        @Override
        public ScrollGui.@NotNull Builder<C> addContent(@NotNull C content) {
            if (this.content == null)
                this.content = new ArrayList<>();
            
            this.content.add(content);
            return this;
        }
        
        @Override
        public ScrollGui.@NotNull Builder<C> setScrollHandlers(@NotNull List<@NotNull BiConsumer<Integer, Integer>> handlers) {
            scrollHandlers = handlers;
            return this;
        }
        
        @Override
        public ScrollGui.@NotNull Builder<C> addScrollHandler(@NotNull BiConsumer<Integer, Integer> handler) {
            if (scrollHandlers == null)
                scrollHandlers = new ArrayList<>(1);
            
            scrollHandlers.add(handler);
            return this;
        }
        
        @Override
        protected void applyModifiers(@NotNull ScrollGui<C> gui) {
            super.applyModifiers(gui);
            gui.setScrollHandlers(scrollHandlers);
        }
        
        @Override
        public @NotNull ScrollGui.Builder<C> clone() {
            var clone = (AbstractBuilder<C>) super.clone();
            clone.content = new ArrayList<>(content);
            clone.scrollHandlers = new ArrayList<>(scrollHandlers);
            return clone;
        }
        
    }
    
}
