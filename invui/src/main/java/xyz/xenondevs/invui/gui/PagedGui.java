package xyz.xenondevs.invui.gui;

import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.inventory.Inventory;
import xyz.xenondevs.invui.item.Item;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A {@link Gui} that can display multiple pages of content.
 *
 * @param <C> The content type
 */
public sealed interface PagedGui<C> extends Gui permits AbstractPagedGui {
    
    /**
     * Creates a new {@link Builder Gui Builder} for a {@link PagedGui} that uses {@link Item Items} as content.
     *
     * @return The new {@link Builder Gui Builder}.
     */
    static Builder<Item> items() {
        return new PagedItemsGuiImpl.Builder<>();
    }
    
    /**
     * Creates a new {@link PagedGui} after configuring a {@link Builder Gui Builder} using the given {@link Consumer}.
     *
     * @param consumer The {@link Consumer} to configure the {@link Builder Gui Builder}.
     * @return The created {@link PagedGui}.
     */
    static PagedGui<Item> items(Consumer<Builder<Item>> consumer) {
        Builder<Item> builder = items();
        consumer.accept(builder);
        return builder.build();
    }
    
    /**
     * Creates a new {@link PagedGui}.
     *
     * @param width            The width of the {@link PagedGui}.
     * @param height           The height of the {@link PagedGui}.
     * @param items            The {@link Item Items} to use as in pages.
     * @param contentListSlots The slots where content should be displayed.
     * @return The created {@link PagedGui}.
     */
    static PagedGui<Item> ofItems(int width, int height, List<? extends Item> items, int... contentListSlots) {
        return new PagedItemsGuiImpl<>(width, height, items, contentListSlots);
    }
    
    /**
     * Creates a new {@link PagedGui}.
     *
     * @param structure The {@link Structure} to use.
     * @param items     The {@link Item Items} to use as in pages.
     * @return The created {@link PagedGui}.
     */
    static PagedGui<Item> ofItems(Structure structure, List<? extends Item> items) {
        return new PagedItemsGuiImpl<>(() -> items, structure);
    }
    
    /**
     * Creates a new {@link Builder Gui Builder} for a {@link PagedGui} that uses {@link Gui Guis} as content.
     *
     * @return The new {@link Builder Gui Builder}.
     */
    static Builder<Gui> guis() {
        return new PagedNestedGuiImpl.Builder<>();
    }
    
