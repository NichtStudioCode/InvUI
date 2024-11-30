package xyz.xenondevs.invui.gui;

import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.inventory.Inventory;
import xyz.xenondevs.invui.inventory.VirtualInventory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

final class PagedInventoriesGuiImpl<C extends Inventory> extends AbstractPagedGui<C> {
    
    private final BiConsumer<Integer, Integer> resizeHandler = (from, to) -> bake();
    
    public PagedInventoriesGuiImpl(int width, int height, @Nullable List<C> inventories, int... contentListSlots) {
        super(width, height, false, contentListSlots);
        setContent(inventories);
    }
    
    public PagedInventoriesGuiImpl(@Nullable List<C> inventories, Structure structure) {
        super(structure.getWidth(), structure.getHeight(), false, structure);
        setContent(inventories);
    }
    
    @SuppressWarnings("DuplicatedCode")
    @Override
    public void setContent(@Nullable List<C> content) {
        // remove resize handlers from previous inventories
        if (this.content != null) {
            for (Inventory inventory : this.content) {
                if (inventory instanceof VirtualInventory) {
                    ((VirtualInventory) inventory).removeResizeHandler(resizeHandler);
                }
            }
        }
        
        // set content, bake pages, update
        super.setContent(content);
        
        // add resize handlers to new inventories
        if (this.content != null) {
            for (Inventory inventory : this.content) {
                if (inventory instanceof VirtualInventory) {
                    ((VirtualInventory) inventory).addResizeHandler(resizeHandler);
                }
            }
        }
    }
    
    @Override
    public void bake() {
        int contentSize = getContentListSlots().length;
        
        List<List<SlotElement>> pages = new ArrayList<>();
        List<SlotElement> page = new ArrayList<>(contentSize);
        
        if (content != null) {
            for (Inventory inventory : content) {
                for (int slot = 0; slot < inventory.getSize(); slot++) {
                    page.add(new SlotElement.InventoryLink(inventory, slot));
                    
                    if (page.size() >= contentSize) {
                        pages.add(page);
                        page = new ArrayList<>(contentSize);
                    }
                }
            }
        }
        
        if (!page.isEmpty()) {
            pages.add(page);
        }
        
        this.pages = pages;
        update();
    }
    
    public static final class Builder<C extends Inventory> extends AbstractBuilder<C> {
        
        @Override
        public PagedGui<C> build() {
            if (structure == null)
                throw new IllegalStateException("Structure is not defined.");
            
            var gui = new PagedInventoriesGuiImpl<>(content, structure);
            applyModifiers(gui);
            return gui;
        }
        
    }
    
}
