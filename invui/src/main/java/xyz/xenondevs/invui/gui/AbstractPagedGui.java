package xyz.xenondevs.invui.gui;

import org.jetbrains.annotations.Unmodifiable;
import org.jetbrains.annotations.UnmodifiableView;
import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.internal.util.CollectionUtils;
import xyz.xenondevs.invui.internal.util.FuncUtils;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.state.MutableProperty;
import xyz.xenondevs.invui.state.Property;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;

non-sealed abstract class AbstractPagedGui<C> extends AbstractGui implements PagedGui<C> {
    
    private static final int DEFAULT_PAGE = 0;
    
    private List<Slot> contentListSlots = List.of();
    
    private final MutableProperty<Integer> page;
    private final MutableProperty<Integer> pageCount = MutableProperty.of(-1);
    private final MutableProperty<List<? extends C>> content;
    private final List<BiConsumer<? super Integer, ? super Integer>> pageChangeHandlers = new ArrayList<>(0);
    private final List<BiConsumer<? super Integer, ? super Integer>> pageCountChangeHandlers = new ArrayList<>(0);
    private int previousPage;
    
    public AbstractPagedGui(
        int width, int height,
        List<? extends Slot> contentListSlots,
        MutableProperty<List<? extends C>> content
    ) {
        super(width, height);
        this.page = MutableProperty.of(DEFAULT_PAGE);
        page.observeWeak(this, AbstractPagedGui::handlePageChange);
        this.content = content;
        content.observeWeak(this, AbstractPagedGui::bake);
        this.contentListSlots = new ArrayList<>(contentListSlots);
    }
    
    public AbstractPagedGui(
        Structure structure,
        MutableProperty<Integer> page,
        MutableProperty<List<? extends C>> content,
        MutableProperty<Boolean> frozen,
        MutableProperty<Boolean> ignoreObscuredInventorySlots,
        MutableProperty<@Nullable ItemProvider> background
    ) {
        super(structure.getWidth(), structure.getHeight(), frozen, ignoreObscuredInventorySlots, background);
        this.page = page;
        page.observeWeak(this, AbstractPagedGui::handlePageChange);
        this.content = content;
        content.observeWeak(this, AbstractPagedGui::bake);
        this.contentListSlots = structure.getIngredientMatrix().getContentListSlots();
        super.applyStructure(structure); // super call to avoid bake() through applyStructure override
    }
    
    @Override
    public void applyStructure(Structure structure) {
        super.applyStructure(structure);
        this.contentListSlots = structure.getIngredientMatrix().getContentListSlots();
        bake();
    }
    
    @Override
    public void setContentListSlots(List<? extends Slot> slots) {
        this.contentListSlots = new ArrayList<>(slots);
        bake();
    }
    
    @Override
    public @Unmodifiable List<Slot> getContentListSlots() {
        return Collections.unmodifiableList(contentListSlots);
    }
    
    @Override
    public final void bake() {
        int prevPageCount = pageCount.get();
        setPage(getPage()); // corrects page and refreshes content
        int newPageCount = getPageCount();
        
        if (prevPageCount == newPageCount)
            return;
        pageCount.set(newPageCount);
        
        // skip handlers for initial bake
        if (prevPageCount == -1)
            return;
        CollectionUtils.forEachCatching(
            pageCountChangeHandlers,
            handler -> handler.accept(prevPageCount, newPageCount),
            "Failed to handle page count change from " + prevPageCount + " to " + newPageCount
        );
    }
    
    private void handlePageChange() {
        int targetPage = getPage();
        int correctedPage = correctPage(targetPage);
        if (targetPage != correctedPage) {
            page.set(correctedPage);
            return;
        }
        
        updateContent();
        if (targetPage != previousPage) {
            CollectionUtils.forEachCatching(
                pageChangeHandlers,
                handler -> handler.accept(previousPage, targetPage),
                "Failed to handle page change from " + previousPage + " to " + targetPage
            );
        }
        previousPage = targetPage;
    }
    
    protected abstract void updateContent();
    
    private int correctPage(int page) {
        // 0 <= page < pageAmount
        return Math.max(0, Math.min(page, getPageCount() - 1));
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
    public void setPage(int page) {
        this.page.set(page);
    }
    
    @Override
    public MutableProperty<Integer> getPageProperty() {
        return page;
    }
    
    @Override
    public Property<Integer> getPageCountProperty() {
        return pageCount;
    }
    
    @Override
    public int getPage() {
        return FuncUtils.getSafely(page, DEFAULT_PAGE);
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
    public void setPageChangeHandlers(List<? extends BiConsumer<Integer, Integer>> handlers) {
        pageChangeHandlers.clear();
        pageChangeHandlers.addAll(handlers);
    }
    
    public @UnmodifiableView List<BiConsumer<Integer, Integer>> getPageChangeHandlers() {
        return CollectionUtils.unmodifiableListUnchecked(pageChangeHandlers);
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
    public void setPageCountChangeHandlers(List<? extends BiConsumer<Integer, Integer>> handlers) {
        pageCountChangeHandlers.clear();
        pageCountChangeHandlers.addAll(handlers);
    }
    
    public @UnmodifiableView List<BiConsumer<Integer, Integer>> getPageCountChangeHandlers() {
        return CollectionUtils.unmodifiableListUnchecked(pageCountChangeHandlers);
    }
    
    @FunctionalInterface
    public interface Constructor<C> {
        PagedGui<C> create(
            Structure structure,
            MutableProperty<Integer> page,
            MutableProperty<List<? extends C>> content,
            MutableProperty<Boolean> frozen,
            MutableProperty<Boolean> ignoreObscuredInventorySlots,
            MutableProperty<@Nullable ItemProvider> background
        );
    }
    
    public static sealed abstract class AbstractBuilder<C>
        extends AbstractGui.AbstractBuilder<PagedGui<C>, PagedGui.Builder<C>>
        implements PagedGui.Builder<C>
        permits PagedItemsGuiImpl.Builder, PagedNestedGuiImpl.Builder, PagedInventoriesGuiImpl.Builder
    {
        
        private final Constructor<C> ctor;
        private MutableProperty<List<? extends C>> content = MutableProperty.of(List.of());
        private MutableProperty<Integer> page = MutableProperty.of(DEFAULT_PAGE);
        private List<BiConsumer<? super Integer, ? super Integer>> pageChangeHandlers = new ArrayList<>(0);
        private List<BiConsumer<? super Integer, ? super Integer>> pageCountChangeHandlers = new ArrayList<>(0);
        
        public AbstractBuilder(Constructor<C> ctor) {
            this.ctor = ctor;
        }
        
        @Override
        public PagedGui.Builder<C> setContent(MutableProperty<List<? extends C>> content) {
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
            
            var gui = ctor.create(structure, page, content, frozen, ignoreObscuredInventorySlots, background);
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
