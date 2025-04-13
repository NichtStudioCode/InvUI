package xyz.xenondevs.invui.gui;

import xyz.xenondevs.invui.state.MutableProperty;
import xyz.xenondevs.invui.state.Property;

import java.util.ArrayList;
import java.util.List;
import java.util.SequencedSet;

final class PagedNestedGuiImpl<C extends Gui> extends AbstractPagedGui<C> {
    
    public PagedNestedGuiImpl(
        int width, int height,
        List<? extends C> guis,
        SequencedSet<Slot> contentListSlots
    ) {
        super(width, height, contentListSlots, Property.of(guis));
        bake();
    }
    
    public PagedNestedGuiImpl(
        Structure structure,
        MutableProperty<Integer> page,
        Property<? extends List<? extends C>> guis
    ) {
        super(structure, page, guis);
        bake();
    }
    
    @Override
    public void bake() {
        List<List<SlotElement>> pages = new ArrayList<>();
        
        for (Gui gui : getContent()) {
            List<SlotElement> page = new ArrayList<>(gui.getSize());
            for (int slot = 0; slot < gui.getSize(); slot++) {
                page.add(new SlotElement.GuiLink(gui, slot));
            }
            
            pages.add(page);
        }
        
        setPages(pages);
        update();
    }
    
    public static final class Builder<C extends Gui> extends AbstractBuilder<C> {
        
        public Builder() {
            super(PagedNestedGuiImpl::new);
        }
        
    }
    
}
