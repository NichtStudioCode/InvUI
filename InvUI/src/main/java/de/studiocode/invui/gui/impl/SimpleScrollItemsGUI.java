package de.studiocode.invui.gui.impl;

import de.studiocode.invui.gui.SlotElement;
import de.studiocode.invui.gui.structure.Structure;
import de.studiocode.invui.item.Item;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SimpleScrollItemsGUI extends ScrollGUI {
    
    private List<Item> items;
    
    public SimpleScrollItemsGUI(int width, int height, @Nullable List<Item> items, int... itemListSlots) {
        super(width, height, false, itemListSlots);
        setItems(items);
    }
    
    public SimpleScrollItemsGUI(int width, int height, @Nullable List<Item> items, @NotNull Structure structure) {
        super(width, height, false, structure);
        setItems(items);
    }
    
    public void setItems(@Nullable List<Item> items) {
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
    protected int getMaxLineIndex() {
        return (int) Math.ceil((double) items.size() / (double) getLineLength()) - 1;
    }
    
}
