package xyz.xenondevs.invui.gui;

import xyz.xenondevs.invui.item.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

final class PagedItemsGuiImpl<C extends Item> extends AbstractPagedGui<C> {
    
    public PagedItemsGuiImpl(int width, int height, List<? extends C> items, int... contentListSlots) {
        super(width, height, false, contentListSlots);
        setContent(items);
    }
    
    public PagedItemsGuiImpl(Supplier<? extends List<? extends C>> items, Structure structure) {
        super(structure.getWidth(), structure.getHeight(), false, structure);
        setContent(items);
    }
    
    @Override
    public void bake() {
        int contentSize = getContentListSlots().length;
        
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