    /**
     * Creates a new {@link PagedGui} after configuring a {@link Builder Gui Builder} using the given {@link Consumer}.
     *
     * @param consumer The {@link Consumer} to configure the {@link Builder Gui Builder}.
     * @return The created {@link PagedGui}.
     */
    static PagedGui<Gui> guis(Consumer<Builder<Gui>> consumer) {
        Builder<Gui> builder = guis();
        consumer.accept(builder);
        return builder.build();
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
    static PagedGui<Gui> ofGuis(int width, int height, List<? extends Gui> guis, int... contentListSlots) {
        return new PagedNestedGuiImpl<>(width, height, guis, contentListSlots);
    }
    
    /**
     * Creates a new {@link PagedGui}.
     *
     * @param structure The {@link Structure} to use.
     * @param guis      The {@link Gui Guis} to use as pages.
     * @return The created {@link PagedGui}.
     */
    static PagedGui<Gui> ofGuis(Structure structure, List<? extends Gui> guis) {
        return new PagedNestedGuiImpl<>(() -> guis, structure);
    }
    
    /**
     * Creates a new {@link Builder Gui Builder} for a {@link PagedGui} that uses {@link Inventory Inventories} as content.
     *
     * @return The new {@link Builder Gui Builder}.
     */
    static Builder<Inventory> inventories() {
        return new PagedInventoriesGuiImpl.Builder<>();
    }
    
    /**
     * Creates a new {@link PagedGui} after configuring a {@link Builder Gui Builder} using the given {@link Consumer}.
     *
     * @param consumer The {@link Consumer} to configure the {@link Builder Gui Builder}.
     * @return The created {@link PagedGui}.
     */
    static PagedGui<Inventory> inventories(Consumer<Builder<Inventory>> consumer) {
        Builder<Inventory> builder = inventories();
        consumer.accept(builder);
        return builder.build();
    }
    
    /**
     * Creates a new {@link PagedGui}.
     *
     * @param width            The width of the {@link PagedGui}.
     * @param height           The height of the {@link PagedGui}.
     * @param inventories      The {@link Inventory Inventories} to use as pages.
     * @param contentListSlots The slots where content should be displayed.
     * @return The created {@link PagedGui}.
     */
    static PagedGui<Inventory> ofInventories(int width, int height, List<? extends Inventory> inventories, int... contentListSlots) {
        return new PagedInventoriesGuiImpl<>(width, height, inventories, contentListSlots);
    }
    
    /**
     * Creates a new {@link PagedGui}.
     *
     * @param structure   The {@link Structure} to use.
     * @param inventories The {@link Inventory Inventories} to use as pages.
     * @return The created {@link PagedGui}.
     */
    static PagedGui<Inventory> ofInventories(Structure structure, List<? extends Inventory> inventories) {
        return new PagedInventoriesGuiImpl<>(() -> inventories, structure);
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
     * Sets the supplier used to retrieve the content of this {@link PagedGui} for all pages.
     * Refreshes can be triggered via {@link PagedGui#bake}.
     *
     * @param contentSupplier The content supplier to set.
     */
    void setContent(Supplier<? extends List<? extends C>> contentSupplier);
    
    /**
     * Sets the content of this {@link PagedGui} for all pages.
     *
     * @param content The content to set.
     */
    void setContent(List<? extends C> content);
    
    /**
     * Gets the content of this {@link PagedGui}.
     *
     * @return The content of this {@link PagedGui}.
     */
    List<? extends C> getContent();
    
    /**
     * Bakes and updates the pages of this {@link PagedGui} based on the current content.
     * <p>
     * This method does not need to be called when using {@link #setContent(List)},
     * but is required when the content itself changes.
     */
    void bake();
    
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
    void addPageChangeHandler(BiConsumer<Integer, Integer> handler);
    
    /**
     * Unregisters a page change handler.
     *
     * @param handler The handler to unregister.
     */
    void removePageChangeHandler(BiConsumer<Integer, Integer> handler);
    
    /**
     * Gets the registered page count change handlers.
     *
     * @return The registered page count change handlers.
     */
    @Nullable
    List<BiConsumer<Integer, Integer>> getPageCountChangeHandlers();
    
    /**
     * Replaces the currently registered page count change handlers with the given list.
     *
     * @param handlers The new page count change handlers.
     */
    void setPageCountChangeHandlers(@Nullable List<BiConsumer<Integer, Integer>> handlers);
    
    /**
     * Registers a page count change handler.
     *
     * @param handler The handler to register.
     */
    void addPageCountChangeHandler(BiConsumer<Integer, Integer> handler);
    
    /**
     * Unregisters a page count change handler.
     *
     * @param handler The handler to unregister.
     */
    void removePageCountChangeHandler(BiConsumer<Integer, Integer> handler);
    
    /**
     * A {@link PagedGui} builder.
     *
     * @param <C> The content type.
     */
    sealed interface Builder<C> extends Gui.Builder<PagedGui<C>, Builder<C>> permits AbstractPagedGui.AbstractBuilder {
        
        /**
         * Sets the supplier used to retrieve the content of the {@link PagedGui} for all pages.
         * Refreshes can be triggered via {@link PagedGui#bake}.
         *
         * @param contentSupplier The content supplier to set.
         * @return This {@link Builder Gui Builder}.
         */
        Builder<C> setContent(Supplier<? extends List<C>> contentSupplier);
        
        /**
         * Sets the content of the {@link PagedGui} for all pages.
         *
         * @param content The content to set.
         * @return This {@link Builder Gui Builder}.
         */
        Builder<C> setContent(List<C> content);
        
        /**
         * Adds content to the {@link PagedGui}.
         *
         * @param content The content to add.
         * @return This {@link Builder Gui Builder}.
         */
        Builder<C> addContent(C content);
        
        /**
         * Sets the page change handlers of the {@link PagedGui}.
         *
         * @param handlers The page change handlers to set.
         * @return This {@link Builder Gui Builder}.
         */
        Builder<C> setPageChangeHandlers(List<BiConsumer<Integer, Integer>> handlers);
        
        /**
         * Adds a page change handler to the {@link PagedGui}.
         *
         * @param handler The page change handler to add.
         * @return This {@link Builder Gui Builder}.
         */
        Builder<C> addPageChangeHandler(BiConsumer<Integer, Integer> handler);
        
        /**
         * Sets the page count change handlers of the {@link PagedGui}.
         *
         * @param handlers The page change handlers to set.
         * @return This {@link Builder Gui Builder}.
         */
        Builder<C> setPageCountChangeHandlers(List<BiConsumer<Integer, Integer>> handlers);
        
        /**
         * Adds a page count change handler to the {@link PagedGui}.
         *
         * @param handler The page change handler to add.
         * @return This {@link Builder Gui Builder}.
         */
        Builder<C> addPageCountChangeHandler(BiConsumer<Integer, Integer> handler);
        
    }
    
}
