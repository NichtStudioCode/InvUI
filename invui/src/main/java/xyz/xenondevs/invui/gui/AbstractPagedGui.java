package xyz.xenondevs.invui.gui;

import org.jetbrains.annotations.Unmodifiable;
import org.jetbrains.annotations.UnmodifiableView;
import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.internal.util.SlotUtils;
import xyz.xenondevs.invui.state.MutableProperty;
import xyz.xenondevs.invui.state.Property;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SequencedSet;
import java.util.function.BiConsumer;

sealed abstract class AbstractPagedGui<C>
    extends AbstractGui
    implements PagedGui<C>
    permits PagedInventoriesGuiImpl, PagedItemsGuiImpl, PagedNestedGuiImpl
{
    
    protected int[] contentListSlots;
    
    private final Runnable bakeFn = this::bake;
    
    private MutableProperty<Integer> page;
    private Property<? extends List<? extends C>> content;
    private final List<BiConsumer<? super Integer, ? super Integer>> pageChangeHandlers = new ArrayList<>(0);
    private final List<BiConsumer<? super Integer, ? super Integer>> pageCountChangeHandlers = new ArrayList<>(0);
    private @Nullable List<? extends List<SlotElement>> pages;
    
    public AbstractPagedGui(
        int width, int height,
        SequencedSet<Slot> contentListSlots,
        Property<? extends List<? extends C>> content
    ) {
        super(width, height);
        this.page = MutableProperty.of(0);
        page.observe(this::update);
        this.content = content;
        content.observe(bakeFn);
        this.contentListSlots = SlotUtils.toSlotIndices(contentListSlots, getWidth());
    }
    
    public AbstractPagedGui(
        Structure structure,
        MutableProperty<Integer> page,
        Property<? extends List<? extends C>> content
    ) {
        super(structure.getWidth(), structure.getHeight());
        this.page = page;
        page.observe(this::update);
        this.content = content;
        content.observe(bakeFn);
        this.contentListSlots = structure.getIngredientMatrix().findContentListSlots();
        super.applyStructure(structure); // super call to avoid bake() through applyStructure override
    }
    
    @Override
    public void applyStructure(Structure structure) {
        super.applyStructure(structure);
        this.contentListSlots = structure.getIngredientMatrix().findContentListSlots();
        bake();
    }
    
    @Override
    public void setContentListSlots(SequencedSet<Slot> slots) {
        this.contentListSlots = SlotUtils.toSlotIndices(slots, getWidth());
        bake();
    }
    
    @Override
    public @Unmodifiable SequencedSet<Slot> getContentListSlots() {
        return Collections.unmodifiableSequencedSet(SlotUtils.toSlotSet(contentListSlots, getWidth()));
    }
    
    @Override
    public void setPage(int page) {
        int previousPage = getPage();
        int newPage = correctPage(page);
        
        if (previousPage == newPage)
            return;
        
        this.page.set(newPage);
        update();
        
        pageChangeHandlers.forEach(handler -> handler.accept(previousPage, newPage));
    }
    
    private int correctPage(int page) {
        // 0 <= page < pageAmount
        return Math.max(0, Math.min(page, getPageCount() - 1));
    }
    
    private void correctCurrentPage() {
        int correctedPage = correctPage(getPage());
        if (correctedPage != getPage())
            setPage(correctedPage);
    }
    
    protected void update() {
        correctCurrentPage();
        updatePageContent();
    }
    
    private void updatePageContent() {
        List<SlotElement> slotElements = (pages != null && !pages.isEmpty()) ? pages.get(getPage()) : List.of();
        
        for (int i = 0; i < contentListSlots.length; i++) {
            if (slotElements.size() > i)
                setSlotElement(contentListSlots[i], slotElements.get(i));
            else remove(contentListSlots[i]);
        }
    }
    
    @Override
    public void setContent(List<? extends C> content) {
        this.content.unobserve(bakeFn);
        this.content = Property.of(content);
        bake();
    }
    
    public void setPages(@Nullable List<? extends List<SlotElement>> pages) {
        int prevPageCount = getPageCount();
        this.pages = pages;
        int newPageCount = getPageCount();
        
        for (var handler : pageCountChangeHandlers) {
            handler.accept(prevPageCount, newPageCount);
        }
    }
    
    @Override
    public List<? extends C> getContent() {
        return content.get();
    }
    
    @Override
    public int getPageCount() {
        return pages != null ? pages.size() : 0;
    }
    
    @Override
    public int getPage() {
        return page.get();
    }
    
    @Override
    public void addPageChangeHandler(BiConsumer<? super Integer, ? super Integer> pageChangeHandler) {
        pageChangeHandlers.add(pageChangeHandler);
    }
    
    @Override
    public void removePageChangeHandler(BiConsumer<? super Integer, ? super Integer> pageChangeHandler) {
        pageChangeHandlers.remove(pageChangeHandler);
    }
    
    @Override
    public void setPageChangeHandlers(@Nullable List<? extends BiConsumer<? super Integer, ? super Integer>> handlers) {
        pageChangeHandlers.clear();
        if (handlers != null)
            pageChangeHandlers.addAll(handlers);
    }
    
    public @UnmodifiableView List<BiConsumer<? super Integer, ? super Integer>> getPageChangeHandlers() {
        return Collections.unmodifiableList(pageChangeHandlers);
    }
    
    @Override
    public void addPageCountChangeHandler(BiConsumer<? super Integer, ? super Integer> handler) {
        pageCountChangeHandlers.add(handler);
    }
    
    @Override
    public void removePageCountChangeHandler(BiConsumer<? super Integer, ? super Integer> handler) {
        pageCountChangeHandlers.remove(handler);
    }
    
    @Override
    public void setPageCountChangeHandlers(@Nullable List<? extends BiConsumer<? super Integer, ? super Integer>> handlers) {
        pageCountChangeHandlers.clear();
        if (handlers != null)
            pageCountChangeHandlers.addAll(handlers);
    }
    
    public @UnmodifiableView List<BiConsumer<? super Integer, ? super Integer>> getPageCountChangeHandlers() {
        return Collections.unmodifiableList(pageCountChangeHandlers);
    }
    
    @FunctionalInterface
    public interface Constructor<C> {
        PagedGui<C> create(Structure structure, MutableProperty<Integer> page, Property<? extends List<? extends C>> content);
    }
    
    public static sealed abstract class AbstractBuilder<C>
        extends AbstractGui.AbstractBuilder<PagedGui<C>, PagedGui.Builder<C>>
        implements PagedGui.Builder<C>
        permits PagedItemsGuiImpl.Builder, PagedNestedGuiImpl.Builder, PagedInventoriesGuiImpl.Builder
    {
        
        private final Constructor<C> ctor;
        private Property<? extends List<? extends C>> content = Property.of(List.of());
        private MutableProperty<Integer> page = MutableProperty.of(0);
        private List<BiConsumer<? super Integer, ? super Integer>> pageChangeHandlers = new ArrayList<>(0);
        private List<BiConsumer<? super Integer, ? super Integer>> pageCountChangeHandlers = new ArrayList<>(0);
        
        public AbstractBuilder(Constructor<C> ctor) {
            this.ctor = ctor;
        }
        
        @Override
        public PagedGui.Builder<C> setContent(Property<? extends List<? extends C>> content) {
            this.content = content;
            return this;
        }
        
        @Override
        public PagedGui.Builder<C> setPage(MutableProperty<Integer> page) {
            this.page = page;
            return this;
        }
        
        @Override
        public PagedGui.Builder<C> setPageChangeHandlers(List<? extends BiConsumer<? super Integer, ? super Integer>> handlers) {
            pageChangeHandlers.clear();
            pageChangeHandlers.addAll(handlers);
            return this;
        }
        
        @Override
        public PagedGui.Builder<C> addPageChangeHandler(BiConsumer<? super Integer, ? super Integer> handler) {
            pageChangeHandlers.add(handler);
            return this;
        }
        
        @Override
        public PagedGui.Builder<C> setPageCountChangeHandlers(List<? extends BiConsumer<? super Integer, ? super Integer>> handlers) {
            pageCountChangeHandlers.clear();
            pageCountChangeHandlers.addAll(handlers);
            return this;
        }
        
        @Override
        public PagedGui.Builder<C> addPageCountChangeHandler(BiConsumer<? super Integer, ? super Integer> handler) {
            pageCountChangeHandlers.add(handler);
            return this;
        }
        
        @Override
        public PagedGui<C> build() {
            if (structure == null)
                throw new IllegalStateException("Structure is not defined.");
            
            var gui = ctor.create(structure, page, content);
            pageChangeHandlers.forEach(gui::addPageChangeHandler);
            pageCountChangeHandlers.forEach(gui::addPageCountChangeHandler);
            applyModifiers(gui);
            
            return gui;
        }
        
        @Override
        public PagedGui.Builder<C> clone() {
            var clone = (AbstractBuilder<C>) super.clone();
            clone.pageChangeHandlers = new ArrayList<>(pageChangeHandlers);
            clone.pageCountChangeHandlers = new ArrayList<>(pageCountChangeHandlers);
            return clone;
        }
        
    }
    
}
