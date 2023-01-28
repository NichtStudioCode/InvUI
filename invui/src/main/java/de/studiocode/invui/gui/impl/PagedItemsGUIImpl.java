package de.studiocode.invui.gui.impl;

import de.studiocode.invui.gui.AbstractPagedGUI;
import de.studiocode.invui.gui.SlotElement;
import de.studiocode.invui.gui.SlotElement.ItemSlotElement;
import de.studiocode.invui.gui.builder.GUIBuilder;
import de.studiocode.invui.gui.structure.Structure;
import de.studiocode.invui.item.Item;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * A {@link AbstractPagedGUI} that is filled with {@link Item}s.
 *
 * @see GUIBuilder
 * @see PageNestedGUIImpl
 */
public final class PagedItemsGUIImpl extends AbstractPagedGUI<Item> {
    
    private List<Item> items;
    private List<BiConsumer<Integer, Integer>> pageChangeHandlers;
    
    public PagedItemsGUIImpl(int width, int height, @Nullable List<Item> items, int... contentListSlots) {
        super(width, height, false, contentListSlots);
        setContent(items);
    }
    
    public PagedItemsGUIImpl(@Nullable List<Item> items, @NotNull Structure structure) {
        super(structure.getWidth(), structure.getHeight(), false, structure);
        setContent(items);
    }
    
    @Override
    public int getPageAmount() {
        return (int) Math.ceil((double) items.size() / (double) getContentListSlots().length);
    }
    
    @Override
    public void setContent(List<@Nullable Item> content) {
        this.items = items != null ? items : new ArrayList<>();
        update();
    }
    
    @Override
    protected List<SlotElement> getPageElements(int page) {
        int length = getContentListSlots().length;
        int from = page * length;
        int to = Math.min(from + length, items.size());
        
        return items.subList(from, to).stream().map(ItemSlotElement::new).collect(Collectors.toList());
    }
    
}
