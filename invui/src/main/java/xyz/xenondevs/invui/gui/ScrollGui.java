package xyz.xenondevs.invui.gui;

import org.jetbrains.annotations.Unmodifiable;
import org.jetbrains.annotations.UnmodifiableView;
import xyz.xenondevs.invui.inventory.Inventory;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.state.MutableProperty;
import xyz.xenondevs.invui.state.Property;

import java.util.List;
import java.util.SequencedSet;
import java.util.function.BiConsumer;

/**
 * A {@link Gui} that displays content in lines that can be scrolled through.
 *
 * @param <C> The content type.
 */
public sealed interface ScrollGui<C> extends Gui permits AbstractScrollGui {
    
    /**
     * Creates a new {@link Builder Gui Builder} for a {@link ScrollGui} that uses {@link Item Items} as content.
     *
     * @return The new {@link Builder Gui Builder}.
     */
    static Builder<Item> itemsBuilder() {
        return new ScrollItemsGuiImpl.Builder<>();
    }
    
    /**
     * Creates a new {@link ScrollGui}.
     *
     * @param width            The width of the {@link ScrollGui}.
     * @param height           The height of the {@link ScrollGui}.
     * @param items            The {@link Item Items} to use.
     * @param contentListSlots The slots where content should be displayed.
     * @param direction        The direction in which the {@link ScrollGui} will scroll.
     * @return The created {@link ScrollGui}.
     */
    static ScrollGui<Item> ofItems(int width, int height, List<? extends Item> items, SequencedSet<? extends Slot> contentListSlots, ScrollDirection direction) {
        return new ScrollItemsGuiImpl<>(width, height, items, contentListSlots, direction == ScrollDirection.VERTICAL);
    }
    
    /**
     * Creates a new {@link ScrollGui}.
     *
     * @param structure The {@link Structure} to use.
     * @param items     The {@link Item Items} to use.
     * @return The created {@link ScrollGui}.
     */
    static ScrollGui<Item> ofItems(Structure structure, List<? extends Item> items) {
        return new ScrollItemsGuiImpl<>(structure, MutableProperty.of(0), Property.of(items));
    }
    
    /**
     * Creates a new {@link Builder Gui Builder} for a {@link ScrollGui} that uses {@link Gui Guis} as content.
     *
     * @return The new {@link Builder Gui Builder}.
     */
    static Builder<Gui> guisBuilder() {
        return new ScrollNestedGuiImpl.Builder<>();
    }
    
    /**
     * Creates a new {@link ScrollGui}.
     *
     * @param width            The width of the {@link ScrollGui}.
     * @param height           The height of the {@link ScrollGui}.
     * @param guis             The {@link Gui Guis} to use.
     * @param contentListSlots The slots where content should be displayed.
     * @param direction        The direction in which the {@link ScrollGui} will scroll.
     * @return The created {@link ScrollGui}.
     */
    static ScrollGui<Gui> ofGuis(int width, int height, List<? extends Gui> guis, SequencedSet<? extends Slot> contentListSlots, ScrollDirection direction) {
        return new ScrollNestedGuiImpl<>(width, height, guis, contentListSlots, direction == ScrollDirection.VERTICAL);
    }
    
    /**
     * Creates a new {@link ScrollGui}.
     *
     * @param structure The {@link Structure} to use.
     * @param guis      The {@link Gui Guis} to use.
     * @return The created {@link ScrollGui}.
     */
    static ScrollGui<Gui> ofGuis(Structure structure, List<? extends Gui> guis) {
        return new ScrollNestedGuiImpl<>(structure, MutableProperty.of(0), Property.of(guis));
    }
    
    /**
     * Creates a new {@link Builder Gui Builder} for a {@link ScrollGui} that uses {@link Inventory VirtualInventories} as content.
     *
     * @return The new {@link Builder Gui Builder}.
     */
    static Builder<Inventory> inventoriesBuilder() {
        return new ScrollInventoryGuiImpl.Builder<>();
    }
    
