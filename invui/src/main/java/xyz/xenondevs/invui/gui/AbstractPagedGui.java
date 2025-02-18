package xyz.xenondevs.invui.gui;

import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.internal.util.SlotUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Supplier;

sealed abstract class AbstractPagedGui<C>
    extends AbstractGui
    implements PagedGui<C>
    permits PagedInventoriesGuiImpl, PagedItemsGuiImpl, PagedNestedGuiImpl
{
    
    private final boolean infinitePages;
    private int[] contentListSlots;
    private int currentPage;
    
    private @Nullable List<BiConsumer<Integer, Integer>> pageChangeHandlers;
    private @Nullable List<BiConsumer<Integer, Integer>> pageCountChangeHandlers;
    private Supplier<? extends List<? extends C>> contentSupplier = List::of;
    private @Nullable List<List<SlotElement>> pages;
    
    public AbstractPagedGui(int width, int height, boolean infinitePages, int... contentListSlots) {
        super(width, height);
        this.infinitePages = infinitePages;
        this.contentListSlots = contentListSlots.clone();
    }
    
    public AbstractPagedGui(int width, int height, boolean infinitePages, Structure structure) {
        super(width, height);
        this.infinitePages = infinitePages;
        this.contentListSlots = structure.getIngredientMatrix().findContentListSlots();
        super.applyStructure(structure); // super call to avoid bake() through applyStructure override
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
        this.contentListSlots = slotIndices.clone();
        bake();
    }
    
    @Override
    public void goForward() {
        if (hasNextPage())
            setPage(currentPage + 1);
    }
    
    @Override
    public void goBack() {
        if (hasPreviousPage())
            setPage(currentPage - 1);
    }
    
    @Override
    public void setPage(int page) {
        int previousPage = currentPage;
        int newPage = correctPage(page);
        
        if (previousPage == newPage)
            return;
        
        currentPage = newPage;
        update();
        
        if (pageChangeHandlers != null) {
            pageChangeHandlers.forEach(handler -> handler.accept(previousPage, newPage));
        }
    }
    
    private int correctPage(int page) {
        // page 0 always exist, every positive page exist for infinite pages
        if (page == 0 || (infinitePages && page > 0))
            return page;
        
        // 0 <= page < pageAmount
        return Math.max(0, Math.min(page, getPageAmount() - 1));
    }
    
    private void correctCurrentPage() {
        int correctedPage = correctPage(currentPage);
        if (correctedPage != currentPage)
            setPage(correctedPage);
    }
    
    @Override
    public boolean hasNextPage() {
        return currentPage < getPageAmount() - 1 || infinitePages;
    }
    
    @Override
    public boolean hasPreviousPage() {
        return currentPage > 0;
    }
    
    protected void update() {
        correctCurrentPage();
        updatePageContent();
    }
    
    private void updatePageContent() {
        List<SlotElement> slotElements = (pages != null && !pages.isEmpty()) ? pages.get(currentPage) : List.of();
        
        for (int i = 0; i < contentListSlots.length; i++) {
            if (slotElements.size() > i)
                setSlotElement(contentListSlots[i], slotElements.get(i));
            else remove(contentListSlots[i]);
        }
    }
    
    @Override
    public void setContentSupplier(Supplier<? extends List<? extends C>> contentSupplier) {
        this.contentSupplier = contentSupplier;
        bake();
    }
    
    @Override
    public void setContent(List<? extends C> content) {
        setContentSupplier(() -> content);
    }
    
    public void setPages(@Nullable List<List<SlotElement>> pages) {
        int prevPageCount = getPageAmount();
        this.pages = pages;
        int newPageCount = getPageAmount();
        
        if (pageCountChangeHandlers != null) {
            for (var handler : pageCountChangeHandlers) {
                handler.accept(prevPageCount, newPageCount);
            }
        }
    }
    
    @Override
    public List<? extends C> getContent() {
        return contentSupplier.get();
    }
    
    @Override
    public int getPageAmount() {
        return pages != null ? pages.size() : 0;
    }
    
    @Override
    public int getPage() {
        return currentPage;
    }
    
    @Override
    public boolean hasInfinitePages() {
        return infinitePages;
    }
    
    @Override
    public int[] getContentListSlots() {
        return contentListSlots;
    }
    
    @Override
    public void addPageChangeHandler(BiConsumer<Integer, Integer> pageChangeHandler) {
        if (pageChangeHandlers == null) {
            pageChangeHandlers = new ArrayList<>();
        }
        
        pageChangeHandlers.add(pageChangeHandler);
    }
    
    @Override
    public void removePageChangeHandler(BiConsumer<Integer, Integer> pageChangeHandler) {
        if (pageChangeHandlers != null) {
            pageChangeHandlers.remove(pageChangeHandler);
        }
    }
    
    @Override
    public void setPageChangeHandlers(@Nullable List<BiConsumer<Integer, Integer>> handlers) {
        this.pageChangeHandlers = handlers;
    }
    
    @Nullable
    public List<BiConsumer<Integer, Integer>> getPageChangeHandlers() {
        return pageChangeHandlers;
    }
    
    @Override
    public void addPageCountChangeHandler(BiConsumer<Integer, Integer> pageChangeHandler) {
        if (pageCountChangeHandlers == null) {
            pageCountChangeHandlers = new ArrayList<>();
        }
        
        pageCountChangeHandlers.add(pageChangeHandler);
    }
    
    @Override
    public void removePageCountChangeHandler(BiConsumer<Integer, Integer> pageChangeHandler) {
        if (pageCountChangeHandlers != null) {
            pageCountChangeHandlers.remove(pageChangeHandler);
        }
    }
    
    @Override
    public void setPageCountChangeHandlers(@Nullable List<BiConsumer<Integer, Integer>> handlers) {
        this.pageCountChangeHandlers = handlers;
    }
    
    @Nullable
    public List<BiConsumer<Integer, Integer>> getPageCountChangeHandlers() {
        return pageCountChangeHandlers;
    }
    
    public static sealed abstract class AbstractBuilder<C>
        extends AbstractGui.AbstractBuilder<PagedGui<C>, PagedGui.Builder<C>>
        implements PagedGui.Builder<C>
        permits PagedItemsGuiImpl.Builder, PagedNestedGuiImpl.Builder, PagedInventoriesGuiImpl.Builder
    {
        
        private final BiFunction<Supplier<? extends List<? extends C>>, Structure, PagedGui<C>> ctor;
        private @Nullable Supplier<? extends List<C>> contentSupplier;
        private @Nullable List<C> content = null;
        private @Nullable List<BiConsumer<Integer, Integer>> pageChangeHandlers;
        private @Nullable List<BiConsumer<Integer, Integer>> pageCountChangeHandlers;
        
        public AbstractBuilder(BiFunction<Supplier<? extends List<? extends C>>, Structure, PagedGui<C>> ctor) {
            this.ctor = ctor;
        }
        
        @Override
        public PagedGui.Builder<C> setContentSupplier(Supplier<? extends List<C>> contentSupplier) {
            this.contentSupplier = contentSupplier;
            return this;
        }
        
        @Override
        public PagedGui.Builder<C> setContent(List<C> content) {
            this.content = content;
            return this;
        }
        
        @Override
        public PagedGui.Builder<C> addContent(C content) {
            if (this.content == null)
                this.content = new ArrayList<>();
            
            this.content.add(content);
            return this;
        }
        
        @Override
        public PagedGui.Builder<C> setPageChangeHandlers(List<BiConsumer<Integer, Integer>> handlers) {
            pageChangeHandlers = handlers;
            return this;
        }
        
        @Override
        public PagedGui.Builder<C> addPageChangeHandler(BiConsumer<Integer, Integer> handler) {
            if (pageChangeHandlers == null)
                pageChangeHandlers = new ArrayList<>(1);
            
            pageChangeHandlers.add(handler);
            return this;
        }
        
        @Override
        public PagedGui.Builder<C> setPageCountChangeHandlers(List<BiConsumer<Integer, Integer>> handlers) {
            pageCountChangeHandlers = handlers;
            return this;
        }
        
        @Override
        public PagedGui.Builder<C> addPageCountChangeHandler(BiConsumer<Integer, Integer> handler) {
            if (pageCountChangeHandlers == null)
                pageCountChangeHandlers = new ArrayList<>(1);
            
            pageCountChangeHandlers.add(handler);
            return this;
        }
        
        @Override
        public PagedGui<C> build() {
            if (structure == null)
                throw new IllegalStateException("Structure is not defined.");
            
            Supplier<? extends List<C>> supplier = contentSupplier != null 
                ? contentSupplier 
                : () -> content != null ? content : List.of();
            
            var gui = ctor.apply(supplier, structure);
            
            if (pageChangeHandlers != null) {
                for (var handler : pageChangeHandlers) {
                    gui.addPageChangeHandler(handler);
                }
            }
            if (pageCountChangeHandlers != null) {
                for (var handler : pageCountChangeHandlers) {
                    gui.addPageChangeHandler(handler);
                }
            }
            
            applyModifiers(gui);
            
            return gui;
        }
        
        @Override
        public PagedGui.Builder<C> clone() {
            var clone = (AbstractBuilder<C>) super.clone();
            if (this.content != null)
                clone.content = new ArrayList<>(this.content);
            if (this.pageChangeHandlers != null)
                clone.pageChangeHandlers = new ArrayList<>(this.pageChangeHandlers);
            return clone;
        }
        
    }
    
}
