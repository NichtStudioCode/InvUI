package xyz.xenondevs.invui.gui;

import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.state.MutableProperty;

import java.util.List;
import java.util.function.Function;

class ScrollSlotElementsGuiImpl<C> extends AbstractScrollGui<C> {
    
    private final Function<? super C, ? extends SlotElement> mapper;
    
    protected ScrollSlotElementsGuiImpl(
        int width, int height,
        List<? extends C> content,
        List<? extends Slot> contentListSlots,
        LineOrientation orientation,
        Function<? super C, ? extends SlotElement> mapper
    ) {
        super(width, height, contentListSlots, orientation, MutableProperty.of(content));
        this.mapper = mapper;
        bake();
    }
    
    protected ScrollSlotElementsGuiImpl(
        Structure structure,
        MutableProperty<Integer> line,
        MutableProperty<List<? extends C>> content,
        MutableProperty<Boolean> frozen,
        MutableProperty<Boolean> ignoreObscuredInventorySlots,
        MutableProperty<@Nullable ItemProvider> background,
        Function<? super C, ? extends SlotElement> mapper
    ) {
        super(structure, line, content, frozen, ignoreObscuredInventorySlots, background);
        this.mapper = mapper;
        bake();
    }
    
    @Override
    protected void updateContent() {
        int topLine = getLine();
        List<Slot> cls = getContentListSlots();
        List<C> content = getContent();
        
        if (getLineOrientation() == LineOrientation.HORIZONTAL) {
            for (Slot slot : cls) {
                int line = slot.y() - min.y() + topLine;
                int offset = slot.x() - min.x();
                int i = line * lineLength + offset;
                SlotElement element = i < content.size() 
                    ? mapper.apply(content.get(i)) 
                    : null;
                setSlotElement(slot, element);
            }
        } else {
            for (Slot slot : cls) {
                int line = slot.x() - min.x() + topLine;
                int offset = slot.y() - min.y();
                int i = line * lineLength + offset;
                SlotElement element = i < content.size() 
                    ? mapper.apply(content.get(i)) 
                    : null;
                setSlotElement(slot, element);
            }
        }
    }
    
    @Override
    public int getLineCount() {
        if (lineLength <= 0)
            return 0;
        
        return Math.ceilDiv(getContent().size(), lineLength);
    }

    public static final class Builder<C extends SlotElement> extends AbstractBuilder<C> {
        
        public Builder() {
            super((structure, line, content, frozen, ignoreObscuredInventorySlots, background) ->
                new ScrollSlotElementsGuiImpl<>(
                    structure,
                    line,
                    content,
                    frozen,
                    ignoreObscuredInventorySlots,
                    background,
                    Function.identity()
                )
            );
        }
        
    }
    
}



