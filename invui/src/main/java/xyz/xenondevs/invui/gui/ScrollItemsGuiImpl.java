package xyz.xenondevs.invui.gui;

import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.state.MutableProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.SequencedSet;

final class ScrollItemsGuiImpl<C extends Item> extends AbstractScrollGui<C> {
    
    public ScrollItemsGuiImpl(
        int width, int height,
        List<? extends C> items,
        SequencedSet<? extends Slot> contentListSlots,
        boolean horizontalLines
    ) {
        super(width, height, contentListSlots, horizontalLines, MutableProperty.of(items));
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
    public void bake() {
        ArrayList<SlotElement> elements = new ArrayList<>();
        
        for (Item item : getContent()) {
            elements.add(new SlotElement.Item(item));
        }
        
        setElements(elements);
        setLine(getLine()); // corrects line and refreshes content
    }
    
    public static final class Builder<C extends Item> extends AbstractBuilder<C> {
        
        public Builder() {
            super(ScrollItemsGuiImpl::new);
        }
        
    }
    
}
