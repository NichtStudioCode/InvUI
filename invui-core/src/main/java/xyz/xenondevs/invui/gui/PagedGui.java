package xyz.xenondevs.invui.gui;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.gui.structure.Structure;
import xyz.xenondevs.invui.inventory.Inventory;
import xyz.xenondevs.invui.item.Item;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * A {@link Gui} that can display multiple pages of content.
 *
 * @param <C> The content type
 */
public interface PagedGui<C> extends Gui {
    
    /**
     * Creates a new {@link Builder Gui Builder} for a {@link PagedGui} that uses {@link Item Items} as content.
     *
     * @return The new {@link Builder Gui Builder}.
     */
    static @NotNull Builder<@NotNull Item> items() {
        return new PagedItemsGuiImpl.Builder();
    }
    
    /**
     * Creates a new {@link PagedGui} after configuring a {@link Builder Gui Builder} using the given {@link Consumer}.
     *
     * @param consumer The {@link Consumer} to configure the {@link Builder Gui Builder}.
     * @return The created {@link PagedGui}.
     */
    static @NotNull PagedGui<@NotNull Item> items(@NotNull Consumer<@NotNull Builder<@NotNull Item>> consumer) {
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
    static @NotNull PagedGui<@NotNull Item> ofItems(int width, int height, @NotNull List<@NotNull Item> items, int... contentListSlots) {
        return new PagedItemsGuiImpl(width, height, items, contentListSlots);
    }
    
    /**
     * Creates a new {@link PagedGui}.
     *
     * @param structure The {@link Structure} to use.
     * @param items     The {@link Item Items} to use as in pages.
     * @return The created {@link PagedGui}.
     */
    static @NotNull PagedGui<@NotNull Item> ofItems(@NotNull Structure structure, @NotNull List<@NotNull Item> items) {
        return new PagedItemsGuiImpl(items, structure);
    }
    
    /**
     * Creates a new {@link Builder Gui Builder} for a {@link PagedGui} that uses {@link Gui Guis} as content.
     *
     * @return The new {@link Builder Gui Builder}.
     */
    static @NotNull Builder<@NotNull Gui> guis() {
        return new PagedNestedGuiImpl.Builder();
    }
    
    /**
     * Creates a new {@link PagedGui} after configuring a {@link Builder Gui Builder} using the given {@link Consumer}.
     *
     * @param consumer The {@link Consumer} to configure the {@link Builder Gui Builder}.
     * @return The created {@link PagedGui}.
     */
    static @NotNull PagedGui<@NotNull Gui> guis(@NotNull Consumer<@NotNull Builder<@NotNull Gui>> consumer) {
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
    static @NotNull PagedGui<@NotNull Gui> ofGuis(int width, int height, @NotNull List<@NotNull Gui> guis, int... contentListSlots) {
        return new PagedNestedGuiImpl(width, height, guis, contentListSlots);
    }
    
    /**
     * Creates a new {@link PagedGui}.
     *
     * @param structure The {@link Structure} to use.
     * @param guis      The {@link Gui Guis} to use as pages.
     * @return The created {@link PagedGui}.
     */
    static @NotNull PagedGui<@NotNull Gui> ofGuis(@NotNull Structure structure, @NotNull List<@NotNull Gui> guis) {
        return new PagedNestedGuiImpl(guis, structure);
    }
    
    /**
     * Creates a new {@link Builder Gui Builder} for a {@link PagedGui} that uses {@link Inventory Inventories} as content.
     *
     * @return The new {@link Builder Gui Builder}.
     */
    static @NotNull Builder<@NotNull Inventory> inventories() {
        return new PagedInventoriesGuiImpl.Builder();
    }
    
    /**
     * Creates a new {@link PagedGui} after configuring a {@link Builder Gui Builder} using the given {@link Consumer}.
     *
     * @param consumer The {@link Consumer} to configure the {@link Builder Gui Builder}.
     * @return The created {@link PagedGui}.
     */
    static @NotNull PagedGui<@NotNull Inventory> inventories(@NotNull Consumer<@NotNull Builder<@NotNull Inventory>> consumer) {
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
    static @NotNull PagedGui<@NotNull Inventory> ofInventories(int width, int height, @NotNull List<@NotNull Inventory> inventories, int... contentListSlots) {
        return new PagedInventoriesGuiImpl(width, height, inventories, contentListSlots);
    }
    
    /**
     * Creates a new {@link PagedGui}.
     *
     * @param structure   The {@link Structure} to use.
     * @param inventories The {@link Inventory Inventories} to use as pages.
     * @return The created {@link PagedGui}.
     */
    static @NotNull PagedGui<@NotNull Inventory> ofInventories(@NotNull Structure structure, @NotNull List<@NotNull Inventory> inventories) {
        return new PagedInventoriesGuiImpl(inventories, structure);
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
    void setContent(@Nullable List<@NotNull C> content);
    
    /**
     * Bakes and updates the pages of this {@link PagedGui} based on the current content.
     * <p>
     * This method does not need to be called when using {@link #setContent(List)},
     * but is required when the size of the content itself changes.
     */
    void bake();
    
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
    void addPageChangeHandler(@NotNull BiConsumer<Integer, Integer> handler);
    
    /**
     * Unregisters a page change handler.
     *
     * @param handler The handler to unregister.
     */
    void removePageChangeHandler(@NotNull BiConsumer<Integer, Integer> handler);
    
    /**
     * A {@link PagedGui} builder.
     *
     * @param <C> The content type.
     */
    interface Builder<C> extends Gui.Builder<PagedGui<C>, Builder<C>> {
        
        /**
         * Sets the content of the {@link PagedGui} for all pages.
         *
         * @param content The content to set.
         * @return This {@link Builder Gui Builder}.
         */
        @Contract("_ -> this")
        @NotNull Builder<C> setContent(@NotNull List<@NotNull C> content);
        
        /**
         * Adds content to the {@link PagedGui}.
         *
         * @param content The content to add.
         * @return This {@link Builder Gui Builder}.
         */
        @Contract("_ -> this")
        @NotNull Builder<C> addContent(@NotNull C content);
        
        /**
         * Sets the page change handlers of the {@link PagedGui}.
         *
         * @param handlers The page change handlers to set.
         * @return This {@link Builder Gui Builder}.
         */
        @Contract("_ -> this")
        @NotNull Builder<C> setPageChangeHandlers(@NotNull List<@NotNull BiConsumer<Integer, Integer>> handlers);
        
        /**
         * Adds a page change handler to the {@link PagedGui}.
         *
         * @param handler The page change handler to add.
         * @return This {@link Builder Gui Builder}.
         */
        @Contract("_ -> this")
        @NotNull Builder<C> addPageChangeHandler(@NotNull BiConsumer<Integer, Integer> handler);
        
    }
    
}
