package xyz.xenondevs.invui.gui;

import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.gui.structure.Structure;

import java.util.ArrayList;
import java.util.List;

final class ScrollNestedGuiImpl extends AbstractScrollGui<Gui> {
    
    public ScrollNestedGuiImpl(int width, int height, @Nullable List<Gui> guis, int... contentListSlots) {
        super(width, height, false, contentListSlots);
        setContent(guis);
    }
    
    public ScrollNestedGuiImpl(@Nullable List<Gui> guis, Structure structure) {
        super(structure.getWidth(), structure.getHeight(), false, structure);
        setContent(guis);
    }
    
    @Override
    public void bake() {
        ArrayList<SlotElement> elements = new ArrayList<>();
        for (Gui gui : content) {
            for (int i = 0; i < gui.getSize(); i++) {
                elements.add(new SlotElement.LinkedSlotElement(gui, i));
            }
        }
        
        this.elements = elements;
        update();
    }
    
    public static final class Builder extends AbstractBuilder<Gui> {
        
        @Override
        public ScrollGui<Gui> build() {
            if (structure == null)
                throw new IllegalStateException("Structure is not defined.");
            
            var gui = new ScrollNestedGuiImpl(content, structure);
            applyModifiers(gui);
            return gui;
        }
        
    }
    
}
