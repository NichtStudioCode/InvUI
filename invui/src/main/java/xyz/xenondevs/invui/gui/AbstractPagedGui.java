package xyz.xenondevs.invui.gui;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.gui.structure.Structure;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * A {@link Gui} with pages.
 * <p>
 * Only in very rare circumstances should this class be used directly.
 * Instead, use the static builder or factory functions from the {@link PagedGui} interface,
 * such as {@link PagedGui#items()}, {@link PagedGui#guis()} or, {@link PagedGui#inventories()}
 * to create a new {@link PagedGui}.
 *
 * @param <C> The content type.
 * @see PagedItemsGuiImpl
 * @see PagedNestedGuiImpl
 */
public abstract class AbstractPagedGui<C> extends AbstractGui implements PagedGui<C> {
    
    private final boolean infinitePages;
    private final int[] contentListSlots;
    private int currentPage;
    
    private List<BiConsumer<Integer, Integer>> pageChangeHandlers;
    /**
     * The content of the gui, to be displayed on the pages.
     */
    protected List<C> content;
    /**
     * The baked pages of the gui, containing the content.
     */
    protected List<List<SlotElement>> pages;
    
    /**
     * Creates a new {@link AbstractPagedGui}.
     *
     * @param width            The width of the gui.
     * @param height           The height of the gui.
     * @param infinitePages    Whether the gui has infinite pages.
     * @param contentListSlots The slots to be used for pages.
     */
    public AbstractPagedGui(int width, int height, boolean infinitePages, int... contentListSlots) {
        super(width, height);
        this.infinitePages = infinitePages;
        this.contentListSlots = contentListSlots;
    }
    
    /**
     * Creates a new {@link AbstractPagedGui}.
     *
     * @param width         The width of the gui.
     * @param height        The height of the gui.
     * @param infinitePages Whether the gui has infinite pages.
     * @param structure     The structure of the gui.
     */
    public AbstractPagedGui(int width, int height, boolean infinitePages, Structure structure) {
        this(width, height, infinitePages, structure.getIngredientList().findContentListSlots());
        applyStructure(structure);
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
    
    /**
     * Updates the gui, by first correcting the current page
     * and then updating all relevant items.
     */
    protected void update() {
        correctCurrentPage();
        updateControlItems();
        updatePageContent();
    }
    
    private void updatePageContent() {
        List<SlotElement> slotElements = (pages != null && !pages.isEmpty()) ? pages.get(currentPage) : List.of();
        
        for (int i = 0; i < contentListSlots.length; i++) {
            if (slotElements.size() > i) setSlotElement(contentListSlots[i], slotElements.get(i));
            else remove(contentListSlots[i]);
        }
    }
    
    @Override
    public void setContent(@Nullable List<C> content) {
        if (content == null || content.isEmpty()) {
            this.content = List.of();
            this.pages = List.of();
            update();
        } else {
            this.content = content;
            bake(); // calls update()
        }
    }
    
    @Override
    public int getPageAmount() {
        return pages != null ? pages.size() : 0;
    }
    
    @Override
    public int getCurrentPage() {
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
    public void addPageChangeHandler(@NotNull BiConsumer<Integer, Integer> pageChangeHandler) {
        if (pageChangeHandlers == null) {
            pageChangeHandlers = new ArrayList<>();
        }
        
        pageChangeHandlers.add(pageChangeHandler);
    }
    
    @Override
    public void removePageChangeHandler(@NotNull BiConsumer<Integer, Integer> pageChangeHandler) {
        if (pageChangeHandlers != null) {
            pageChangeHandlers.remove(pageChangeHandler);
        }
    }
    
    @Override
    public void setPageChangeHandlers(@Nullable List<@NotNull BiConsumer<Integer, Integer>> handlers) {
        this.pageChangeHandlers = handlers;
    }
    
    @Nullable
    public List<BiConsumer<Integer, Integer>> getPageChangeHandlers() {
        return pageChangeHandlers;
    }
    
    /**
     * Builder for {@link AbstractPagedGui}.
     * <p>
     * This class should only be used directly if you're creating a custom {@link AbstractBuilder} implementation.
     * Otherwise, use the static builder functions from {@link PagedGui}, such as
     * {@link PagedGui#items()}, {@link PagedGui#guis()} or {@link PagedGui#inventories()} to obtain a builder instance.
     *
     * @param <C> The content type.
     */
    public static abstract class AbstractBuilder<C>
        extends AbstractGui.AbstractBuilder<PagedGui<C>, PagedGui.Builder<C>>
        implements PagedGui.Builder<C>
    {
        
        /**
         * The content of the {@link AbstractPagedGui}.
         */
        protected List<C> content;
        /**
         * The page change handlers of the {@link AbstractPagedGui}.
         */
        protected List<BiConsumer<Integer, Integer>> pageChangeHandlers;
        
        @Override
        public PagedGui.@NotNull Builder<C> setContent(@NotNull List<@NotNull C> content) {
            this.content = content;
            return this;
        }
        
        @Override
        public PagedGui.@NotNull Builder<C> addContent(@NotNull C content) {
            if (this.content == null)
                this.content = new ArrayList<>();
            
            this.content.add(content);
            return this;
        }
        
        @Override
        public PagedGui.@NotNull Builder<C> setPageChangeHandlers(@NotNull List<@NotNull BiConsumer<Integer, Integer>> handlers) {
            pageChangeHandlers = handlers;
            return this;
        }
        
        @Override
        public PagedGui.@NotNull Builder<C> addPageChangeHandler(@NotNull BiConsumer<Integer, Integer> handler) {
            if (pageChangeHandlers == null)
                pageChangeHandlers = new ArrayList<>(1);
            
            pageChangeHandlers.add(handler);
            return this;
        }
        
        @Override
        protected void applyModifiers(@NotNull PagedGui<C> gui) {
            super.applyModifiers(gui);
            gui.setPageChangeHandlers(pageChangeHandlers);
        }
        
        @Override
        public @NotNull PagedGui.Builder<C> clone() {
            var clone = (AbstractBuilder<C>) super.clone();
            if (this.content != null)
                clone.content = new ArrayList<>(this.content);
            if (this.pageChangeHandlers != null)
                clone.pageChangeHandlers = new ArrayList<>(this.pageChangeHandlers);
            return clone;
        }
        
    }
    
}
