package xyz.xenondevs.invui.gui;

import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.internal.util.SlotUtils;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.state.MutableProperty;

import java.util.List;

final class ScrollNestedGuiImpl<C extends Gui> extends AbstractScrollGui<C> {
    
    public ScrollNestedGuiImpl(
        int width, int height,
        List<? extends C> guis,
        List<? extends Slot> contentListSlots,
        LineOrientation direction
    ) {
        super(width, height, contentListSlots, direction, MutableProperty.of(guis));
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
    protected void updateContent() {
        switch(getLineOrientation()) {
            case HORIZONTAL -> updateContentHorizontal();
            case VERTICAL -> updateContentVertical();
        }
    }
    
    private void updateContentHorizontal() {
        int topLine = getLine();
        List<Slot> cls = getContentListSlots();
        List<C> content = getContent();
        
        slot:
        for (Slot slot : cls) {
            int line = slot.y() - min.y() + topLine;
            int offset = slot.x() - min.x();
            
            for (Gui gui : content) {
                if (gui.getHeight() > line) {
                    setSlotElement(slot, SlotUtils.getGuiLinkOrNull(gui, offset, line));
                    continue slot;
                }
                line -= gui.getHeight();
            }
            
            setSlotElement(slot, null); // no gui for slot
        }
    }
    
    private void updateContentVertical() {
        int topLine = getLine();
        List<Slot> cls = getContentListSlots();
        List<C> content = getContent();
        
        slot:
        for (Slot slot : cls) {
            int line = slot.x() - min.x() + topLine;
            int offset = slot.y() - min.y();
            
            for (Gui gui : content) {
                if (gui.getWidth() > line) {
                    setSlotElement(slot, SlotUtils.getGuiLinkOrNull(gui, line, offset));
                    continue slot;
                }
                line -= gui.getWidth();
            }
            
            setSlotElement(slot, null); // no gui for slot
        }
    }
    
    @Override
    public int getLineCount() {
        if (getLineOrientation() == LineOrientation.HORIZONTAL) {
            return getContent().stream().mapToInt(Gui::getHeight).sum();
        } else {
            return getContent().stream().mapToInt(Gui::getWidth).sum();
        }
    }
    
    public static final class Builder<C extends Gui> extends AbstractBuilder<C> {
        
        public Builder() {
            super(ScrollNestedGuiImpl::new);
        }
        
    }
    
}
