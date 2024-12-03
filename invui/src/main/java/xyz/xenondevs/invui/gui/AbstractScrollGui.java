package xyz.xenondevs.invui.gui;

import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.internal.util.SlotUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Supplier;

sealed abstract class AbstractScrollGui<C>
    extends AbstractGui
    implements ScrollGui<C>
    permits ScrollItemsGuiImpl, ScrollNestedGuiImpl, ScrollInventoryGuiImpl
{
    
    private final boolean infiniteLines;
    private final int lineLength;
    private final int lineAmount;
    private final int[] contentListSlots;
    private int currentLine;
    private int offset;
    
    private @Nullable List<BiConsumer<Integer, Integer>> scrollHandlers;
    private @Nullable List<BiConsumer<Integer, Integer>> lineCountChangeHandlers;
    private Supplier<List<C>> contentSupplier = List::of;
    private @Nullable List<SlotElement> elements;
    
    public AbstractScrollGui(int width, int height, boolean infiniteLines, int... contentListSlots) {
        super(width, height);
        
        this.infiniteLines = infiniteLines;
        this.contentListSlots = contentListSlots;
        this.lineLength = SlotUtils.getLongestLineLength(contentListSlots, width);
        this.lineAmount = (int) Math.ceil((double) contentListSlots.length / (double) lineLength);
        
        if (contentListSlots.length == 0)
            throw new IllegalArgumentException("Content list slots must not be empty");
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
    public void setContent(List<C> content) {
        setContent(() -> content);
    }
    
    @Override
    public void setContent(Supplier<List<C>> contentSupplier) {
        this.contentSupplier = contentSupplier;
        bake();
    }
    
    public void setElements(@Nullable List<SlotElement> elements) {
        int previousMaxLine = getMaxLine();
        this.elements = elements;
        int newMaxLine = getMaxLine();
        
        if (lineCountChangeHandlers != null) {
            for (var handler : lineCountChangeHandlers) {
                handler.accept(previousMaxLine, newMaxLine);
            }
        }
    }
    
    protected void update() {
        correctCurrentLine();
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
    public List<C> getContent() {
        return contentSupplier.get();
    }
    
    @Override
    public void setScrollHandlers(@Nullable List<BiConsumer<Integer, Integer>> scrollHandlers) {
        this.scrollHandlers = scrollHandlers;
    }
    
    @Override
    public void addScrollHandler(BiConsumer<Integer, Integer> scrollHandler) {
        if (scrollHandlers == null)
            scrollHandlers = new ArrayList<>();
        
        scrollHandlers.add(scrollHandler);
    }
    
    @Override
    public void removeScrollHandler(BiConsumer<Integer, Integer> scrollHandler) {
        if (scrollHandlers != null)
            scrollHandlers.remove(scrollHandler);
    }
    
    @Override
    public @Nullable List<BiConsumer<Integer, Integer>> getScrollHandlers() {
        return scrollHandlers;
    }
    
    @Override
    public void setLineCountChangeHandlers(@Nullable List<BiConsumer<Integer, Integer>> handlers) {
        this.scrollHandlers = handlers;
    }
    
    @Override
    public void addLineCountChangeHandler(BiConsumer<Integer, Integer> handler) {
        if (lineCountChangeHandlers == null)
            lineCountChangeHandlers = new ArrayList<>();
        
        lineCountChangeHandlers.add(handler);
    }
    
    @Override
    public void removeLineCountChangeHandler(BiConsumer<Integer, Integer> handler) {
        if (lineCountChangeHandlers != null)
            lineCountChangeHandlers.remove(handler);
    }
    
    @Override
    public @Nullable List<BiConsumer<Integer, Integer>> getLineCountChangeHandlers() {
        return lineCountChangeHandlers;
    }
    
    public static sealed abstract class AbstractBuilder<C>
        extends AbstractGui.AbstractBuilder<ScrollGui<C>, ScrollGui.Builder<C>>
        implements ScrollGui.Builder<C>
        permits ScrollItemsGuiImpl.Builder, ScrollNestedGuiImpl.Builder, ScrollInventoryGuiImpl.Builder
    {
        
        private final BiFunction<Supplier<List<C>>, Structure, ScrollGui<C>> ctor;
        private @Nullable Supplier<List<C>> contentSupplier;
        private @Nullable List<C> content;
        private @Nullable List<BiConsumer<Integer, Integer>> scrollHandlers;
        private @Nullable List<BiConsumer<Integer, Integer>> lineCountChangeHandlers;
        
        public AbstractBuilder(BiFunction<Supplier<List<C>>, Structure, ScrollGui<C>> ctor) {
            this.ctor = ctor;
        }
        
        @Override
        public ScrollGui.Builder<C> setContent(Supplier<List<C>> contentSupplier) {
            this.contentSupplier = contentSupplier;
            return this;
        }
        
        @Override
        public ScrollGui.Builder<C> setContent(List<C> content) {
            this.content = content;
            return this;
        }
        
        @Override
        public ScrollGui.Builder<C> addContent(C content) {
            if (this.content == null)
                this.content = new ArrayList<>();
            
            this.content.add(content);
            return this;
        }
        
        @Override
        public ScrollGui.Builder<C> setScrollHandlers(List<BiConsumer<Integer, Integer>> handlers) {
            scrollHandlers = handlers;
            return this;
        }
        
        @Override
        public ScrollGui.Builder<C> addScrollHandler(BiConsumer<Integer, Integer> handler) {
            if (scrollHandlers == null)
                scrollHandlers = new ArrayList<>(1);
            
            scrollHandlers.add(handler);
            return this;
        }
        
        @Override
        public ScrollGui.Builder<C> setLineCountChangeHandlers(List<BiConsumer<Integer, Integer>> handlers) {
            lineCountChangeHandlers = handlers;
            return this;
        }
        
        @Override
        public ScrollGui.Builder<C> addLineCountChangeHandler(BiConsumer<Integer, Integer> handler) {
            if (lineCountChangeHandlers == null)
                lineCountChangeHandlers = new ArrayList<>(1);
            
            lineCountChangeHandlers.add(handler);
            return this;
        }
        
        @Override
        public ScrollGui<C> build() {
            if (structure == null)
                throw new IllegalStateException("Structure is not defined.");
            
            Supplier<List<C>> supplier = contentSupplier != null
                ? contentSupplier
                : () -> content != null ? content : List.of();
            
            var gui = ctor.apply(supplier, structure);
            
            if (scrollHandlers != null) {
                for (var handler : scrollHandlers) {
                    gui.addScrollHandler(handler);
                }
            }
            
            applyModifiers(gui);
            
            return gui;
        }
        
        @Override
        public ScrollGui.Builder<C> clone() {
            var clone = (AbstractBuilder<C>) super.clone();
            if (this.content != null)
                clone.content = new ArrayList<>(content);
            if (this.scrollHandlers != null)
                clone.scrollHandlers = new ArrayList<>(scrollHandlers);
            return clone;
        }
        
    }
    
}
