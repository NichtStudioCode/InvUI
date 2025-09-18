package xyz.xenondevs.invui.gui;

import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.state.MutableProperty;

import java.util.List;

final class ScrollItemsGuiImpl<C extends Item> extends AbstractScrollGui<C> {
    
    public ScrollItemsGuiImpl(
        int width, int height,
        List<? extends C> items,
        List<? extends Slot> contentListSlots,
        LineOrientation direction
    ) {
        super(width, height, contentListSlots, direction, MutableProperty.of(items));
        bake();
    }
    
    public ScrollItemsGuiImpl(
        Structure structure,
        MutableProperty<Integer> line,
        MutableProperty<List<? extends C>> items,
        MutableProperty<Boolean> frozen,
        MutableProperty<Boolean> ignoreObscuredInventorySlots,
        MutableProperty<@Nullable ItemProvider> background
    ) {
        super(structure, line, items, frozen, ignoreObscuredInventorySlots, background);
        bake();
    }
    
    @Override
    protected void updateContent() {
        int topLine = getLine();
        List<Slot> cls = getContentListSlots();
        List<C> content = getContent();
        
        if (getLineOrientation() == LineOrientation.HORIZONTAL) {
            for (Slot slot : cls) {
                int line = slot.y() - min.y() + topLine;
                int offset = slot.x() - min.x();
                int i = line * lineLength + offset;
                setSlotElement(slot, i < content.size() ? new SlotElement.Item(content.get(i)) : null);
            }
        } else {
            for (Slot slot : cls) {
                int line = slot.x() - min.x() + topLine;
                int offset = slot.y() - min.y();
                int i = line * lineLength + offset;
                setSlotElement(slot, i < content.size() ? new SlotElement.Item(content.get(i)) : null);
            }
        }
    }
    
    @Override
    public int getLineCount() {
        if (lineLength <= 0)
            return 0;
        
        return Math.ceilDiv(getContent().size(), lineLength);
    }
    
    public static final class Builder<C extends Item> extends AbstractBuilder<C> {
        
        public Builder() {
            super(ScrollItemsGuiImpl::new);
        }
        
    }
    
}
