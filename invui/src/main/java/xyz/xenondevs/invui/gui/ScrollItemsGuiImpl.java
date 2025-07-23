package xyz.xenondevs.invui.gui;

import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.state.MutableProperty;
import xyz.xenondevs.invui.state.Property;

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
        super(width, height, contentListSlots, horizontalLines, Property.of(items));
        bake();
    }
    
    public ScrollItemsGuiImpl(
        Structure structure,
        MutableProperty<Integer> line,
        Property<? extends List<? extends C>> items
    ) {
        super(structure, line, items);
        bake();
    }
    
    @Override
    public void bake() {
        ArrayList<SlotElement> elements = new ArrayList<>();
        
        for (Item item : getContent()) {
            elements.add(new SlotElement.Item(item));
        }
        
        setElements(elements);
        update();
    }
    
    public static final class Builder<C extends Item> extends AbstractBuilder<C> {
        
        public Builder() {
            super(ScrollItemsGuiImpl::new);
        }
        
    }
    
}
