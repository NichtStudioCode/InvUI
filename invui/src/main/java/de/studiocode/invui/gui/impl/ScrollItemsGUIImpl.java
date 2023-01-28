package de.studiocode.invui.gui.impl;

import de.studiocode.invui.gui.AbstractScrollGUI;
import de.studiocode.invui.gui.SlotElement;
import de.studiocode.invui.gui.structure.Structure;
import de.studiocode.invui.item.Item;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class ScrollItemsGUIImpl extends AbstractScrollGUI<Item> {
    
    private List<Item> items;
    
    public ScrollItemsGUIImpl(int width, int height, @Nullable List<Item> items, int... contentListSlots) {
        super(width, height, false, contentListSlots);
        setContent(items);
    }
    
    public ScrollItemsGUIImpl(@Nullable List<Item> items, @NotNull Structure structure) {
        super(structure.getWidth(), structure.getHeight(), false, structure);
        setContent(items);
    }
    
    @Override
    public void setContent(@Nullable List<Item> items) {
        this.items = items != null ? items : new ArrayList<>();
        update();
    }
    
    @Override
    protected List<SlotElement> getElements(int from, int to) {
        return items.subList(from, Math.min(items.size(), to)).stream()
            .map(SlotElement.ItemSlotElement::new)
            .collect(Collectors.toList());
    }
    
    @Override
    public int getMaxLine() {
        return (int) Math.ceil((double) items.size() / (double) getLineLength()) - 1;
    }
    
}
