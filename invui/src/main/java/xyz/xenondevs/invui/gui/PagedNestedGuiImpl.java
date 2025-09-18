package xyz.xenondevs.invui.gui;

import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.internal.util.SlotUtils;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.state.MutableProperty;

import java.util.List;

final class PagedNestedGuiImpl<C extends Gui> extends AbstractPagedGui<C> {
    
    public PagedNestedGuiImpl(
        int width, int height,
        List<? extends C> guis,
        List<? extends Slot> contentListSlots
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
    protected void updateContent() {
        int page = getPage();
        List<Slot> cls = getContentListSlots();
        List<C> content = getContent();
        
        if (page < 0)
            return;
        
        if (page < content.size()) {
            Gui gui = content.get(page);
            Slot min = SlotUtils.min(cls);
            for (Slot slot : cls) {
                setSlotElement(slot, SlotUtils.getGuiLinkOrNull(gui, slot.x() - min.x(), slot.y() - min.y()));
            }
        } else {
            for (Slot slot : cls) {
                setSlotElement(slot, null);
            }
        }
    }
    
    @Override
    public int getPageCount() {
        return getContent().size();
    }
    
    public static final class Builder<C extends Gui> extends AbstractBuilder<C> {
        
        public Builder() {
            super(PagedNestedGuiImpl::new);
        }
        
    }
    
}
