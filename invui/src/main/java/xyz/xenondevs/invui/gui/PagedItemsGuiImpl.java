package xyz.xenondevs.invui.gui;

import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.state.MutableProperty;
import xyz.xenondevs.invui.state.Property;

import java.util.ArrayList;
import java.util.List;
import java.util.SequencedSet;
import java.util.function.Supplier;

final class PagedItemsGuiImpl<C extends Item> extends AbstractPagedGui<C> {
    
    public PagedItemsGuiImpl(int width, int height, List<? extends C> items, SequencedSet<Slot> contentListSlots) {
        super(width, height, contentListSlots, Property.of(items));
        bake();
    }
    
    public PagedItemsGuiImpl(
        Structure structure,
        MutableProperty<Integer> page,
        Property<? extends List<? extends C>> items
    ) {
        super(structure, page, items);
        bake();
    }
    
    @Override
    public void bake() {
        int contentSize = contentListSlots.length;
        
        List<List<SlotElement>> pages = new ArrayList<>();
        List<SlotElement> page = new ArrayList<>(contentSize);
        
        for (Item item : getContent()) {
            page.add(new SlotElement.Item(item));
            
            if (page.size() >= contentSize) {
                pages.add(page);
                page = new ArrayList<>(contentSize);
            }
        }
        
        if (!page.isEmpty()) {
            pages.add(page);
        }
        
        setPages(pages);
        update();
    }
    
    public static final class Builder<C extends Item> extends AbstractBuilder<C> {
        
        public Builder() {
            super(PagedItemsGuiImpl::new);
        }
        
    }
    
}
