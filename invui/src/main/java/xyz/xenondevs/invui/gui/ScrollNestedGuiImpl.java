package xyz.xenondevs.invui.gui;

import xyz.xenondevs.invui.state.MutableProperty;
import xyz.xenondevs.invui.state.Property;

import java.util.ArrayList;
import java.util.List;
import java.util.SequencedSet;

final class ScrollNestedGuiImpl<C extends Gui> extends AbstractScrollGui<C> {
    
    public ScrollNestedGuiImpl(
        int width, int height,
        List<? extends C> guis,
        SequencedSet<Slot> contentListSlots,
        boolean horizontalLines
    ) {
        super(width, height, contentListSlots, horizontalLines, Property.of(guis));
        setContent(guis);
    }
    
    public ScrollNestedGuiImpl(
        Structure structure,
        MutableProperty<Integer> line,
        Property<? extends List<? extends C>> guis
    ) {
        super(structure, line, guis);
        bake();
    }
    
    @Override
    public void bake() {
        ArrayList<SlotElement> elements = new ArrayList<>();
        for (Gui gui : getContent()) {
            for (int i = 0; i < gui.getSize(); i++) {
                elements.add(new SlotElement.GuiLink(gui, i));
            }
        }
        
        setElements(elements);
        update();
    }
    
    public static final class Builder<C extends Gui> extends AbstractBuilder<C> {
        
        public Builder() {
            super(ScrollNestedGuiImpl::new);
        }
        
    }
    
}
