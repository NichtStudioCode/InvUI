package de.studiocode.invui.gui.impl;

import de.studiocode.invui.gui.SlotElement;
import de.studiocode.invui.gui.SlotElement.ItemSlotElement;
import de.studiocode.invui.gui.builder.GUIBuilder;
import de.studiocode.invui.gui.structure.Structure;
import de.studiocode.invui.item.Item;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A {@link PagedGUI} that is filled with {@link Item}s.
 *
 * @see GUIBuilder
 * @see SimplePagedNestedGUI
 */
public class SimplePagedItemsGUI extends PagedGUI {
    
    private List<Item> items;
    
    public SimplePagedItemsGUI(int width, int height, @Nullable List<Item> items, int... itemListSlots) {
        super(width, height, false, itemListSlots);
        setItems(items);
    }
    
    public SimplePagedItemsGUI(int width, int height, @Nullable List<Item> items, @NotNull Structure structure) {
        super(width, height, false, structure);
        setItems(items);
    }
    
    @Override
    public int getPageAmount() {
        return (int) Math.ceil((double) items.size() / (double) getItemListSlots().length);
    }
    
    @Override
    protected List<SlotElement> getPageElements(int page) {
        int length = getItemListSlots().length;
        int from = page * length;
        int to = Math.min(from + length, items.size());
        
        return items.subList(from, to).stream().map(ItemSlotElement::new).collect(Collectors.toList());
    }
    
    public void setItems(@Nullable List<Item> items) {
        this.items = items != null ? items : new ArrayList<>();
        update();
    }
    
}
