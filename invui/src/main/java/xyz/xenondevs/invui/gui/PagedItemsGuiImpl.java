package xyz.xenondevs.invui.gui;

import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.state.MutableProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.SequencedSet;

final class PagedItemsGuiImpl<C extends Item> extends AbstractPagedGui<C> {
    
    public PagedItemsGuiImpl(
        int width, int height,
        List<? extends C> items,
        SequencedSet<? extends Slot> contentListSlots
    ) {
        super(width, height, contentListSlots, MutableProperty.of(items));
        bake();
    }
    
    public PagedItemsGuiImpl(
        Structure structure,
        MutableProperty<Integer> page,
        MutableProperty<List<? extends C>> items,
        MutableProperty<Boolean> frozen,
        MutableProperty<Boolean> ignoreObscuredInventorySlots,
        MutableProperty<@Nullable ItemProvider> background
    ) {
        super(structure, page, items, frozen, ignoreObscuredInventorySlots, background);
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
        
        setBakedPages(pages);
        setPage(getPage()); // corrects page and refreshes content
    }
    
    public static final class Builder<C extends Item> extends AbstractBuilder<C> {
        
        public Builder() {
            super(PagedItemsGuiImpl::new);
        }
        
    }
    
}
