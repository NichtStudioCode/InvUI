package xyz.xenondevs.invui.gui;

import xyz.xenondevs.invui.item.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

final class ScrollItemsGuiImpl<C extends Item> extends AbstractScrollGui<C> {
    
    public ScrollItemsGuiImpl(int width, int height, List<? extends C> items, int... contentListSlots) {
        super(width, height, false, contentListSlots);
        setContent(items);
    }
    
    public ScrollItemsGuiImpl(Supplier<? extends List<? extends C>> items, Structure structure) {
        super(structure.getWidth(), structure.getHeight(), false, structure);
        setContent(items);
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
