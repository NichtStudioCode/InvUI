package xyz.xenondevs.invui.gui;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.gui.builder.GuiBuilder;
import xyz.xenondevs.invui.gui.impl.PagedItemsGuiImpl;
import xyz.xenondevs.invui.gui.impl.PagedNestedGuiImpl;
import xyz.xenondevs.invui.gui.structure.Structure;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * A {@link Gui} with pages.
 *
 * @see GuiBuilder
 * @see PagedItemsGuiImpl
 * @see PagedNestedGuiImpl
 */
public abstract class AbstractPagedGui<C> extends AbstractGui implements PagedGui<C> {
    
    private final boolean infinitePages;
    private final int[] contentListSlots;
    private int currentPage;
    
    private List<BiConsumer<Integer, Integer>> pageChangeHandlers;
    
    public AbstractPagedGui(int width, int height, boolean infinitePages, int... contentListSlots) {
        super(width, height);
        this.infinitePages = infinitePages;
        this.contentListSlots = contentListSlots;
    }
    
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
        int previous = currentPage;
        currentPage = page;
        update();
        if (previous != currentPage) {
            pageChangeHandlers.forEach(handler -> handler.accept(previous,  page));
        }
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
        correctPage();
        updateControlItems();
        updatePageContent();
    }
    
    private void correctPage() {
        if (currentPage == 0 || infinitePages) return;
        
        int pageAmount = getPageAmount();
        if (currentPage < 0 || pageAmount <= 0) currentPage = 0;
        else if (currentPage >= pageAmount) currentPage = pageAmount - 1;
    }
    
    private void updatePageContent() {
        List<SlotElement> slotElements = getPageElements(currentPage);
        
        for (int i = 0; i < contentListSlots.length; i++) {
            if (slotElements.size() > i) setSlotElement(contentListSlots[i], slotElements.get(i));
            else remove(contentListSlots[i]);
        }
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
    public void registerPageChangeHandler(@NotNull BiConsumer<Integer, Integer> pageChangeHandler) {
        if (pageChangeHandlers == null) {
            pageChangeHandlers = new ArrayList<>();
        }
        
        pageChangeHandlers.add(pageChangeHandler);
    }
    
    @Override
    public void unregisterPageChangeHandler(@NotNull BiConsumer<Integer, Integer> pageChangeHandler) {
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
    
    protected abstract List<SlotElement> getPageElements(int page);
    
}
