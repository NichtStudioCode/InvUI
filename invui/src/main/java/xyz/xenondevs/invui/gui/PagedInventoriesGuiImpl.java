package xyz.xenondevs.invui.gui;

import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.inventory.Inventory;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.state.MutableProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.SequencedSet;

final class PagedInventoriesGuiImpl<C extends Inventory> extends AbstractPagedGui<C> {
    
    public PagedInventoriesGuiImpl(
        int width, int height,
        List<? extends C> inventories,
        SequencedSet<? extends Slot> contentListSlots
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
    public void bake() {
        int contentSize = contentListSlots.length;
        
        List<? extends Inventory> inventories = getContent();
        List<List<SlotElement>> pages = new ArrayList<>();
        List<SlotElement> page = new ArrayList<>(contentSize);
        
        for (Inventory inventory : inventories) {
            for (int slot = 0; slot < inventory.getSize(); slot++) {
                page.add(new SlotElement.InventoryLink(inventory, slot));
                
                if (page.size() >= contentSize) {
                    pages.add(page);
                    page = new ArrayList<>(contentSize);
                }
            }
        }
        
        if (!page.isEmpty()) {
            pages.add(page);
        }
        
        setBakedPages(pages);
        setPage(getPage()); // corrects page and refreshes content
    }
    
    public static final class Builder<C extends Inventory> extends AbstractBuilder<C> {
        
        public Builder() {
            super(PagedInventoriesGuiImpl::new);
        }
        
    }
    
}
