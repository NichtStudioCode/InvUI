package de.studiocode.invui.gui.impl;

import de.studiocode.invui.gui.GUI;
import de.studiocode.invui.gui.SlotElement;
import de.studiocode.invui.gui.SlotElement.LinkedSlotElement;
import de.studiocode.invui.gui.builder.GUIBuilder;
import de.studiocode.invui.gui.structure.Structure;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * A {@link GUI} that has multiple tabs with which users can switch between {@link GUI}s.
 *
 * @see GUIBuilder
 */
public class SimpleTabGUI extends TabGUI {
    
    private final List<GUI> tabs;
    private final List<List<SlotElement>> linkingElements;
    private List<BiConsumer<Integer, Integer>> tabChangeHandlers;
    
    public SimpleTabGUI(int width, int height, @NotNull List<GUI> tabs, int[] listSlots) {
        super(width, height, tabs.size(), listSlots);
        this.linkingElements = tabs.stream().map(this::getLinkingElements).collect(Collectors.toList());
        this.tabs = tabs;
        
        update();
    }
    
    public SimpleTabGUI(@NotNull List<GUI> tabs, @NotNull Structure structure) {
        super(structure.getWidth(), structure.getHeight(), tabs.size(), structure);
        this.linkingElements = tabs.stream().map(this::getLinkingElements).collect(Collectors.toList());
        this.tabs = tabs;
        
        update();
    }
    
    private List<SlotElement> getLinkingElements(GUI gui) {
        if (gui == null) return null;
        
        List<SlotElement> elements = new ArrayList<>();
        for (int slot = 0; slot < gui.getSize(); slot++) {
            SlotElement link = new LinkedSlotElement(gui, slot);
            elements.add(link);
        }
        
        return elements;
    }
    
    public List<GUI> getTabs() {
        return Collections.unmodifiableList(tabs);
    }
    
    @Override
    public boolean isTabAvailable(int tab) {
        return tabs.get(tab) != null;
    }
    
    public void addTabChangeHandler(@NotNull BiConsumer<Integer, Integer> tabChangeHandler) {
        if (tabChangeHandlers == null) tabChangeHandlers = new ArrayList<>();
        tabChangeHandlers.add(tabChangeHandler);
    }
    
    public void removeTabChangeHandler(@NotNull BiConsumer<Integer, Integer> tabChangeHandler) {
        if (tabChangeHandlers != null) tabChangeHandlers.remove(tabChangeHandler);
    }
    
    @Nullable
    public List<BiConsumer<Integer, Integer>> getTabChangeHandlers() {
        return tabChangeHandlers;
    }
    
    public void setTabChangeHandlers(@Nullable List<BiConsumer<Integer, Integer>> tabChangeHandlers) {
        this.tabChangeHandlers = tabChangeHandlers;
    }
    
    @Override
    protected void handleTabChange(int previous, int now) {
        if (tabChangeHandlers != null) tabChangeHandlers.forEach(handler -> handler.accept(previous, now));
    }
    
    @Override
    protected List<SlotElement> getSlotElements(int tab) {
        return linkingElements.get(tab);
    }
    
}
