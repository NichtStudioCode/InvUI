package xyz.xenondevs.invui.gui;

import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.state.MutableProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.SequencedSet;

final class PagedNestedGuiImpl<C extends Gui> extends AbstractPagedGui<C> {
    
    public PagedNestedGuiImpl(
        int width, int height,
        List<? extends C> guis,
        SequencedSet<? extends Slot> contentListSlots
    ) {
        super(width, height, contentListSlots, MutableProperty.of(guis));
        bake();
    }
    
    public PagedNestedGuiImpl(
        Structure structure,
        MutableProperty<Integer> page,
        MutableProperty<List<? extends C>> guis,
        MutableProperty<Boolean> frozen,
        MutableProperty<Boolean> ignoreObscuredInventorySlots,
        MutableProperty<@Nullable ItemProvider> background
    ) {
        super(structure, page, guis, frozen, ignoreObscuredInventorySlots, background);
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
        
        setBakedPages(pages);
        setPage(getPage()); // corrects page and refreshes content
    }
    
    public static final class Builder<C extends Gui> extends AbstractBuilder<C> {
        
        public Builder() {
            super(PagedNestedGuiImpl::new);
        }
        
    }
    
}
