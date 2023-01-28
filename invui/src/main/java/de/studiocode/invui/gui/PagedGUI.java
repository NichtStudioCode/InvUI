package de.studiocode.invui.gui;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BiConsumer;

public interface PagedGUI<C> extends GUI {
    
    /**
     * Gets the amount of pages this {@link PagedGUI} has.
     *
     * @return The amount of pages this {@link PagedGUI} has.
     */
    int getPageAmount();
    
    /**
     * Gets the current page of this {@link PagedGUI} as an index.
     *
     * @return Gets the current page of this {@link PagedGUI} as an index.
     */
    int getCurrentPage();
    
    /**
     * Sets the current page of this {@link PagedGUI}.
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
     * Gets if there are infinite pages in this {@link PagedGUI}.
     *
     * @return Whether there are infinite pages in this {@link PagedGUI}.
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
     * Gets the slot indices that are used to display content in this {@link PagedGUI}.
     *
     * @return The slot indices that are used to display content in this {@link PagedGUI}.
     */
    int[] getContentListSlots();
    
    /**
     * Sets the content of this {@link PagedGUI} for all pages.
     *
     * @param content The content to set.
     */
    void setContent(List<@Nullable C> content);
    
    /**
     * Gets the registered page change handlers.
     *
     * @return The registered page change handlers.
     */
    @Nullable
    List<BiConsumer<Integer, Integer>> getPageChangeHandlers();
    
    /**
     * Replaces the currently registered page change handlers with the given list.
     *
     * @param handlers The new page change handlers.
     */
    void setPageChangeHandlers(@Nullable List<BiConsumer<Integer, Integer>> handlers);
    
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
