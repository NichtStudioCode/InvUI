package xyz.xenondevs.invui.gui;

import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.state.MutableProperty;

import java.util.List;

final class ScrollItemsGuiImpl<C extends Item> extends ScrollSlotElementsGuiImpl<C> {
    
    public ScrollItemsGuiImpl(
        int width, int height,
        List<? extends C> items,
        List<? extends Slot> contentListSlots,
        LineOrientation direction
    ) {
        super(width, height, items, contentListSlots, direction, SlotElement.Item::new);
    }
    
    public ScrollItemsGuiImpl(
        Structure structure,
        MutableProperty<Integer> line,
        MutableProperty<List<? extends C>> items,
        MutableProperty<Boolean> frozen,
        MutableProperty<Boolean> ignoreObscuredInventorySlots,
        MutableProperty<@Nullable ItemProvider> background
    ) {
        super(structure, line, items, frozen, ignoreObscuredInventorySlots, background, SlotElement.Item::new);
    }
    
    public static final class Builder<C extends Item> extends AbstractBuilder<C> {
        
        public Builder() {
            super(ScrollItemsGuiImpl::new);
        }
        
    }
    
}
