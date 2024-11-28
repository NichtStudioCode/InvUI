package xyz.xenondevs.invui.gui;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.gui.structure.Structure;
import xyz.xenondevs.invui.util.SlotUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * A scrollable {@link Gui}
 * <p>
 * Only in very rare circumstances should this class be used directly.
 * Instead, use the static factory or builder functions from the {@link ScrollGui} interface,
 * such as {@link ScrollGui#items()}, {@link ScrollGui#guis()} or {@link ScrollGui#inventories()}
 * to create a new {@link ScrollGui}.
 *
 * @param <C> The content type.
 * @see ScrollItemsGuiImpl
 * @see ScrollNestedGuiImpl
 */
public abstract class AbstractScrollGui<C> extends AbstractGui implements ScrollGui<C> {
    
    private final boolean infiniteLines;
    private final int lineLength;
    private final int lineAmount;
    private final int[] contentListSlots;
    private int currentLine;
    private int offset;
    
    private List<BiConsumer<Integer, Integer>> scrollHandlers;
    /**
     * The content of the gui, to be displayed on the lines.
     */
    protected List<C> content;
    /**
     * The baked {@link SlotElement SlotElements}, containing the content.
     */
    protected List<SlotElement> elements;
    
    /**
     * Creates a new {@link AbstractScrollGui}.
     *
     * @param width            The width of the gui.
     * @param height           The height of the gui.
     * @param infiniteLines    Whether the gui has infinite lines.
     * @param contentListSlots The slots to be used for lines.
     */
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
    
    /**
     * Creates a new {@link AbstractScrollGui}.
     *
     * @param width         The width of the gui.
     * @param height        The height of the gui.
     * @param infiniteLines Whether the gui has infinite lines.
     * @param structure     The structure of the gui.
     */
    public AbstractScrollGui(int width, int height, boolean infiniteLines, Structure structure) {
        this(width, height, infiniteLines, structure.getIngredientList().findContentListSlots());
        applyStructure(structure);
    }
    
    /**
     * Gets the (longest) scroll line length used in this {@link AbstractScrollGui}.
     *
     * @return The line length.
     */
    public int getLineLength() {
        return lineLength;
    }
    
    @Override
    public int getCurrentLine() {
        return currentLine;
    }
    
    @Override
    public void setCurrentLine(int line) {
        int previousLine = currentLine;
        int newLine = correctLine(line);
        
        if (previousLine == newLine)
            return;
        
        this.currentLine = line;
        this.offset = line * lineLength;
        update();
        
        if (scrollHandlers != null) {
            scrollHandlers.forEach(handler -> handler.accept(previousLine, newLine));
        }
    }
    
    private int correctLine(int line) {
        // line 0 always exists, every positive line exists for infinite lines
        if (line == 0 || (infiniteLines && line > 0))
            return 0;
        
        // 0 <= line <= maxLine
        return Math.max(0, Math.min(line, getMaxLine()));
    }
    
    private void correctCurrentLine() {
        int correctedLine = correctLine(currentLine);
        if (correctedLine != currentLine)
            setCurrentLine(correctedLine);
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
        if (lines == 0)
            return;
        
        if (canScroll(lines)) {
            setCurrentLine(getCurrentLine() + lines);
        } else if (lines > 1) {
            setCurrentLine(getMaxLine());
        } else if (lines < -1) {
            setCurrentLine(0);
        }
    }
    
    @Override
    public int getMaxLine() {
        if (elements == null) return 0;
        return (int) Math.ceil((double) elements.size() / (double) getLineLength()) - 1;
    }
    
    @Override
    public void setContent(@Nullable List<C> content) {
        if (content == null || content.isEmpty()) {
            this.content = List.of();
            this.elements = List.of();
            update();
        } else {
            this.content = content;
            bake(); // calls update()
        }
    }
    
    /**
     * Updates the gui, by first correcting the current line
     * and then updating all relevant items.
     */
    protected void update() {
        correctCurrentLine();
        updateControlItems();
        updateContent();
    }
    
    private void updateContent() {
        List<SlotElement> slotElements = elements.subList(offset, Math.min(elements.size(), contentListSlots.length + offset));
        
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
    
    /**
     * Builder for {@link AbstractScrollGui AbstractScrollGuis}.
     * <p>
     * This class should only be used directly if you're creating a custom {@link AbstractBuilder} implementation.
     * Otherwise, use the static builder functions from {@link ScrollGui}, such as
     * {@link ScrollGui#items()}, {@link ScrollGui#guis()} or {@link ScrollGui#inventories()} to obtain a builder instance.
     *
     * @param <C> The content type.
     */
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
            if (this.content != null)
                clone.content = new ArrayList<>(content);
            if (this.scrollHandlers != null)
                clone.scrollHandlers = new ArrayList<>(scrollHandlers);
            return clone;
        }
        
    }
    
}
