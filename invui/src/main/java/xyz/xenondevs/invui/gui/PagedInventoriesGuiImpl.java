package xyz.xenondevs.invui.gui;

import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.gui.structure.Structure;
import xyz.xenondevs.invui.inventory.Inventory;
import xyz.xenondevs.invui.inventory.VirtualInventory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

final class PagedInventoriesGuiImpl extends AbstractPagedGui<Inventory> {
    
    private final BiConsumer<Integer, Integer> resizeHandler = (from, to) -> bake();
    
    public PagedInventoriesGuiImpl(int width, int height, @Nullable List<Inventory> inventories, int... contentListSlots) {
        super(width, height, false, contentListSlots);
        setContent(inventories);
    }
    
    public PagedInventoriesGuiImpl(@Nullable List<Inventory> inventories, Structure structure) {
        super(structure.getWidth(), structure.getHeight(), false, structure);
        setContent(inventories);
    }
    
    @SuppressWarnings("DuplicatedCode")
    @Override
    public void setContent(@Nullable List<Inventory> content) {
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
        
        for (Inventory inventory : content) {
            for (int slot = 0; slot < inventory.getSize(); slot++) {
                page.add(new SlotElement.InventorySlotElement(inventory, slot));
                
                if (page.size() >= contentSize) {
                    pages.add(page);
                    page = new ArrayList<>(contentSize);
                }
            }
        }
        
        if (!page.isEmpty()) {
            pages.add(page);
        }
        
        this.pages = pages;
        update();
    }
    
    public static final class Builder extends AbstractBuilder<Inventory> {
        
        @Override
        public PagedGui<Inventory> build() {
            if (structure == null)
                throw new IllegalStateException("Structure is not defined.");
            
            var gui = new PagedInventoriesGuiImpl(content, structure);
            applyModifiers(gui);
            return gui;
        }
        
    }
    
}
