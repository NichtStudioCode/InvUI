package xyz.xenondevs.invui.gui;

import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.inventory.Inventory;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.state.MutableProperty;

import java.util.List;

final class PagedInventoriesGuiImpl<C extends Inventory> extends AbstractPagedGui<C> {
    
    public PagedInventoriesGuiImpl(
        int width, int height,
        List<? extends C> inventories,
        List<? extends Slot> contentListSlots
    ) {
        super(width, height, contentListSlots, MutableProperty.of(inventories));
        bake();
    }
    
    public PagedInventoriesGuiImpl(
        Structure structure,
        MutableProperty<Integer> page,
        MutableProperty<List<? extends C>> inventories,
        MutableProperty<Boolean> frozen,
        MutableProperty<Boolean> ignoreObscuredInventorySlots,
        MutableProperty<@Nullable ItemProvider> background
    ) {
        super(structure, page, inventories, frozen, ignoreObscuredInventorySlots, background);
        bake();
    }
    
    @Override
    protected void updateContent() {
        int page = getPage();
        List<Slot> cls = getContentListSlots();
        List<C> content = getContent();
        
        if (page < 0)
            return;
        
        int skip = page * cls.size();
        int i = 0;
        for (Inventory inv : content) {
            if (skip >= inv.getSize()) {
                skip -= inv.getSize();
                continue;
            }
            for (int invSlot = skip; invSlot < inv.getSize(); invSlot++) {
                if (i >= cls.size())
                    return;
                Slot guiSlot = cls.get(i++);
                setSlotElement(guiSlot, new SlotElement.InventoryLink(inv, invSlot));
            }
            skip = 0;
        }
        for (; i < cls.size(); i++) {
            setSlotElement(cls.get(i), null);
        }
    }
    
    @Override
    public int getPageCount() {
        var cls = getContentListSlots();
        if (cls.isEmpty())
            return 0;
        
        int slots = getContent().stream().mapToInt(Inventory::getSize).sum();
        return Math.ceilDiv(slots, cls.size());
    }
    
    public static final class Builder<C extends Inventory> extends AbstractBuilder<C> {
        
        public Builder() {
            super(PagedInventoriesGuiImpl::new);
        }
        
    }
    
}
