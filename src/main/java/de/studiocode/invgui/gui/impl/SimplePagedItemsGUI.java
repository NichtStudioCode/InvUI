package de.studiocode.invgui.gui.impl;

import de.studiocode.invgui.gui.SlotElement;
import de.studiocode.invgui.gui.SlotElement.ItemSlotElement;
import de.studiocode.invgui.item.Item;
import de.studiocode.invgui.item.impl.pagedgui.BackItem;
import de.studiocode.invgui.item.impl.pagedgui.ForwardItem;
import de.studiocode.invgui.item.itembuilder.ItemBuilder;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SimplePagedItemsGUI extends PagedGUI {
    
    private final List<Item> items;
    
    public SimplePagedItemsGUI(int width, int height,
                               int backItemSlot, Function<PagedGUI, ItemBuilder> backFunction,
                               int forwardItemSlot, Function<PagedGUI, ItemBuilder> forwardFunction,
                               List<Item> items, int... itemListSlots) {
        
        super(width, height, false, itemListSlots);
        this.items = items;
        
        setControlItems(backItemSlot, new BackItem(this, backFunction),
            forwardItemSlot, new ForwardItem(this, forwardFunction));
        update();
    }
    
    @Override
    public int getPageAmount() {
        return (int) Math.ceil((double) items.size() / (double) getItemListSlots().length);
    }
    
    @Override
    protected List<SlotElement> getPageItems(int page) {
        int length = getItemListSlots().length;
        int from = page * length;
        int to = Math.min(from + length, items.size());
        
        return items.subList(from, to).stream().map(ItemSlotElement::new).collect(Collectors.toList());
    }
    
}
