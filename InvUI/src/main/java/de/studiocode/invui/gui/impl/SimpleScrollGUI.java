package de.studiocode.invui.gui.impl;

import de.studiocode.invui.gui.SlotElement;
import de.studiocode.invui.gui.structure.Structure;
import de.studiocode.invui.item.Item;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SimpleScrollGUI extends ScrollGUI {
    
    private List<Item> items;
    
    public SimpleScrollGUI(int width, int height, @Nullable List<Item> items, int... itemListSlots) {
        super(width, height, false, itemListSlots);
        this.items = items == null ? new ArrayList<>() : items;
        
        update();
    }
    
    public SimpleScrollGUI(int width, int height, @Nullable List<Item> items, @NotNull Structure structure) {
        super(width, height, false, structure);
        this.items = items == null ? new ArrayList<>() : items;
        
        update();
    }
    
    public void setItems(List<Item> items) {
        this.items = items;
        update();
    }
    
    @Override
    protected int getElementAmount() {
        return items.size();
    }
    
    @Override
    protected List<SlotElement> getElements(int from, int to) {
        return items.subList(from, Math.min(items.size(), to)).stream()
            .map(SlotElement.ItemSlotElement::new)
            .collect(Collectors.toList());
    }
    
}
