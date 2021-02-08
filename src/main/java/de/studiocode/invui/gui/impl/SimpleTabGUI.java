package de.studiocode.invui.gui.impl;

import de.studiocode.invui.gui.GUI;
import de.studiocode.invui.gui.SlotElement;
import de.studiocode.invui.gui.builder.GUIBuilder;

import java.util.Arrays;
import java.util.List;

/**
 * A {@link GUI} that has multiple tabs with which users can switch between {@link GUI}s.
 *
 * @see GUIBuilder
 */
public class SimpleTabGUI extends TabGUI {
    
    private final List<GUI> tabs;
    
    public SimpleTabGUI(int width, int height, List<GUI> tabs, int[] listSlots) {
        super(width, height, tabs.size(), listSlots);
        this.tabs = tabs;
        
        update();
    }
    
    @Override
    public List<SlotElement> getSlotElements(int tab) {
        return Arrays.asList(tabs.get(tab).getSlotElements());
    }
    
}
