package xyz.xenondevs.invui.gui;

import org.jetbrains.annotations.Unmodifiable;
import org.jetbrains.annotations.UnmodifiableView;
import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.internal.util.ArrayUtils;
import xyz.xenondevs.invui.internal.util.CollectionUtils;
import xyz.xenondevs.invui.internal.util.SlotUtils;
import xyz.xenondevs.invui.state.MutableProperty;
import xyz.xenondevs.invui.state.Property;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SequencedSet;
import java.util.function.BiConsumer;

sealed abstract class AbstractScrollGui<C>
    extends AbstractGui
    implements ScrollGui<C>
    permits ScrollItemsGuiImpl, ScrollNestedGuiImpl, ScrollInventoryGuiImpl
{
    
    private int lineLength;
    private int[] contentListSlots = new int[0];
    
    private final MutableProperty<Integer> line;
    private Property<? extends List<? extends C>> content;
    private final List<BiConsumer<? super Integer, ? super Integer>> scrollHandlers = new ArrayList<>(0);
    private final List<BiConsumer<? super Integer, ? super Integer>> lineCountChangeHandlers = new ArrayList<>(0);
    private @Nullable List<SlotElement> elements;
    
    public AbstractScrollGui(
        int width, int height,
        SequencedSet<? extends Slot> contentListSlots,
        boolean horizontalLines,
        Property<? extends List<? extends C>> content
    ) {
        super(width, height);
        this.line = MutableProperty.of(0);
        line.observeWeak(this, AbstractScrollGui::update);
        this.content = content;
        content.observeWeak(this, AbstractScrollGui::bake);
        setContentListSlots(SlotUtils.toSlotIndicesSet(contentListSlots, getWidth()), horizontalLines);
    }
    
    public AbstractScrollGui(
        Structure structure,
        MutableProperty<Integer> line,
        Property<? extends List<? extends C>> content
    ) {
        super(structure.getWidth(), structure.getHeight());
        this.line = line;
        line.observeWeak(this, AbstractScrollGui::update);
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
        int[] horizontal = matrix.findIndices(Markers.CONTENT_LIST_SLOT_HORIZONTAL);
        int[] vertical = matrix.findIndices(Markers.CONTENT_LIST_SLOT_VERTICAL);
        
        if (horizontal.length > 0 && vertical.length > 0)
            throw new IllegalArgumentException("Cannot determine line orientation as structure contains both horizontal and vertical content list slots");
        
        if (horizontal.length > 0) {
            setContentListSlots(ArrayUtils.toSequencedSet(horizontal), true);
        } else if (vertical.length > 0) {
            setContentListSlots(ArrayUtils.toSequencedSet(vertical), false);
        }
    }
    
    @Override
    public void setContentListSlotsHorizontal(SequencedSet<? extends Slot> slots) {
        setContentListSlots(SlotUtils.toSlotIndicesSet(slots, getWidth()), true);
        bake();
    }
    
    @Override
    public void setContentListSlotsVertical(SequencedSet<? extends Slot> slots) {
        setContentListSlots(SlotUtils.toSlotIndicesSet(slots, getWidth()), false);
        bake();
    }
    
    @Override
    public @Unmodifiable SequencedSet<Slot> getContentListSlots() {
        return Collections.unmodifiableSequencedSet(SlotUtils.toSlotSet(contentListSlots, getWidth()));
    }
    
    private void setContentListSlots(SequencedSet<Integer> slots, boolean horizontal) {
        int lineLength = horizontal
            ? SlotUtils.determineHorizontalLinesLength(slots, getWidth())
            : SlotUtils.determineVerticalLinesLength(slots, getWidth());
        
        this.contentListSlots = ArrayUtils.toIntArray(slots);
        this.lineLength = lineLength;
    }
    
    @Override
    public int getLine() {
        return line.get();
    }
    
    @Override
    public void setLine(int line) {
        int previousLine = getLine();
        int newLine = correctLine(line);
        
        if (previousLine == newLine)
            return;
        
        this.line.set(newLine);
        update();
        
        scrollHandlers.forEach(handler -> handler.accept(previousLine, newLine));
    }
    
    private int correctLine(int line) {
        // 0 <= line <= maxLine
        return Math.max(0, Math.min(line, getMaxLine()));
    }
    
    private void correctCurrentLine() {
        int currentLine = getLine();
        int correctedLine = correctLine(currentLine);
        if (correctedLine != currentLine)
            setLine(correctedLine);
    }
    
    @Override
    public int getLineCount() {
        if (elements == null)
            return 0;
        
        return (int) Math.ceil((double) elements.size() / (double) lineLength);
    }
    
    @Override
    public int getMaxLine() {
        if (elements == null)
            return 0;
        
        int visibleLines = contentListSlots.length / lineLength;
        int lineCount = getLineCount();
        return Math.max(0, lineCount - visibleLines);
    }
    
    @Override
    public void setContent(List<? extends C> content) {
        this.content.unobserveWeak(this);
        this.content = Property.of(content);
        bake();
    }
    
    public void setElements(@Nullable List<SlotElement> elements) {
        int previousLineCount = getLineCount();
        this.elements = elements;
        int newLineCount = getLineCount();
        
        for (var handler : lineCountChangeHandlers) {
            handler.accept(previousLineCount, newLineCount);
        }
    }
    
    protected void update() {
        correctCurrentLine();
        updateContent();
    }
    
    private void updateContent() {
        assert elements != null;
        int offset = getLine() * lineLength;
        List<SlotElement> slotElements = elements.subList(offset, Math.min(elements.size(), contentListSlots.length + offset));
        
        for (int i = 0; i < contentListSlots.length; i++) {
            setSlotElement(contentListSlots[i], slotElements.size() > i ? slotElements.get(i) : null);
        }
    }
    
    @Override
    public @UnmodifiableView List<C> getContent() {
        return Collections.unmodifiableList(content.get());
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
        ScrollGui<C> create(Structure structure, MutableProperty<Integer> line, Property<? extends List<? extends C>> content);
    }
    
    public static sealed abstract class AbstractBuilder<C>
        extends AbstractGui.AbstractBuilder<ScrollGui<C>, ScrollGui.Builder<C>>
        implements ScrollGui.Builder<C>
        permits ScrollItemsGuiImpl.Builder, ScrollNestedGuiImpl.Builder, ScrollInventoryGuiImpl.Builder
    {
        
        private final Constructor<C> ctor;
        private Property<? extends List<? extends C>> content = Property.of(List.of());
        private MutableProperty<Integer> line = MutableProperty.of(0);
        private List<BiConsumer<? super Integer, ? super Integer>> scrollHandlers = new ArrayList<>(0);
        private List<BiConsumer<? super Integer, ? super Integer>> lineCountChangeHandlers = new ArrayList<>(0);
        
        public AbstractBuilder(Constructor<C> ctor) {
            this.ctor = ctor;
        }
        
        @Override
        public ScrollGui.Builder<C> setContent(Property<? extends List<? extends C>> content) {
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
            
            var gui = ctor.create(structure, line, content);
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
