package xyz.xenondevs.invui.gui;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.gui.structure.Structure;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface TabGui extends Gui {
    
    /**
     * Creates a new {@link Builder Gui Builder} for a {@link TabGui}.
     *
     * @return The new {@link Builder Gui Builder}.
     */
    static @NotNull Builder normal() {
        return new TabGuiImpl.BuilderImpl();
    }
    
    /**
     * Creates a new {@link TabGui} after configuring a {@link Builder Gui Builder} using the given {@link Consumer}.
     *
     * @param consumer The {@link Consumer} to configure the {@link Builder Gui Builder}.
     * @return The created {@link TabGui}.
     */
    static @NotNull TabGui normal(@NotNull Consumer<@NotNull Builder> consumer) {
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
    static @NotNull TabGui of(int width, int height, @NotNull List<@Nullable Gui> tabs, int... contentListSlots) {
        return new TabGuiImpl(width, height, tabs, contentListSlots);
    }
    
    /**
     * Creates a new {@link TabGui}.
     *
     * @param structure The {@link Structure} to use.
     * @param tabs      The {@link Gui Guis} to use as tabs.
     * @return The created {@link TabGui}.
     */
    static @NotNull TabGui of(Structure structure, @NotNull List<@Nullable Gui> tabs) {
        return new TabGuiImpl(tabs, structure);
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
    @NotNull List<@Nullable Gui> getTabs();
    
    /**
     * Gets the registered tab change handlers.
     *
     * @return The registered tab change handlers.
     */
    @Nullable List<@NotNull BiConsumer<Integer, Integer>> getTabChangeHandlers();
    
    /**
     * Replaces the currently registered tab change handlers with the given list.
     *
     * @param handlers The new page change handlers.
     */
    void setTabChangeHandlers(@Nullable List<@NotNull BiConsumer<Integer, Integer>> handlers);
    
    /**
     * Registers a page change handler.
     *
     * @param handler The handler to register.
     */
    void addTabChangeHandler(@NotNull BiConsumer<Integer, Integer> handler);
    
    /**
     * Unregisters a page change handler.
     *
     * @param handler The handler to unregister.
     */
    void removeTabChangeHandler(@NotNull BiConsumer<Integer, Integer> handler);
    
    /**
     * A {@link TabGui} builder.
     */
    interface Builder extends Gui.Builder<TabGui, Builder> {
        
        /**
         * Sets the tabs of the {@link TabGui}.
         * Individual tabs can be null to disable them, but there must at least one tab.
         *
         * @param tabs The tabs of the {@link TabGui}.
         * @return This {@link Builder Gui Builder}.
         */
        @Contract("_ -> this")
        @NotNull Builder setTabs(@NotNull List<@Nullable Gui> tabs);
        
        /**
         * Adds a tab to the {@link TabGui}.
         * Individual tabs can be null to disable them, but there must at least one tab.
         *
         * @param tab The tab to add.
         * @return This {@link Builder Gui Builder}.
         */
        @Contract("_ -> this")
        @NotNull Builder addTab(@Nullable Gui tab);
        
        /**
         * Sets the tab change handlers of the {@link TabGui}.
         *
         * @param handlers The tab change handlers of the {@link TabGui}.
         * @return This {@link Builder Gui Builder}.
         */
        @Contract("_ -> this")
        @NotNull Builder setTabChangeHandlers(@NotNull List<@NotNull BiConsumer<Integer, Integer>> handlers);
        
        /**
         * Adds a tab change handler to the {@link TabGui}.
         *
         * @param handler The tab change handler to add.
         * @return This {@link Builder Gui Builder}.
         */
        @Contract("_ -> this")
        @NotNull Builder addTabChangeHandler(@NotNull BiConsumer<Integer, Integer> handler);
        
    }
    
}
