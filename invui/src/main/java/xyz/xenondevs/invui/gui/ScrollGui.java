package xyz.xenondevs.invui.gui;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.gui.structure.Structure;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.virtualinventory.VirtualInventory;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface ScrollGui<C> extends Gui {
    
    /**
     * Creates a new {@link Builder Gui Builder} for a {@link ScrollGui} that uses {@link Item Items} as content.
     *
     * @return The new {@link Builder Gui Builder}.
     */
    static @NotNull Builder<@NotNull Item> items() {
        return new ScrollItemsGuiImpl.Builder();
    }
    
    /**
     * Creates a new {@link ScrollGui} that uses {@link Item Items} as content after configuring a
     * {@link Builder Gui Builder} using the given {@link Consumer}.
     *
     * @param consumer The {@link Consumer} to configure the {@link Builder Gui Builder}.
     * @return The created {@link ScrollGui}.
     */
    static @NotNull ScrollGui<@NotNull Item> items(@NotNull Consumer<@NotNull Builder<@NotNull Item>> consumer) {
        Builder<@NotNull Item> builder = items();
        consumer.accept(builder);
        return builder.build();
    }
    
    /**
     * Creates a new {@link ScrollGui}.
     *
     * @param width            The width of the {@link ScrollGui}.
     * @param height           The height of the {@link ScrollGui}.
     * @param items            The {@link Item Items} to use.
     * @param contentListSlots The slots where content should be displayed.
     * @return The created {@link ScrollGui}.
     */
    static @NotNull ScrollGui<@NotNull Item> ofItems(int width, int height, @NotNull List<@NotNull Item> items, int... contentListSlots) {
        return new ScrollItemsGuiImpl(width, height, items, contentListSlots);
    }
    
    /**
     * Creates a new {@link ScrollGui}.
     *
     * @param structure The {@link Structure} to use.
     * @param items     The {@link Item Items} to use.
     * @return The created {@link ScrollGui}.
     */
    static @NotNull ScrollGui<@NotNull Item> ofItems(@NotNull Structure structure, @NotNull List<@NotNull Item> items) {
        return new ScrollItemsGuiImpl(items, structure);
    }
    
    /**
     * Creates a new {@link Builder Gui Builder} for a {@link ScrollGui} that uses {@link Gui Guis} as content.
     *
     * @return The new {@link Builder Gui Builder}.
     */
    static @NotNull Builder<@NotNull Gui> guis() {
        return new ScrollNestedGuiImpl.Builder();
    }
    
    /**
     * Creates a new {@link ScrollGui} that uses {@link Gui Guis} as content after configuring a
     * {@link Builder Gui Builder} using the given {@link Consumer}.
     *
     * @param consumer The {@link Consumer} to configure the {@link Builder Gui Builder}.
     * @return The created {@link ScrollGui}.
     */
    static @NotNull ScrollGui<@NotNull Gui> guis(@NotNull Consumer<@NotNull Builder<@NotNull Gui>> consumer) {
        Builder<@NotNull Gui> builder = guis();
        consumer.accept(builder);
        return builder.build();
    }
    
    /**
     * Creates a new {@link ScrollGui}.
     *
     * @param width            The width of the {@link ScrollGui}.
     * @param height           The height of the {@link ScrollGui}.
     * @param guis             The {@link Gui Guis} to use.
     * @param contentListSlots The slots where content should be displayed.
     * @return The created {@link ScrollGui}.
     */
    static @NotNull ScrollGui<@NotNull Gui> ofGuis(int width, int height, @NotNull List<@NotNull Gui> guis, int... contentListSlots) {
        return new ScrollNestedGuiImpl(width, height, guis, contentListSlots);
    }
    
    /**
     * Creates a new {@link ScrollGui}.
     *
     * @param structure The {@link Structure} to use.
     * @param guis      The {@link Gui Guis} to use.
     * @return The created {@link ScrollGui}.
     */
    static @NotNull ScrollGui<@NotNull Gui> ofGuis(Structure structure, @NotNull List<@NotNull Gui> guis) {
        return new ScrollNestedGuiImpl(guis, structure);
    }
    
    /**
     * Creates a new {@link Builder Gui Builder} for a {@link ScrollGui} that uses {@link VirtualInventory VirtualInventories} as content.
     *
     * @return The new {@link Builder Gui Builder}.
     */
    static @NotNull Builder<@NotNull VirtualInventory> inventories() {
        return new ScrollInventoryGuiImpl.Builder();
    }
    
    /**
     * Creates a new {@link ScrollGui} that uses {@link VirtualInventory VirtualInventories} as content after configuring a
     * {@link Builder Gui Builder} using the given {@link Consumer}.
     *
     * @param consumer The {@link Consumer} to configure the {@link Builder Gui Builder}.
     * @return The created {@link ScrollGui}.
     */
    static @NotNull ScrollGui<@NotNull VirtualInventory> inventories(@NotNull Consumer<@NotNull Builder<@NotNull VirtualInventory>> consumer) {
        Builder<@NotNull VirtualInventory> builder = inventories();
        consumer.accept(builder);
        return builder.build();
    }
    
    /**
     * Creates a new {@link ScrollGui}.
     *
     * @param width            The width of the {@link ScrollGui}.
     * @param height           The height of the {@link ScrollGui}.
     * @param inventories      The {@link VirtualInventory VirtualInventories} to use.
     * @param contentListSlots The slots where content should be displayed.
     * @return The created {@link ScrollGui}.
     */
    static @NotNull ScrollGui<@NotNull VirtualInventory> ofInventories(int width, int height, @NotNull List<@NotNull VirtualInventory> inventories, int... contentListSlots) {
        return new ScrollInventoryGuiImpl(width, height, inventories, contentListSlots);
    }
    
    /**
     * Creates a new {@link ScrollGui}.
     *
     * @param structure   The {@link Structure} to use.
     * @param inventories The {@link VirtualInventory VirtualInventories} to use.
     * @return The created {@link ScrollGui}.
     */
    static @NotNull ScrollGui<@NotNull VirtualInventory> ofInventories(@NotNull Structure structure, @NotNull List<@NotNull VirtualInventory> inventories) {
        return new ScrollInventoryGuiImpl(inventories, structure);
    }
    
    /**
     * Gets the current line of this {@link ScrollGui}.
     *
     * @return The current line of this {@link ScrollGui}.
     */
    int getCurrentLine();
    
    /**
     * Gets the max line index of this {@link ScrollGui}.
     *
     * @return The max line index of this {@link ScrollGui}.
     */
    int getMaxLine();
    
    /**
     * Sets the current line of this {@link ScrollGui}.
     *
     * @param line The line to set.
     */
    void setCurrentLine(int line);
    
    /**
     * Checks if it is possible to scroll the specified amount of lines.
     *
     * @return Whether it is possible to scroll the specified amount of lines.
     */
    boolean canScroll(int lines);
    
    /**
     * Scrolls the specified amount of lines.
     *
     * @param lines The amount of lines to scroll.
     */
    void scroll(int lines);
    
    /**
     * Sets the content of this {@link ScrollGui} for all lines.
     *
     * @param content The content to set.
     */
    void setContent(@Nullable List<@NotNull C> content);
    
    /**
     * Replaces the currently registered scroll handlers with the specified ones.
     *
     * @param scrollHandlers The new scroll handlers.
     */
    void setScrollHandlers(@NotNull List<@NotNull BiConsumer<Integer, Integer>> scrollHandlers);
    
    /**
     * Adds a scroll handler to this {@link ScrollGui}.
     *
     * @param scrollHandler The scroll handler to add.
     */
    void addScrollHandler(@NotNull BiConsumer<Integer, Integer> scrollHandler);
    
    /**
     * Removes the specified scroll handler from this {@link ScrollGui}.
     *
     * @param scrollHandler The scroll handler to remove.
     */
    void removeScrollHandler(@NotNull BiConsumer<Integer, Integer> scrollHandler);
    
    /**
     * A {@link ScrollGui} builder.
     *
     * @param <C> The content type.
     */
    interface Builder<C> extends Gui.Builder<ScrollGui<C>, Builder<C>> {
        
        /**
         * Sets the content of the {@link ScrollGui} for all lines.
         *
         * @param content The content to set.
         * @return This {@link Builder Gui Builder}.
         */
        @Contract("_ -> this")
        Builder<C> setContent(@NotNull List<@NotNull C> content);
        
        /**
         * Adds content to the {@link ScrollGui}.
         *
         * @param content The content to add.
         * @return This {@link Builder Gui Builder}.
         */
        @Contract("_ -> this")
        Builder<C> addContent(@NotNull C content);
        
        /**
         * Adds content to the {@link ScrollGui}.
         *
         * @param handlers The content to add.
         * @return This {@link Builder Gui Builder}.
         */
        @Contract("_ -> this")
        Builder<C> setScrollHandlers(@NotNull List<@NotNull BiConsumer<Integer, Integer>> handlers);
        
        /**
         * Adds a scroll handler to the {@link ScrollGui}.
         *
         * @param handler The scroll handler to add.
         * @return This {@link Builder Gui Builder}.
         */
        @Contract("_ -> this")
        Builder<C> addScrollHandler(@NotNull BiConsumer<Integer, Integer> handler);
        
    }
    
}
