package xyz.xenondevs.invui.gui;

import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.item.Item;

import java.util.ArrayList;
import java.util.List;

final class PagedItemsGuiImpl<C extends Item> extends AbstractPagedGui<C> {
    
    public PagedItemsGuiImpl(int width, int height, @Nullable List<C> items, int... contentListSlots) {
        super(width, height, false, contentListSlots);
        setContent(items);
    }
    
    public PagedItemsGuiImpl(@Nullable List<C> items, Structure structure) {
        super(structure.getWidth(), structure.getHeight(), false, structure);
        setContent(items);
    }
    
    @Override
    public void bake() {
        int contentSize = getContentListSlots().length;
        
        List<List<SlotElement>> pages = new ArrayList<>();
        List<SlotElement> page = new ArrayList<>(contentSize);
        
        var content = getContent();
        if (content != null) {
            for (Item item : content) {
                page.add(new SlotElement.Item(item));
                
                if (page.size() >= contentSize) {
                    pages.add(page);
                    page = new ArrayList<>(contentSize);
                }
            }
        }
        
        if (!page.isEmpty()) {
            pages.add(page);
        }
        
        setPages(pages);
        update();
    }
    
    public static final class Builder<C extends Item> extends AbstractBuilder<C> {
        
        @Override
        public PagedGui<C> build() {
            if (structure == null)
                throw new IllegalStateException("Structure is not defined.");
            
            var gui = new PagedItemsGuiImpl<>(content, structure);
            applyModifiers(gui);
            return gui;
        }
        
    }
    
}
