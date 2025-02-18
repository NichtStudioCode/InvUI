package xyz.xenondevs.invui.gui;

import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.internal.util.SlotUtils;

import java.util.ArrayList;
import java.util.Arrays;
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
    private int lineLength;
    private int lineAmount;
    private int[] contentListSlots = new int[0];
    private int currentLine;
    private int offset;
    
    private @Nullable List<BiConsumer<Integer, Integer>> scrollHandlers;
    private @Nullable List<BiConsumer<Integer, Integer>> lineCountChangeHandlers;
    private Supplier<? extends List<? extends C>> contentSupplier = List::of;
    private @Nullable List<SlotElement> elements;
    
    public AbstractScrollGui(int width, int height, boolean infiniteLines, int... contentListSlots) {
        super(width, height);
        this.infiniteLines = infiniteLines;
        setContentListSlots(contentListSlots, false);
    }
    
    public AbstractScrollGui(int width, int height, boolean infiniteLines, Structure structure) {
        super(width, height);
        this.infiniteLines = infiniteLines;
        super.applyStructure(structure); // super call to avoid bake() through applyStructure override
        setContentListSlots(structure.getIngredientMatrix().findContentListSlots(), false);
    }
    
    @Override
    public void applyStructure(Structure structure) {
        super.applyStructure(structure);
        setContentListSlots(structure.getIngredientMatrix().findContentListSlots());
    }
    
    @Override
    public void setContentListSlots(Slot[] slots) {
        setContentListSlots(SlotUtils.toSlotIndices(slots, getWidth()));
    }
    
    @Override
    public void setContentListSlots(int[] slotIndices) {
        setContentListSlots(slotIndices, true);
    }
    
    private void setContentListSlots(int[] slotIndices, boolean bake) {
        int lineLength = SlotUtils.getLongestLineLength(slotIndices, getWidth());
        if (slotIndices.length % lineLength != 0)
            throw new IllegalArgumentException("contentListSlots has to be a multiple of lineLength");
        
        this.contentListSlots = slotIndices.clone();
        this.lineLength = lineLength;
        this.lineAmount = (int) Math.ceil((double) slotIndices.length / (double) lineLength);
        
        if (bake)
            bake();
    }
    
    public int getLineLength() {
        return lineLength;
    }
    
    @Override
    public int getLine() {
        return currentLine;
    }
    
    @Override
    public void setLine(int line) {
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
            setLine(correctedLine);
    }
    
    @Override
    public boolean canScroll(int lines) {
        if (lines == 0 || (infiniteLines && lines > 0) || (lines < 0 && getLine() > 0)) return true;
        
        int line = getLine() + lines;
        int maxLineIndex = getMaxLine();
        return line >= 0 && (line + lineAmount - 1) <= maxLineIndex;
    }
    
    @Override
    public void scroll(int lines) {
        if (lines == 0)
            return;
        
        if (canScroll(lines)) {
            setLine(getLine() + lines);
        } else if (lines > 1) {
            setLine(getMaxLine());
        } else if (lines < -1) {
            setLine(0);
        }
    }
    
    @Override
    public int getMaxLine() {
        if (elements == null) return 0;
        return (int) Math.ceil((double) elements.size() / (double) getLineLength()) - 1;
    }
    
    @Override
    public void setContent(List<? extends C> content) {
        setContentSupplier(() -> content);
    }
    
    @Override
    public void setContentSupplier(Supplier<? extends List<? extends C>> contentSupplier) {
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
    public List<? extends C> getContent() {
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
        
        private final BiFunction<Supplier<? extends List<? extends C>>, Structure, ScrollGui<C>> ctor;
        private @Nullable Supplier<List<C>> contentSupplier;
        private @Nullable List<C> content;
        private @Nullable List<BiConsumer<Integer, Integer>> scrollHandlers;
        private @Nullable List<BiConsumer<Integer, Integer>> lineCountChangeHandlers;
        
        public AbstractBuilder(BiFunction<Supplier<? extends List<? extends C>>, Structure, ScrollGui<C>> ctor) {
            this.ctor = ctor;
        }
        
        @Override
        public ScrollGui.Builder<C> setContentSupplier(Supplier<List<C>> contentSupplier) {
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
