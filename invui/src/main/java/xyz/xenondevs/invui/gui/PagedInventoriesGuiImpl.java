package xyz.xenondevs.invui.gui;

import xyz.xenondevs.invui.inventory.Inventory;
import xyz.xenondevs.invui.inventory.VirtualInventory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

final class PagedInventoriesGuiImpl<C extends Inventory> extends AbstractPagedGui<C> {
    
    private final BiConsumer<Integer, Integer> resizeHandler = (from, to) -> bake();
    
    public PagedInventoriesGuiImpl(int width, int height, List<? extends C> inventories, int... contentListSlots) {
        super(width, height, false, contentListSlots);
        setContent(inventories);
    }
    
    public PagedInventoriesGuiImpl(Supplier<? extends List<? extends C>> inventories, Structure structure) {
        super(structure.getWidth(), structure.getHeight(), false, structure);
        setContentSupplier(inventories);
    }
    
    @SuppressWarnings("DuplicatedCode")
    @Override
    public void setContent(List<? extends C> content) {
        // remove resize handlers from previous inventories
        for (Inventory inventory : getContent()) {
            if (inventory instanceof VirtualInventory) {
                ((VirtualInventory) inventory).removeResizeHandler(resizeHandler);
            }
        }
        
        // set content, bake pages, update
        super.setContent(content);
        
        // add resize handlers to new inventories
        for (Inventory inventory : content) {
            if (inventory instanceof VirtualInventory) {
                ((VirtualInventory) inventory).addResizeHandler(resizeHandler);
            }
        }
    }
    
    @Override
    public void bake() {
        int contentSize = getContentListSlots().length;
        
        List<List<SlotElement>> pages = new ArrayList<>();
        List<SlotElement> page = new ArrayList<>(contentSize);
        
        for (Inventory inventory : getContent()) {
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
        
        setPages(pages);
        update();
    }
    
    public static final class Builder<C extends Inventory> extends AbstractBuilder<C> {
        
        public Builder() {
            super(PagedInventoriesGuiImpl::new);
        }
        
    }
    
}
