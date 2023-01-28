package de.studiocode.invui.gui.impl;

import de.studiocode.invui.gui.AbstractScrollGUI;
import de.studiocode.invui.gui.GUI;
import de.studiocode.invui.gui.SlotElement;
import de.studiocode.invui.gui.structure.Structure;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public final class ScrollNestedGUIImpl extends AbstractScrollGUI<GUI> {
    
    private List<GUI> guis;
    private List<SlotElement.LinkedSlotElement> elements;
    
    public ScrollNestedGUIImpl(int width, int height, @Nullable List<GUI> guis, int... contentListSlots) {
        super(width, height, false, contentListSlots);
        setContent(guis);
    }
    
    public ScrollNestedGUIImpl(@Nullable List<GUI> guis, @NotNull Structure structure) {
        super(structure.getWidth(), structure.getHeight(), false, structure);
        setContent(guis);
    }
    
    @Override
    public void setContent(@Nullable List<GUI> guis) {
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
    public int getMaxLine() {
        if (elements == null) return 0;
        return (int) Math.ceil((double) elements.size() / (double) getLineLength()) - 1;
    }
    
}
