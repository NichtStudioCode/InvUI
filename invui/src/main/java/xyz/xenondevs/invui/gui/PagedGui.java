package xyz.xenondevs.invui.gui;

import org.jetbrains.annotations.Unmodifiable;
import org.jetbrains.annotations.UnmodifiableView;
import xyz.xenondevs.invui.inventory.Inventory;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.state.MutableProperty;
import xyz.xenondevs.invui.state.Property;

import java.util.List;
import java.util.function.BiConsumer;

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
    static Builder<Item> itemsBuilder() {
        return new PagedItemsGuiImpl.Builder<>();
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
    static PagedGui<Item> ofItems(int width, int height, List<? extends Item> items, List<? extends Slot> contentListSlots) {
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
        var gui = ofItems(structure.getWidth(), structure.getHeight(), items, List.of());
        gui.applyStructure(structure);
        return gui;
    }
    
    /**
     * Creates a new {@link Builder Gui Builder} for a {@link PagedGui} that uses {@link Gui Guis} as content.
     *
     * @return The new {@link Builder Gui Builder}.
     */
    static Builder<Gui> guisBuilder() {
        return new PagedNestedGuiImpl.Builder<>();
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
    static PagedGui<Gui> ofGuis(int width, int height, List<? extends Gui> guis, List<? extends Slot> contentListSlots) {
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
        var gui = ofGuis(structure.getWidth(), structure.getHeight(), guis, List.of());
        gui.applyStructure(structure);
        return gui;
    }
    
    /**
     * Creates a new {@link Builder Gui Builder} for a {@link PagedGui} that uses {@link Inventory Inventories} as content.
     *
     * @return The new {@link Builder Gui Builder}.
     */
    static Builder<Inventory> inventoriesBuilder() {
        return new PagedInventoriesGuiImpl.Builder<>();
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
    static PagedGui<Inventory> ofInventories(int width, int height, List<? extends Inventory> inventories, List<? extends Slot> contentListSlots) {
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
        var gui = ofInventories(structure.getWidth(), structure.getHeight(), inventories, List.of());
        gui.applyStructure(structure);
        return gui;
    }
    
    /**
     * Sets the slots at which page content should be displayed, in order of appearance.
     *
     * @param slots The slots to set.
     */
    void setContentListSlots(List<? extends Slot> slots);
    
    /**
     * Gets the slots that are used to display content in this {@link PagedGui}.
     *
     * @return The slots that are used to display content in this {@link PagedGui}.
     */
    @Unmodifiable
    List<Slot> getContentListSlots();
    
    /**
     * Gets the property containing the currently selected page.
     *
     * @return The page property.
     */
    MutableProperty<Integer> getPageProperty();
    
    /**
     * Gets the current page of this {@link PagedGui} as an index.
     *
     * @return Gets the current page of this {@link PagedGui} as an index.
     */
    int getPage();
    
    /**
     * Sets the current page of this {@link PagedGui}.
     *
     * @param page The page to set.
     */
    void setPage(int page);
    
    /**
     * Gets the property containing the page count.
     *
     * @return The page count property.
     */
    Property<Integer> getPageCountProperty();
    
    /**
     * Gets the amount of pages this {@link PagedGui} has.
     *
     * @return The amount of pages this {@link PagedGui} has.
     */
    int getPageCount();
    
    /**
     * Sets the content of this {@link PagedGui} for all pages.
     *
     * @param content The content to set.
     */
    void setContent(List<? extends C> content);
    
    /**
     * Gets the property containing the page content.
     *
     * @return The content property.
     */
    MutableProperty<List<? extends C>> getContentProperty();
    
    /**
     * Gets the content of this {@link PagedGui}.
     *
     * @return The content of this {@link PagedGui}.
     */
    @UnmodifiableView
    List<C> getContent();
    
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
    @UnmodifiableView
    List<BiConsumer<Integer, Integer>> getPageChangeHandlers();
    
    /**
     * Replaces the currently registered page change handlers with the given list.
     *
     * @param handlers The new page change handlers.
     */
    void setPageChangeHandlers(List<? extends BiConsumer<Integer, Integer>> handlers);
    
    /**
     * Registers a page change handler.
     *
     * @param handler The handler to register.
     */
    void addPageChangeHandler(BiConsumer<? super Integer, ? super Integer> handler);
    
    /**
     * Unregisters a page change handler.
     *
     * @param handler The handler to unregister.
     */
    void removePageChangeHandler(BiConsumer<? super Integer, ? super Integer> handler);
    
    /**
     * Gets the registered page count change handlers.
     *
     * @return The registered page count change handlers.
     */
    @UnmodifiableView
    List<BiConsumer<Integer, Integer>> getPageCountChangeHandlers();
    
    /**
     * Replaces the currently registered page count change handlers with the given list.
     *
     * @param handlers The new page count change handlers.
     */
    void setPageCountChangeHandlers(List<? extends BiConsumer<Integer, Integer>> handlers);
    
    /**
     * Registers a page count change handler.
     *
     * @param handler The handler to register.
     */
    void addPageCountChangeHandler(BiConsumer<? super Integer, ? super Integer> handler);
    
    /**
     * Unregisters a page count change handler.
     *
     * @param handler The handler to unregister.
     */
    void removePageCountChangeHandler(BiConsumer<? super Integer, ? super Integer> handler);
    
    /**
     * A {@link PagedGui} builder.
     *
     * @param <C> The content type.
     */
    sealed interface Builder<C> extends Gui.Builder<PagedGui<C>, Builder<C>> permits AbstractPagedGui.AbstractBuilder {
        
        /**
         * Sets the property that contains the content of the {@link PagedGui}.
         *
         * @param content The content property to set.
         * @return This {@link Builder Gui Builder}.
         */
        Builder<C> setContent(MutableProperty<List<? extends C>> content);
        
        /**
         * Sets the content of the {@link PagedGui} for all pages.
         *
         * @param content The content to set.
         * @return This {@link Builder Gui Builder}.
         */
        default Builder<C> setContent(List<? extends C> content) {
            return setContent(MutableProperty.of(content));
        }
        
        /**
         * Sets the property that contains the page of the {@link PagedGui}.
         *
         * @param page The page property to set.
         * @return This {@link Builder Gui Builder}.
         */
        Builder<C> setPage(MutableProperty<Integer> page);
        
        /**
         * Sets the page change handlers of the {@link PagedGui}.
         *
         * @param handlers The page change handlers to set.
         * @return This {@link Builder Gui Builder}.
         */
        Builder<C> setPageChangeHandlers(List<? extends BiConsumer<? super Integer, ? super Integer>> handlers);
        
        /**
         * Adds a page change handler to the {@link PagedGui}.
         *
         * @param handler The page change handler to add.
         * @return This {@link Builder Gui Builder}.
         */
        Builder<C> addPageChangeHandler(BiConsumer<? super Integer, ? super Integer> handler);
        
        /**
         * Sets the page count change handlers of the {@link PagedGui}.
         *
         * @param handlers The page change handlers to set.
         * @return This {@link Builder Gui Builder}.
         */
        Builder<C> setPageCountChangeHandlers(List<? extends BiConsumer<? super Integer, ? super Integer>> handlers);
        
        /**
         * Adds a page count change handler to the {@link PagedGui}.
         *
         * @param handler The page change handler to add.
         * @return This {@link Builder Gui Builder}.
         */
        Builder<C> addPageCountChangeHandler(BiConsumer<? super Integer, ? super Integer> handler);
        
    }
    
}
