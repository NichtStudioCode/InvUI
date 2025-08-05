package xyz.xenondevs.invui.gui;

import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.state.MutableProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.SequencedSet;

final class ScrollNestedGuiImpl<C extends Gui> extends AbstractScrollGui<C> {
    
    public ScrollNestedGuiImpl(
        int width, int height,
        List<? extends C> guis,
        SequencedSet<? extends Slot> contentListSlots,
        boolean horizontalLines
    ) {
        super(width, height, contentListSlots, horizontalLines, MutableProperty.of(guis));
        setContent(guis);
    }
    
    public ScrollNestedGuiImpl(
        Structure structure,
        MutableProperty<Integer> line,
        MutableProperty<List<? extends C>> guis,
        MutableProperty<Boolean> frozen,
        MutableProperty<Boolean> ignoreObscuredInventorySlots,
        MutableProperty<@Nullable ItemProvider> background
    ) {
        super(structure, line, guis, frozen, ignoreObscuredInventorySlots, background);
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
        setLine(getLine()); // corrects line and refreshes content
    }
    
    public static final class Builder<C extends Gui> extends AbstractBuilder<C> {
        
        public Builder() {
            super(ScrollNestedGuiImpl::new);
        }
        
    }
    
}
