package xyz.xenondevs.invui.gui;

import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.state.MutableProperty;

import java.util.List;

final class PagedItemsGuiImpl<C extends Item> extends PagedSlotElementsGuiImpl<C> {
    
    public PagedItemsGuiImpl(
        int width, int height,
        List<? extends C> items,
        List<? extends Slot> contentListSlots
    ) {
        super(width, height, items, contentListSlots, SlotElement.Item::new);
    }
    
    public PagedItemsGuiImpl(
        Structure structure,
        MutableProperty<Integer> page,
        MutableProperty<List<? extends C>> items,
        MutableProperty<Boolean> frozen,
        MutableProperty<Boolean> ignoreObscuredInventorySlots,
        MutableProperty<@Nullable ItemProvider> background
    ) {
        super(structure, page, items, frozen, ignoreObscuredInventorySlots, background, SlotElement.Item::new);
    }
    
    public static final class Builder<C extends Item> extends AbstractBuilder<C> {
        
        public Builder() {
            super(PagedItemsGuiImpl::new);
        }
        
    }
    
}
