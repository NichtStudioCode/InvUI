package de.studiocode.invui.gui.impl;

import de.studiocode.invui.gui.AbstractTabGUI;
import de.studiocode.invui.gui.GUI;
import de.studiocode.invui.gui.SlotElement;
import de.studiocode.invui.gui.SlotElement.LinkedSlotElement;
import de.studiocode.invui.gui.builder.GUIBuilder;
import de.studiocode.invui.gui.structure.Structure;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A {@link GUI} that has multiple tabs with which users can switch between {@link GUI}s.
 *
 * @see GUIBuilder
 */
public final class TabGUIImpl extends AbstractTabGUI {
    
    private final List<GUI> tabs;
    private final List<List<SlotElement>> linkingElements;
    
    public TabGUIImpl(int width, int height, @NotNull List<GUI> tabs, int[] listSlots) {
        super(width, height, tabs.size(), listSlots);
        this.linkingElements = tabs.stream().map(this::getLinkingElements).collect(Collectors.toList());
        this.tabs = tabs;
        
        update();
    }
    
    public TabGUIImpl(@NotNull List<GUI> tabs, @NotNull Structure structure) {
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
    
    @Override
    protected List<SlotElement> getSlotElements(int tab) {
        return linkingElements.get(tab);
    }
    
}
