package xyz.xenondevs.invui.gui;

import org.jetbrains.annotations.Unmodifiable;
import org.jetbrains.annotations.UnmodifiableView;
import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.state.MutableProperty;
import xyz.xenondevs.invui.state.Property;

import java.util.List;
import java.util.SequencedSet;
import java.util.function.BiConsumer;

/**
 * A {@link Gui} that displays multiple tabs, which themselves are {@link Gui Guis} as well.
 */
public sealed interface TabGui extends Gui permits TabGuiImpl {
    
    /**
     * Creates a new {@link Builder Gui Builder} for a {@link TabGui}.
     *
     * @return The new {@link Builder Gui Builder}.
     */
    static Builder builder() {
        return new TabGuiImpl.Builder();
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
    static TabGui of(int width, int height, List<? extends @Nullable Gui> tabs, SequencedSet<? extends Slot> contentListSlots) {
        return new TabGuiImpl(width, height, contentListSlots, Property.of(tabs));
    }
    
    /**
     * Creates a new {@link TabGui}.
     *
     * @param structure The {@link Structure} to use.
     * @param tabs      The {@link Gui Guis} to use as tabs.
     * @return The created {@link TabGui}.
     */
    static TabGui of(Structure structure, List<? extends @Nullable Gui> tabs) {
        return new TabGuiImpl(structure, MutableProperty.of(0), Property.of(tabs));
    }
    
    /**
     * Sets the slots at which tab content should be displayed, in order of appearance.
     *
     * @param slots The slots to set.
     */
    void setContentListSlots(SequencedSet<Slot> slots);
    
    /**
     * Gets the slots that are used to display the tabs.
     *
     * @return The slots that are used to display the tabs.
     */
    @Unmodifiable
    SequencedSet<Slot> getContentListSlots();
    
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
     * Sets the tabs.
     *
     * @param tabs The tabs to set.
     */
    void setTabs(List<? extends @Nullable Gui> tabs);
    
    /**
     * Gets the configured tabs.
     *
     * @return The configured tabs.
     */
    @UnmodifiableView
    List<@Nullable Gui> getTabs();
    
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
    @UnmodifiableView
    List<BiConsumer<Integer, Integer>> getTabChangeHandlers();
    
    /**
     * Replaces the currently registered tab change handlers with the given list.
     *
     * @param handlers The new page change handlers.
     */
    void setTabChangeHandlers(List<? extends BiConsumer<Integer, Integer>> handlers);
    
    /**
     * Registers a page change handler.
     *
     * @param handler The handler to register.
     */
    void addTabChangeHandler(BiConsumer<? super Integer, ? super Integer> handler);
    
    /**
     * Unregisters a page change handler.
     *
     * @param handler The handler to unregister.
     */
    void removeTabChangeHandler(BiConsumer<? super Integer, ? super Integer> handler);
    
    /**
     * A {@link TabGui} builder.
     */
    sealed interface Builder extends Gui.Builder<TabGui, Builder> permits TabGuiImpl.Builder {
        
        /**
         * Sets the property that contains the tabs.
         *
         * @param tabs The tabs property to set.
         * @return This {@link Builder Gui Builder}.
         */
        Builder setTabs(Property<? extends List<? extends @Nullable Gui>> tabs);
        
        /**
         * Sets the tabs of the {@link TabGui}.
         * Individual tabs can be null to disable them, but there must at least one tab.
         *
         * @param tabs The tabs of the {@link TabGui}.
         * @return This {@link Builder Gui Builder}.
         */
        default Builder setTabs(List<? extends @Nullable Gui> tabs) {
            return setTabs(Property.of(tabs));
        }
        
        /**
         * Sets the property that contains the current tab.
         *
         * @param tab The tab property to set.
         * @return This {@link Builder Gui Builder}.
         */
        Builder setTab(MutableProperty<Integer> tab);
        
        /**
         * Sets the tab change handlers of the {@link TabGui}.
         *
         * @param handlers The tab change handlers of the {@link TabGui}.
         * @return This {@link Builder Gui Builder}.
         */
        Builder setTabChangeHandlers(List<? extends BiConsumer<? super Integer, ? super Integer>> handlers);
        
        /**
         * Adds a tab change handler to the {@link TabGui}.
         *
         * @param handler The tab change handler to add.
         * @return This {@link Builder Gui Builder}.
         */
        Builder addTabChangeHandler(BiConsumer<? super Integer, ? super Integer> handler);
        
    }
    
}
