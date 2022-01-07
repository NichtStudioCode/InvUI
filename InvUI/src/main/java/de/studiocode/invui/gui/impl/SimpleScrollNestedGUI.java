package de.studiocode.invui.gui.impl;

import de.studiocode.invui.gui.GUI;
import de.studiocode.invui.gui.SlotElement;
import de.studiocode.invui.gui.structure.Structure;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SimpleScrollNestedGUI extends ScrollGUI {
    
    private List<GUI> guis;
    private List<SlotElement.LinkedSlotElement> elements;
    
    public SimpleScrollNestedGUI(int width, int height, @Nullable List<GUI> guis, int... itemListSlots) {
        super(width, height, false, itemListSlots);
        setGuis(guis);
    }
    
    public SimpleScrollNestedGUI(int width, int height, @Nullable List<GUI> guis, @NotNull Structure structure) {
        super(width, height, false, structure);
        setGuis(guis);
    }
    
    public void setGuis(@Nullable List<GUI> guis) {
        this.guis = guis != null ? guis : new ArrayList<>();
        updateElements();
        update();
    }
    
    private void updateElements() {
        elements = new ArrayList<>();
        for (GUI gui : guis) {
            for (int i = 0; i < gui.getSize(); i++) {
                elements.add(new SlotElement.LinkedSlotElement(gui, i));
            }
        }
    }
    
    @Override
    protected List<SlotElement.LinkedSlotElement> getElements(int from, int to) {
        return elements.subList(from, Math.min(elements.size(), to));
    }
    
    @Override
    protected int getMaxLineIndex() {
        return guis.size() - 1;
    }
    
}