    /**
     * Creates a new {@link ScrollGui}.
     *
     * @param width            The width of the {@link ScrollGui}.
     * @param height           The height of the {@link ScrollGui}.
     * @param inventories      The {@link Inventory VirtualInventories} to use.
     * @param contentListSlots The slots where content should be displayed.
     * @param direction        The direction in which the {@link ScrollGui} will scroll.
     * @return The created {@link ScrollGui}.
     */
    static ScrollGui<Inventory> ofInventories(int width, int height, List<? extends Inventory> inventories, SequencedSet<? extends Slot> contentListSlots, ScrollDirection direction) {
        return new ScrollInventoryGuiImpl<>(width, height, inventories, contentListSlots, direction == ScrollDirection.VERTICAL);
    }
    
    /**
     * Creates a new {@link ScrollGui}.
     *
     * @param structure   The {@link Structure} to use.
     * @param inventories The {@link Inventory VirtualInventories} to use.
     * @return The created {@link ScrollGui}.
     */
    static ScrollGui<Inventory> ofInventories(Structure structure, List<? extends Inventory> inventories) {
        return new ScrollInventoryGuiImpl<>(structure, MutableProperty.of(0), Property.of(inventories));
    }
    
    /**
     * Sets the slots at which scroll content should be displayed, in order of appearance,
     * assuming a horizontal line orientation.
     *
     * @param slots The slots to set.
     * @throws IllegalArgumentException If there are differing line lengths
     */
    void setContentListSlotsHorizontal(SequencedSet<? extends Slot> slots);
    
    /**
     * Sets the slots at which scroll content should be displayed, in order of appearance,
     * assuming a vertical line orientation.
     *
     * @param slots The slots to set
     * @throws IllegalArgumentException If there are differing line lengths
     */
    void setContentListSlotsVertical(SequencedSet<? extends Slot> slots);
    
    /**
     * Gets the slots that are used to display the content.
     *
     * @return The slots that are used to display the content.
     */
    @Unmodifiable
    SequencedSet<Slot> getContentListSlots();
    
    /**
     * Gets the current line, which is the index of the first line to be displayed.
     *
     * @return The current line.
     */
    int getLine();
    
    /**
     * Sets the current line, which is the index of the first line to be displayed.
     *
     * @param line The line to set.
     */
    void setLine(int line);
    
    /**
     * Gets the amount of lines.
     *
     * @return The amount of lines.
     */
    int getLineCount();
    
    /**
     * Gets the maximum selectable line index.
     * This index is chosen such that the last line fills the last row / column.
     *
     * @return The maximum selectable line index.
     */
    int getMaxLine();
    
    /**
     * Sets the content for all lines.
     *
     * @param content The content to set.
     */
    void setContent(List<? extends C> content);
    
    /**
     * Gets the scrollable content.
     *
     * @return The scrollable content.
     */
    @UnmodifiableView
    List<C> getContent();
    
    /**
     * Bakes the elements of this {@link PagedGui} based on the current content.
     * <p>
     * This method does not need to be called when using {@link #setContent(List)},
     * but is required when the size of the content itself changes.
     */
    void bake();
    
    /**
     * Gets the scroll handlers of this {@link ScrollGui}.
     *
     * @return The scroll handlers of this {@link ScrollGui}.
     */
    @UnmodifiableView
    List<BiConsumer<Integer, Integer>> getScrollHandlers();
    
    /**
     * Replaces the currently registered scroll handlers with the specified ones.
     *
     * @param handlers The new scroll handlers.
     */
    void setScrollHandlers(List<? extends BiConsumer<Integer, Integer>> handlers);
    
    /**
     * Adds a scroll handler to this {@link ScrollGui}.
     *
     * @param handler The scroll handler to add.
     */
    void addScrollHandler(BiConsumer<? super Integer, ? super Integer> handler);
    
