package xyz.xenondevs.invui.gui;

import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

final class ScrollNestedGuiImpl<C extends Gui> extends AbstractScrollGui<C> {
    
    public ScrollNestedGuiImpl(int width, int height, @Nullable List<C> guis, int... contentListSlots) {
        super(width, height, false, contentListSlots);
        setContent(guis);
    }
    
    public ScrollNestedGuiImpl(@Nullable List<C> guis, Structure structure) {
        super(structure.getWidth(), structure.getHeight(), false, structure);
        setContent(guis);
    }
    
    @Override
    public void bake() {
        ArrayList<SlotElement> elements = new ArrayList<>();
        var content = getContent();
        if (content != null) {
            for (Gui gui : content) {
                for (int i = 0; i < gui.getSize(); i++) {
                    elements.add(new SlotElement.GuiLink(gui, i));
                }
            }
        }
        
        setElements(elements);
        update();
    }
    
    public static final class Builder<C extends Gui> extends AbstractBuilder<C> {
        
        @Override
        public ScrollGui<C> build() {
            if (structure == null)
                throw new IllegalStateException("Structure is not defined.");
            
            var gui = new ScrollNestedGuiImpl<>(content, structure);
            applyModifiers(gui);
            return gui;
        }
        
    }
    
}
