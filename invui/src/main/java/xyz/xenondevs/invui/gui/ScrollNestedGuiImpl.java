package xyz.xenondevs.invui.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

final class ScrollNestedGuiImpl<C extends Gui> extends AbstractScrollGui<C> {
    
    public ScrollNestedGuiImpl(int width, int height, List<? extends C> guis, int... contentListSlots) {
        super(width, height, false, contentListSlots);
        setContent(guis);
    }
    
    public ScrollNestedGuiImpl(Supplier<? extends List<? extends C>> guis, Structure structure) {
        super(structure.getWidth(), structure.getHeight(), false, structure);
        setContentSupplier(guis);
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
