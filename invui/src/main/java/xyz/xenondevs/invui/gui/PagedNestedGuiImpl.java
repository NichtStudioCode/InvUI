package xyz.xenondevs.invui.gui;

import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

final class PagedNestedGuiImpl<C extends Gui> extends AbstractPagedGui<C> {
    
    public PagedNestedGuiImpl(int width, int height, @Nullable List<C> guis, int... contentListSlots) {
        super(width, height, false, contentListSlots);
        setContent(guis);
    }
    
    public PagedNestedGuiImpl(@Nullable List<C> guis, Structure structure) {
        super(structure.getWidth(), structure.getHeight(), false, structure);
        setContent(guis);
    }
    
    @Override
    public void bake() {
        List<List<SlotElement>> pages = new ArrayList<>();
        
        if (content != null) {
            for (Gui gui : content) {
                List<SlotElement> page = new ArrayList<>(gui.getSize());
                for (int slot = 0; slot < gui.getSize(); slot++) {
                    page.add(new SlotElement.GuiLink(gui, slot));
                }
                
                pages.add(page);
            }
        }
        
        this.pages = pages;
        update();
    }
    
    public static final class Builder<C extends Gui> extends AbstractBuilder<C> {
        
        @Override
        public PagedGui<C> build() {
            if (structure == null)
                throw new IllegalStateException("Structure is not defined.");
            
            var gui = new PagedNestedGuiImpl<>(content, structure);
            applyModifiers(gui);
            return gui;
        }
        
    }
    
}
