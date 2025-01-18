package xyz.xenondevs.invui.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

final class PagedNestedGuiImpl<C extends Gui> extends AbstractPagedGui<C> {
    
    public PagedNestedGuiImpl(int width, int height, List<? extends C> guis, int... contentListSlots) {
        super(width, height, false, contentListSlots);
        setContent(guis);
    }
    
    public PagedNestedGuiImpl(Supplier<? extends List<? extends C>> guis, Structure structure) {
        super(structure.getWidth(), structure.getHeight(), false, structure);
        setContentSupplier(guis);
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
