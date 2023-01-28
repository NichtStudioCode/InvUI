package xyz.xenondevs.invui.gui;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.gui.impl.PagedItemsGuiImpl;
import xyz.xenondevs.invui.gui.impl.PagedNestedGuiImpl;
import xyz.xenondevs.invui.gui.structure.Structure;
import xyz.xenondevs.invui.item.Item;

import java.util.List;
import java.util.function.BiConsumer;

@SuppressWarnings("deprecation")
public interface PagedGui<C> extends Gui {
    
    /**
     * Creates a new {@link PagedGui}.
     *
     * @param width            The width of the {@link PagedGui}.
     * @param height           The height of the {@link PagedGui}.
     * @param items            The {@link Item Items} to use as in pages.
     * @param contentListSlots The slots where content should be displayed.
     * @return The created {@link PagedGui}.
     */
    static @NotNull PagedGui<Item> ofItems(int width, int height, @NotNull List<@NotNull Item> items, int... contentListSlots) {
        return new PagedItemsGuiImpl(width, height, items, contentListSlots);
    }
    
    /**
     * Creates a new {@link PagedGui}.
     *
     * @param structure The {@link Structure} to use.
     * @param items     The {@link Item Items} to use as in pages.
     * @return The created {@link PagedGui}.
     */
    static @NotNull PagedGui<Item> ofItems(Structure structure, @NotNull List<@NotNull Item> items) {
        return new PagedItemsGuiImpl(items, structure);
    }
    
    /**
     * Creates a new {@link PagedGui}.
     *
     * @param width            The width of the {@link PagedGui}.
     * @param height           The height of the {@link PagedGui}.
     * @param guis             The {@link Gui Guis} to use as pages.
     * @param contentListSlots The slots where content should be displayed.
     * @return The created {@link PagedGui}.
     */
    static @NotNull PagedGui<Gui> ofGuis(int width, int height, @NotNull List<@NotNull Gui> guis, int... contentListSlots) {
        return new PagedNestedGuiImpl(width, height, guis, contentListSlots);
    }
    
    /**
     * Creates a new {@link PagedGui}.
     *
     * @param structure The {@link Structure} to use.
     * @param guis      The {@link Gui Guis} to use as pages.
     * @return The created {@link PagedGui}.
     */
    static @NotNull PagedGui<Gui> ofGuis(Structure structure, @NotNull List<@NotNull Gui> guis) {
        return new PagedNestedGuiImpl(guis, structure);
    }
    
    /**
     * Gets the amount of pages this {@link PagedGui} has.
     *
     * @return The amount of pages this {@link PagedGui} has.
     */
    int getPageAmount();
    
    /**
     * Gets the current page of this {@link PagedGui} as an index.
     *
     * @return Gets the current page of this {@link PagedGui} as an index.
     */
    int getCurrentPage();
    
    /**
     * Sets the current page of this {@link PagedGui}.
     *
     * @param page The page to set.
     */
    void setPage(int page);
    
    /**
     * Checks if there is a next page.
     *
     * @return Whether there is a next page.
     */
    boolean hasNextPage();
    
    /**
     * Checks if there is a previous page.
     *
     * @return Whether there is a previous page.
     */
    boolean hasPreviousPage();
    
    /**
     * Gets if there are infinite pages in this {@link PagedGui}.
     *
     * @return Whether there are infinite pages in this {@link PagedGui}.
     */
    boolean hasInfinitePages();
    
    /**
     * Displays the next page if there is one.
     */
    void goForward();
    
    /**
     * Displays the previous page if there is one.
     */
    void goBack();
    
    /**
     * Gets the slot indices that are used to display content in this {@link PagedGui}.
     *
     * @return The slot indices that are used to display content in this {@link PagedGui}.
     */
    int[] getContentListSlots();
    
    /**
     * Sets the content of this {@link PagedGui} for all pages.
     *
     * @param content The content to set.
     */
    void setContent(List<@Nullable C> content);
    
    /**
     * Gets the registered page change handlers.
     *
     * @return The registered page change handlers.
     */
    @Nullable List<@NotNull BiConsumer<Integer, Integer>> getPageChangeHandlers();
    
    /**
     * Replaces the currently registered page change handlers with the given list.
     *
     * @param handlers The new page change handlers.
     */
    void setPageChangeHandlers(@Nullable List<@NotNull BiConsumer<Integer, Integer>> handlers);
    
    /**
     * Registers a page change handler.
     *
     * @param handler The handler to register.
     */
    void registerPageChangeHandler(@NotNull BiConsumer<Integer, Integer> handler);
    
    /**
     * Unregisters a page change handler.
     *
     * @param handler The handler to unregister.
     */
    void unregisterPageChangeHandler(@NotNull BiConsumer<Integer, Integer> handler);
    
}
