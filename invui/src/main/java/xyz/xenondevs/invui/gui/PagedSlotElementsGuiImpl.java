package xyz.xenondevs.invui.gui;

import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.state.MutableProperty;

import java.util.List;
import java.util.function.Function;

class PagedSlotElementsGuiImpl<C> extends AbstractPagedGui<C> {
    
    private final Function<? super C, ? extends SlotElement> mapper;
    
    protected PagedSlotElementsGuiImpl(
        int width, int height,
        List<? extends C> content,
        List<? extends Slot> contentListSlots,
        Function<? super C, ? extends SlotElement> mapper
    ) {
        super(width, height, contentListSlots, MutableProperty.of(content));
        this.mapper = mapper;
        bake();
    }
    
    protected PagedSlotElementsGuiImpl(
        Structure structure,
        MutableProperty<Integer> page,
        MutableProperty<List<? extends C>> content,
        MutableProperty<Boolean> frozen,
        MutableProperty<Boolean> ignoreObscuredInventorySlots,
        MutableProperty<@Nullable ItemProvider> background,
        Function<? super C, ? extends SlotElement> mapper
    ) {
        super(structure, page, content, frozen, ignoreObscuredInventorySlots, background);
        this.mapper = mapper;
        bake();
    }
    
    @Override
    protected void updateContent() {
        int page = getPage();
        List<Slot> cls = getContentListSlots();
        List<C> content = getContent();
        
        if (page < 0)
            return;
        
        int off = page * cls.size();
        for (int i = 0; i < cls.size(); i++) {
            SlotElement element = (i + off) < content.size() 
                ? mapper.apply(content.get(i + off)) 
                : null;
            setSlotElement(cls.get(i), element);
        }
    }
    
    @Override
    public int getPageCount() {
        var cls = getContentListSlots();
        if (cls.isEmpty())
            return 0;
        
        return Math.ceilDiv(getContent().size(), cls.size());
    }
    
    public static final class Builder<C extends SlotElement> extends AbstractBuilder<C> {
        
        public Builder() {
            super((structure, page, content, frozen, ignoreObscuredInventorySlots, background) ->
                new PagedSlotElementsGuiImpl<>(
                    structure,
                    page,
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



