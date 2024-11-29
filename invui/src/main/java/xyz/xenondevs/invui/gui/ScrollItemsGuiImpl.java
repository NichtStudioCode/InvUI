package xyz.xenondevs.invui.gui;

import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.gui.structure.Structure;
import xyz.xenondevs.invui.item.Item;

import java.util.ArrayList;
import java.util.List;

final class ScrollItemsGuiImpl extends AbstractScrollGui<Item> {
    
    public ScrollItemsGuiImpl(int width, int height, @Nullable List<Item> items, int... contentListSlots) {
        super(width, height, false, contentListSlots);
        setContent(items);
    }
    
    public ScrollItemsGuiImpl(@Nullable List<Item> items, Structure structure) {
        super(structure.getWidth(), structure.getHeight(), false, structure);
        setContent(items);
    }
    
    @Override
    public void bake() {
        ArrayList<SlotElement> elements = new ArrayList<>(content.size());
        for (Item item : content) {
            elements.add(new SlotElement.ItemSlotElement(item));
        }
        
        this.elements = elements;
        update();
    }
    
    public static final class Builder extends AbstractBuilder<Item> {
        
        @Override
        public ScrollGui<Item> build() {
            if (structure == null)
                throw new IllegalStateException("Structure is not defined.");
            
            var gui = new ScrollItemsGuiImpl(content, structure);
            applyModifiers(gui);
            return gui;
        }
        
    }
    
}
