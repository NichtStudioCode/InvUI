package de.studiocode.invui.gui;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BiConsumer;

public interface TabGUI extends GUI {
    
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
    void showTab(int tab);
    
    /**
     * Checks if the given tab is available.
     *
     * @param tab The index of the tab to check.
     * @return Whether the given tab is available.
     */
    boolean isTabAvailable(int tab);
    
    /**
     * Gets the configured tabs.
     * @return The configured tabs.
     */
    List<GUI> getTabs();
    
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
    void registerTabChangeHandler(@NotNull BiConsumer<Integer, Integer> handler);
    
    /**
     * Unregisters a page change handler.
     *
     * @param handler The handler to unregister.
     */
    void unregisterTabChangeHandler(@NotNull BiConsumer<Integer, Integer> handler);
    
}