    /**
     * Removes the specified scroll handler from this {@link ScrollGui}.
     *
     * @param handler The scroll handler to remove.
     */
    void removeScrollHandler(BiConsumer<? super Integer, ? super Integer> handler);
    
    /**
     * Gets the line count change handlers of this {@link ScrollGui}.
     *
     * @return The line count change handlers of this {@link ScrollGui}.
     */
    @UnmodifiableView
    List<BiConsumer<Integer, Integer>> getLineCountChangeHandlers();
    
    /**
     * Replaces the currently registered line count change handlers with the specified ones.
     *
     * @param handlers The new line count change handlers.
     */
    void setLineCountChangeHandlers(List<? extends BiConsumer<Integer, Integer>> handlers);
    
    /**
     * Adds a line count change handlers to this {@link ScrollGui}.
     *
     * @param handler The line count change handlers to add.
     */
    void addLineCountChangeHandler(BiConsumer<? super Integer, ? super Integer> handler);
    
    /**
     * Removes the specified line count change handlers from this {@link ScrollGui}.
     *
     * @param handler The line count change handlers to remove.
     */
    void removeLineCountChangeHandler(BiConsumer<? super Integer, ? super Integer> handler);
    
    /**
     * A {@link ScrollGui} builder.
     *
     * @param <C> The content type.
     */
    sealed interface Builder<C> extends Gui.Builder<ScrollGui<C>, Builder<C>> permits AbstractScrollGui.AbstractBuilder {
        
        /**
         * Sets the content of the {@link ScrollGui} for all lines.
         *
         * @param content The content to set.
         * @return This {@link Builder Gui Builder}.
         */
        default Builder<C> setContent(List<? extends C> content) {
            return setContent(Property.of(content));
        }
        
        /**
         * Sets the property that contains the content of the {@link ScrollGui}.
         *
         * @param content The content property to set.
         * @return This {@link Builder Gui Builder}.
         */
        Builder<C> setContent(Property<? extends List<? extends C>> content);
        
        /**
         * Sets the property that contains the page of the {@link PagedGui}.
         *
         * @param line The line property to set.
         * @return This {@link Builder Gui Builder}.
         */
        Builder<C> setLine(MutableProperty<Integer> line);
        
        /**
         * Sets the scroll handlers of the {@link ScrollGui}.
         *
         * @param handlers The scroll handlers to set.
         * @return This {@link Builder Gui Builder}.
         */
        Builder<C> setScrollHandlers(List<? extends BiConsumer<? super Integer, ? super Integer>> handlers);
        
        /**
         * Adds a scroll handler to the {@link ScrollGui}.
         *
         * @param handler The scroll handler to add.
         * @return This {@link Builder Gui Builder}.
         */
        Builder<C> addScrollHandler(BiConsumer<? super Integer, ? super Integer> handler);
        
        /**
         * Sets the line count change handlers of the {@link ScrollGui}.
         *
         * @param handlers The line count change handlers to set.
         * @return This {@link Builder Gui Builder}.
         */
        Builder<C> setLineCountChangeHandlers(List<? extends BiConsumer<? super Integer, ? super Integer>> handlers);
        
        /**
         * Adds a line count change handler to the {@link ScrollGui}.
         *
         * @param handler The scroll handler to add.
         * @return This {@link Builder Gui Builder}.
         */
        Builder<C> addLineCountChangeHandler(BiConsumer<? super Integer, ? super Integer> handler);
        
    }
    
    /**
     * The direction in which a {@link ScrollGui} scrolls.
     */
    enum ScrollDirection {
        /**
         * The {@link ScrollGui} scrolls vertically, i.e. uses horizontal lines.
         */
        VERTICAL,
        
        /**
         * The {@link ScrollGui} scrolls horizontally, i.e. uses vertical lines.
         */
        HORIZONTAL
    }
    
}
