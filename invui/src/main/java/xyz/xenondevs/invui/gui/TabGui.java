package xyz.xenondevs.invui.gui;

import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public sealed interface TabGui<C extends Gui> extends Gui permits AbstractTabGui {
    
    /**
     * Creates a new {@link Builder Gui Builder} for a {@link TabGui}.
     *
     * @return The new {@link Builder Gui Builder}.
     */
    static <C extends Gui> Builder<C> normal() {
        return new TabGuiImpl.BuilderImpl<>();
    }
    
    /**
     * Creates a new {@link TabGui} after configuring a {@link Builder Gui Builder} using the given {@link Consumer}.
     *
     * @param consumer The {@link Consumer} to configure the {@link Builder Gui Builder}.
     * @return The created {@link TabGui}.
     */
    static <C extends Gui> TabGui<C> normal(Consumer<Builder<C>> consumer) {
        Builder<C> builder = normal();
        consumer.accept(builder);
        return builder.build();
    }
    
    /**
     * Creates a new {@link TabGui}.
     *
     * @param width            The width of the {@link TabGui}.
     * @param height           The height of the {@link TabGui}.
     * @param tabs             The {@link Gui Guis} to use as tabs.
     * @param contentListSlots The slots where content should be displayed.
     * @return The created {@link TabGui}.
     */
    static <C extends Gui> TabGui<C> of(int width, int height, List<@Nullable C> tabs, int... contentListSlots) {
        return new TabGuiImpl<>(width, height, tabs, contentListSlots);
    }
    
    /**
     * Creates a new {@link TabGui}.
     *
     * @param structure The {@link Structure} to use.
     * @param tabs      The {@link Gui Guis} to use as tabs.
     * @return The created {@link TabGui}.
     */
    static <C extends Gui> TabGui<C> of(Structure structure, List<@Nullable C> tabs) {
        return new TabGuiImpl<>(tabs, structure);
    }
    
    /**
     * Gets the current tab index.
     *
     * @return The current tab index.
     */
    int getCurrentTab();
    
    /**
     * Sets the current tab.
     *
     * @param tab The index of the tab to show.
     */
    void setTab(int tab);
    
    /**
     * Checks if the given tab is available.
     *
     * @param tab The index of the tab to check.
     * @return Whether the given tab is available.
     */
    boolean isTabAvailable(int tab);
    
    /**
     * Gets the configured tabs.
     *
     * @return The configured tabs.
     */
    List<@Nullable C> getTabs();
    
    /**
     * Gets the registered tab change handlers.
     *
     * @return The registered tab change handlers.
     */
    @Nullable
    List<BiConsumer<Integer, Integer>> getTabChangeHandlers();
    
    /**
     * Replaces the currently registered tab change handlers with the given list.
     *
     * @param handlers The new page change handlers.
     */
    void setTabChangeHandlers(@Nullable List<BiConsumer<Integer, Integer>> handlers);
    
    /**
     * Registers a page change handler.
     *
     * @param handler The handler to register.
     */
    void addTabChangeHandler(BiConsumer<Integer, Integer> handler);
    
    /**
     * Unregisters a page change handler.
     *
     * @param handler The handler to unregister.
     */
    void removeTabChangeHandler(BiConsumer<Integer, Integer> handler);
    
    /**
     * A {@link TabGui} builder.
     */
    sealed interface Builder<C extends Gui> extends Gui.Builder<TabGui<C>, Builder<C>> permits AbstractTabGui.AbstractBuilder {
        
        /**
         * Sets the tabs of the {@link TabGui}.
         * Individual tabs can be null to disable them, but there must at least one tab.
         *
         * @param tabs The tabs of the {@link TabGui}.
         * @return This {@link Builder Gui Builder}.
         */
        Builder<C> setTabs(List<@Nullable C> tabs);
        
        /**
         * Adds a tab to the {@link TabGui}.
         * Individual tabs can be null to disable them, but there must at least one tab.
         *
         * @param tab The tab to add.
         * @return This {@link Builder Gui Builder}.
         */
        Builder<C> addTab(@Nullable C tab);
        
        /**
         * Sets the tab change handlers of the {@link TabGui}.
         *
         * @param handlers The tab change handlers of the {@link TabGui}.
         * @return This {@link Builder Gui Builder}.
         */
        Builder<C> setTabChangeHandlers(List<BiConsumer<Integer, Integer>> handlers);
        
        /**
         * Adds a tab change handler to the {@link TabGui}.
         *
         * @param handler The tab change handler to add.
         * @return This {@link Builder Gui Builder}.
         */
        Builder<C> addTabChangeHandler(BiConsumer<Integer, Integer> handler);
        
    }
    
}
