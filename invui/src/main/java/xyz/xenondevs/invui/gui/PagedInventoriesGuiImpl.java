package xyz.xenondevs.invui.gui;

import xyz.xenondevs.invui.inventory.Inventory;
import xyz.xenondevs.invui.inventory.VirtualInventory;
import xyz.xenondevs.invui.state.MutableProperty;
import xyz.xenondevs.invui.state.Property;

import java.util.*;
import java.util.function.BiConsumer;

final class PagedInventoriesGuiImpl<C extends Inventory> extends AbstractPagedGui<C> {
    
    private final BiConsumer<Integer, Integer> resizeHandler = (from, to) -> bake();
    private final Set<VirtualInventory> lastKnownInventories = new HashSet<>();
    
    public PagedInventoriesGuiImpl(
        int width, int height,
        List<? extends C> inventories,
        SequencedSet<? extends Slot> contentListSlots
    ) {
        super(width, height, contentListSlots, Property.of(inventories));
        bake();
    }
    
    public PagedInventoriesGuiImpl(
        Structure structure,
        MutableProperty<Integer> page,
        Property<? extends List<? extends C>> inventories
    ) {
        super(structure, page, inventories);
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
        
        applyResizeHandlers(inventories);
        setPages(pages);
        update();
    }
    
    @SuppressWarnings("DuplicatedCode")
    private void applyResizeHandlers(List<? extends Inventory> inventories) {
        // remove resize handlers from previous inventories
        for (var inv : lastKnownInventories) {
            inv.removeResizeHandler(resizeHandler);
        }
        lastKnownInventories.clear();
        
        // add resize handlers to new inventories
        for (var inv : inventories) {
            if (inv instanceof VirtualInventory vi) {
                vi.addResizeHandler(resizeHandler);
                lastKnownInventories.add(vi);
            }
        }
    }
    
    public static final class Builder<C extends Inventory> extends AbstractBuilder<C> {
        
        public Builder() {
            super(PagedInventoriesGuiImpl::new);
        }
        
    }
    
}
