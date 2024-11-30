package xyz.xenondevs.invui.gui;

import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.item.Item;

import java.util.ArrayList;
import java.util.List;

final class ScrollItemsGuiImpl<C extends Item> extends AbstractScrollGui<C> {
    
    public ScrollItemsGuiImpl(int width, int height, @Nullable List<C> items, int... contentListSlots) {
        super(width, height, false, contentListSlots);
        setContent(items);
    }
    
    public ScrollItemsGuiImpl(@Nullable List<C> items, Structure structure) {
        super(structure.getWidth(), structure.getHeight(), false, structure);
        setContent(items);
    }
    
    @Override
    public void bake() {
        ArrayList<SlotElement> elements = new ArrayList<>();
        
        if (content != null) {
            for (Item item : content) {
                elements.add(new SlotElement.Item(item));
            }
        }
        
        this.elements = elements;
        update();
    }
    
    public static final class Builder<C extends Item> extends AbstractBuilder<C> {
        
        @Override
        public ScrollGui<C> build() {
            if (structure == null)
                throw new IllegalStateException("Structure is not defined.");
            
            var gui = new ScrollItemsGuiImpl<>(content, structure);
            applyModifiers(gui);
            return gui;
        }
        
    }
    
}
