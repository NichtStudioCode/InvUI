package xyz.xenondevs.invui.gui;

import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A {@link Gui} that displays multiple tabs, which themselves are {@link Gui Guis} as well.
 */
public sealed interface TabGui extends Gui permits TabGuiImpl {
    
    /**
     * Creates a new {@link Builder Gui Builder} for a {@link TabGui}.
     *
     * @return The new {@link Builder Gui Builder}.
     */
    static Builder normal() {
        return new TabGuiImpl.Builder();
    }
    
    /**
     * Creates a new {@link TabGui} after configuring a {@link Builder Gui Builder} using the given {@link Consumer}.
     *
     * @param consumer The {@link Consumer} to configure the {@link Builder Gui Builder}.
     * @return The created {@link TabGui}.
     */
    static TabGui normal(Consumer<Builder> consumer) {
        Builder builder = normal();
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
    static TabGui of(int width, int height, List<? extends @Nullable Gui> tabs, int... contentListSlots) {
        return new TabGuiImpl(width, height, tabs, contentListSlots);
    }
    
    /**
     * Creates a new {@link TabGui}.
     *
     * @param structure The {@link Structure} to use.
     * @param tabs      The {@link Gui Guis} to use as tabs.
     * @return The created {@link TabGui}.
     */
    static TabGui of(Structure structure, List<? extends @Nullable Gui> tabs) {
        return new TabGuiImpl(() -> tabs, structure);
    }
    
    /**
     * Sets the slots at which tab content should be displayed, in order of appearance.
     *
     * @param slots The slots to set.
     */
    void setContentListSlots(Slot[] slots);
    
    /**
     * Sets the slot indices at which tab content should be displayed, in order of appearance.
     *
     * @param slotIndices The slot indices to set.
     */
    void setContentListSlots(int[] slotIndices);
    
    /**
     * Gets the current tab index.
     *
     * @return The current tab index.
     */
    int getTab();
    
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
     * Sets the supplier used to retrieve the tabs of this {@link TabGui}.
     * Refreshes can be triggered via {@link TabGui#bake}.
     *
     * @param tabsSupplier The content supplier to set.
     */
    void setTabsSupplier(Supplier<? extends List<? extends @Nullable Gui>> tabsSupplier);
    
    /**
     * Sets the tabs of this {@link TabGui}.
     *
     * @param tabs The tabs to set.
     */
    void setTabs(List<? extends @Nullable Gui> tabs);
    
    /**
     * Gets the configured tabs.
     *
     * @return The configured tabs.
     */
    List<? extends @Nullable Gui> getTabs();
    
    /**
     * Bakes and updates the tabs of this {@link TabGui} based on the current tabs.
     * <p>
     * This method does not need to be called when using {@link #setTabs(List)},
     * but is required when the tabs list itself changes.
     */
    void bake();
    
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
    sealed interface Builder extends Gui.Builder<TabGui, Builder> permits TabGuiImpl.Builder {
        
        /**
         * Sets the supplier used to retrieve the tabs of this {@link TabGui}.
         * Refreshes can be triggered via {@link TabGui#bake}.
         *
         * @param tabsSupplier The content supplier to set.
         */
        Builder setTabsSupplier(Supplier<? extends List<@Nullable Gui>> tabsSupplier);
        
        /**
         * Sets the tabs of the {@link TabGui}.
         * Individual tabs can be null to disable them, but there must at least one tab.
         *
         * @param tabs The tabs of the {@link TabGui}.
         * @return This {@link Builder Gui Builder}.
         */
        Builder setTabs(List<@Nullable Gui> tabs);
        
        /**
         * Adds a tab to the {@link TabGui}.
         * Individual tabs can be null to disable them, but there must at least one tab.
         *
         * @param tab The tab to add.
         * @return This {@link Builder Gui Builder}.
         */
        Builder addTab(@Nullable Gui tab);
        
        /**
         * Sets the tab change handlers of the {@link TabGui}.
         *
         * @param handlers The tab change handlers of the {@link TabGui}.
         * @return This {@link Builder Gui Builder}.
         */
        Builder setTabChangeHandlers(List<BiConsumer<Integer, Integer>> handlers);
        
        /**
         * Adds a tab change handler to the {@link TabGui}.
         *
         * @param handler The tab change handler to add.
         * @return This {@link Builder Gui Builder}.
         */
        Builder addTabChangeHandler(BiConsumer<Integer, Integer> handler);
        
    }
    
}
