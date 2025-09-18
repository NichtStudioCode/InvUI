package xyz.xenondevs.invui.gui;

import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.state.MutableProperty;

import java.util.List;

final class PagedItemsGuiImpl<C extends Item> extends AbstractPagedGui<C> {
    
    public PagedItemsGuiImpl(
        int width, int height,
        List<? extends C> items,
        List<? extends Slot> contentListSlots
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
    protected void updateContent() {
        int page = getPage();
        List<Slot> cls = getContentListSlots();
        List<C> content = getContent();
        
        if (page < 0)
            return;
        
        int off = page * cls.size();
        for (int i = 0; i < cls.size(); i++) {
            Item item = (i + off) < content.size() ? content.get(i + off) : null;
            setSlotElement(cls.get(i), item != null ? new SlotElement.Item(item) : null);
        }
    }
    
    @Override
    public int getPageCount() {
        var cls = getContentListSlots();
        if (cls.isEmpty()) 
            return 0;
        
        return Math.ceilDiv(getContent().size(), cls.size());
    }
    
    public static final class Builder<C extends Item> extends AbstractBuilder<C> {
        
        public Builder() {
            super(PagedItemsGuiImpl::new);
        }
        
    }
    
}
