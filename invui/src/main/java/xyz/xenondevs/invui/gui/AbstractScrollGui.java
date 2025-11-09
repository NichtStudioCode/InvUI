package xyz.xenondevs.invui.gui;

import org.jetbrains.annotations.Unmodifiable;
import org.jetbrains.annotations.UnmodifiableView;
import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.internal.util.CollectionUtils;
import xyz.xenondevs.invui.internal.util.FuncUtils;
import xyz.xenondevs.invui.internal.util.SlotUtils;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.state.MutableProperty;
import xyz.xenondevs.invui.state.Property;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;

non-sealed abstract class AbstractScrollGui<C> extends AbstractGui implements ScrollGui<C> {
    
    private static final int DEFAULT_LINE = 0;
    
    private List<Slot> contentListSlots = List.of();
    protected Slot min = new Slot(0, 0);
    protected Slot max = new Slot(0, 0);
    protected int lineLength = 0;
    private LineOrientation orientation = LineOrientation.HORIZONTAL;
    
    private final MutableProperty<Integer> line;
    private final MutableProperty<Integer> lineCount = MutableProperty.of(-1);
    private final MutableProperty<Integer> maxLine = MutableProperty.of(-1);
    private final MutableProperty<List<? extends C>> content;
    private final List<BiConsumer<? super Integer, ? super Integer>> scrollHandlers = new ArrayList<>(0);
    private final List<BiConsumer<? super Integer, ? super Integer>> lineCountChangeHandlers = new ArrayList<>(0);
    private int previousLine;
    
    public AbstractScrollGui(
        int width, int height,
        List<? extends Slot> contentListSlots,
        LineOrientation orientation,
        MutableProperty<List<? extends C>> content
    ) {
        super(width, height);
        this.line = MutableProperty.of(DEFAULT_LINE);
        line.observeWeak(this, AbstractScrollGui::handleLineChange);
        this.content = content;
        content.observeWeak(this, AbstractScrollGui::bake);
        setContentListSlotsNoBake(contentListSlots, orientation);
    }
    
    public AbstractScrollGui(
        Structure structure,
        MutableProperty<Integer> line,
        MutableProperty<List<? extends C>> content,
        MutableProperty<Boolean> frozen,
        MutableProperty<Boolean> ignoreObscuredInventorySlots,
        MutableProperty<@Nullable ItemProvider> background
    ) {
        super(structure.getWidth(), structure.getHeight(), frozen, ignoreObscuredInventorySlots, background);
        this.line = line;
        line.observeWeak(this, AbstractScrollGui::handleLineChange);
        this.content = content;
        content.observeWeak(this, AbstractScrollGui::bake);
        super.applyStructure(structure); // super call to avoid bake() through applyStructure override
        setContentListSlotsFromStructure(structure);
    }
    
    @Override
    public void applyStructure(Structure structure) {
        super.applyStructure(structure);
        setContentListSlotsFromStructure(structure);
        bake();
    }
    
    private void setContentListSlotsFromStructure(Structure structure) {
        IngredientMatrix matrix = structure.getIngredientMatrix();
        List<Slot> horizontal = matrix.getSlots(Markers.CONTENT_LIST_SLOT_HORIZONTAL);
        List<Slot> vertical = matrix.getSlots(Markers.CONTENT_LIST_SLOT_VERTICAL);
        
        if (!horizontal.isEmpty() && !vertical.isEmpty())
            throw new IllegalArgumentException("Cannot determine line orientation as structure contains both horizontal and vertical content list slots");
        
        if (!horizontal.isEmpty()) {
            setContentListSlotsNoBake(horizontal, LineOrientation.HORIZONTAL);
        } else if (!vertical.isEmpty()) {
            setContentListSlotsNoBake(vertical, LineOrientation.VERTICAL);
        }
    }
    
    @Override
    public void setContentListSlots(List<? extends Slot> slots, LineOrientation orientation) {
        setContentListSlotsNoBake(slots, orientation);
        bake();
    }
    
    public void setContentListSlotsNoBake(List<? extends Slot> slots, LineOrientation orientation) {
        lineLength = switch (orientation) {
            case HORIZONTAL -> SlotUtils.determineLongestHorizontalLineLength(slots, getHeight());
            case VERTICAL -> SlotUtils.determineLongestVerticalLineLength(slots, getWidth());
        };
        this.min = SlotUtils.min(slots);
        this.max = SlotUtils.max(slots);
        this.contentListSlots = new ArrayList<>(slots);
        this.orientation = orientation;
    }
    
    @Override
    public final void bake() {
        int prevLineCount = lineCount.get();
        int prevMaxLine = maxLine.get();
        setLine(getLine()); // corrects line and refreshes content
        int newLineCount = getLineCount();
        int newMaxLine = getMaxLine();
        
        if (prevLineCount != newLineCount) {
            lineCount.set(newLineCount);
            
            // skip handlers for initial bake
            if (prevLineCount != -1) {
                CollectionUtils.forEachCatching(
                    lineCountChangeHandlers,
                    handler -> handler.accept(prevLineCount, newLineCount),
                    "Failed to handle line count change from " + prevLineCount + " to " + newLineCount
                );
            }
        }
        
        if (prevMaxLine != newMaxLine) {
            maxLine.set(newMaxLine);
        }
    }
    
    private void handleLineChange() {
        int targetLine = getLine();
        int correctedLine = correctLine(targetLine);
        if (correctedLine != targetLine) {
            line.set(correctedLine);
            return;
        }
        
        updateContent();
        if (targetLine != previousLine) {
            CollectionUtils.forEachCatching(
                scrollHandlers,
                handler -> handler.accept(previousLine, targetLine),
                "Failed to handle scroll from line " + previousLine + " to line " + targetLine
            );
        }
        previousLine = targetLine;
    }
    
    protected abstract void updateContent();
    
    private int correctLine(int line) {
        // 0 <= line <= maxLine
        return Math.max(0, Math.min(line, getMaxLine()));
    }
    
    @Override
    public int getMaxLine() {
        if (lineLength == 0)
            return 0;
        
        int lines = switch(orientation) {
            case HORIZONTAL -> max.y() - min.y();
            case VERTICAL -> max.x() - min.x();
        } + 1;
        return Math.max(0, getLineCount() - lines);
    }
    
    @Override
    public void setContent(List<? extends C> content) {
        this.content.set(content);
    }
    
    @Override
    public MutableProperty<List<? extends C>> getContentProperty() {
        return content;
    }
    
    @Override
    public @UnmodifiableView List<C> getContent() {
        return Collections.unmodifiableList(FuncUtils.getSafely(content, List.of()));
    }
    
    @Override
    public @Unmodifiable List<Slot> getContentListSlots() {
        return Collections.unmodifiableList(contentListSlots);
    }
    
    @Override
    public LineOrientation getLineOrientation() {
        return orientation;
    }
    
    @Override
    public Property<Integer> getLineCountProperty() {
        return lineCount;
    }
    
    @Override
    public Property<Integer> getMaxLineProperty() {
        return maxLine;
    }
    
    @Override
    public MutableProperty<Integer> getLineProperty() {
        return line;
    }
    
    @Override
    public int getLine() {
        return FuncUtils.getSafely(line, DEFAULT_LINE);
    }
    
    @Override
    public void setLine(int line) {
        this.line.set(line);
    }
    
    @Override
    public void setScrollHandlers(List<? extends BiConsumer<Integer, Integer>> handlers) {
        this.scrollHandlers.clear();
        this.scrollHandlers.addAll(handlers);
    }
    
    @Override
    public void addScrollHandler(BiConsumer<? super Integer, ? super Integer> handler) {
        scrollHandlers.add(handler);
    }
    
    @Override
    public void removeScrollHandler(BiConsumer<? super Integer, ? super Integer> handler) {
        scrollHandlers.remove(handler);
    }
    
    @Override
    public @UnmodifiableView List<BiConsumer<Integer, Integer>> getScrollHandlers() {
        return CollectionUtils.unmodifiableListUnchecked(scrollHandlers);
    }
    
    @Override
    public void setLineCountChangeHandlers(List<? extends BiConsumer<Integer, Integer>> handlers) {
        lineCountChangeHandlers.clear();
        lineCountChangeHandlers.addAll(handlers);
    }
    
    @Override
    public void addLineCountChangeHandler(BiConsumer<? super Integer, ? super Integer> handler) {
        lineCountChangeHandlers.add(handler);
    }
    
    @Override
    public void removeLineCountChangeHandler(BiConsumer<? super Integer, ? super Integer> handler) {
        lineCountChangeHandlers.remove(handler);
    }
    
    @Override
    public @UnmodifiableView List<BiConsumer<Integer, Integer>> getLineCountChangeHandlers() {
        return CollectionUtils.unmodifiableListUnchecked(lineCountChangeHandlers);
    }
    
    @FunctionalInterface
    public interface Constructor<C> {
        ScrollGui<C> create(
            Structure structure,
            MutableProperty<Integer> line,
            MutableProperty<List<? extends C>> content,
            MutableProperty<Boolean> frozen,
            MutableProperty<Boolean> ignoreObscuredInventorySlots,
            MutableProperty<@Nullable ItemProvider> background
        );
    }
    
    public static sealed abstract class AbstractBuilder<C>
        extends AbstractGui.AbstractBuilder<ScrollGui<C>, ScrollGui.Builder<C>>
        implements ScrollGui.Builder<C>
        permits ScrollItemsGuiImpl.Builder, ScrollNestedGuiImpl.Builder, ScrollInventoryGuiImpl.Builder
    {
        
        private final Constructor<C> ctor;
        private MutableProperty<List<? extends C>> content = MutableProperty.of(List.of());
        private MutableProperty<Integer> line = MutableProperty.of(DEFAULT_LINE);
        private List<BiConsumer<? super Integer, ? super Integer>> scrollHandlers = new ArrayList<>(0);
        private List<BiConsumer<? super Integer, ? super Integer>> lineCountChangeHandlers = new ArrayList<>(0);
        
        public AbstractBuilder(Constructor<C> ctor) {
            this.ctor = ctor;
        }
        
        @Override
        public ScrollGui.Builder<C> setContent(MutableProperty<List<? extends C>> content) {
            this.content = content;
            return this;
        }
        
        @Override
        public ScrollGui.Builder<C> setLine(MutableProperty<Integer> line) {
            this.line = line;
            return this;
        }
        
        @Override
        public ScrollGui.Builder<C> setScrollHandlers(List<? extends BiConsumer<? super Integer, ? super Integer>> handlers) {
            this.scrollHandlers.clear();
            this.scrollHandlers.addAll(handlers);
            return this;
        }
        
        @Override
        public ScrollGui.Builder<C> addScrollHandler(BiConsumer<? super Integer, ? super Integer> handler) {
            scrollHandlers.add(handler);
            return this;
        }
        
        @Override
        public ScrollGui.Builder<C> setLineCountChangeHandlers(List<? extends BiConsumer<? super Integer, ? super Integer>> handlers) {
            lineCountChangeHandlers.clear();
            lineCountChangeHandlers.addAll(handlers);
            return this;
        }
        
        @Override
        public ScrollGui.Builder<C> addLineCountChangeHandler(BiConsumer<? super Integer, ? super Integer> handler) {
            lineCountChangeHandlers.add(handler);
            return this;
        }
        
        @Override
        public ScrollGui<C> build() {
            if (structure == null)
                throw new IllegalStateException("Structure is not defined.");
            
            var gui = ctor.create(structure, line, content, frozen, ignoreObscuredInventorySlots, background);
            scrollHandlers.forEach(gui::addScrollHandler);
            lineCountChangeHandlers.forEach(gui::addLineCountChangeHandler);
            applyModifiers(gui);
            
            return gui;
        }
        
        @Override
        public ScrollGui.Builder<C> clone() {
            var clone = (AbstractBuilder<C>) super.clone();
            clone.scrollHandlers = new ArrayList<>(scrollHandlers);
            clone.lineCountChangeHandlers = new ArrayList<>(lineCountChangeHandlers);
            return clone;
        }
        
    }
    
}
