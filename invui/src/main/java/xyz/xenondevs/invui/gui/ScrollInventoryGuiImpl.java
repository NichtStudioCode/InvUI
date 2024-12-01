package xyz.xenondevs.invui.gui;

import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.inventory.Inventory;
import xyz.xenondevs.invui.inventory.VirtualInventory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

final class ScrollInventoryGuiImpl<C extends Inventory> extends AbstractScrollGui<C> {
    
    private final BiConsumer<Integer, Integer> resizeHandler = (from, to) -> bake();
    
    public ScrollInventoryGuiImpl(int width, int height, @Nullable List<C> inventories, int... contentListSlots) {
        super(width, height, false, contentListSlots);
        setContent(inventories);
    }
    
    public ScrollInventoryGuiImpl(@Nullable List<C> inventories, Structure structure) {
        super(structure.getWidth(), structure.getHeight(), false, structure);
        setContent(inventories);
    }
    
    @SuppressWarnings("DuplicatedCode")
    @Override
    public void setContent(@Nullable List<C> content) {
        // remove resize handlers from previous inventories
        var previousContent = getContent();
        if (previousContent != null) {
            for (Inventory inventory : previousContent) {
                if (inventory instanceof VirtualInventory) {
                    ((VirtualInventory) inventory).removeResizeHandler(resizeHandler);
                }
            }
        }
        
        // set content, bake pages, update
        super.setContent(content);
        
        // add resize handlers to new inventories
        if (content != null) {
            for (Inventory inventory : content) {
                if (inventory instanceof VirtualInventory) {
                    ((VirtualInventory) inventory).addResizeHandler(resizeHandler);
                }
            }
        }
    }
    
    @Override
    public void bake() {
        List<SlotElement> elements = new ArrayList<>();
        var content = getContent();
        if (content != null) {
            for (Inventory inventory : content) {
                for (int i = 0; i < inventory.getSize(); i++) {
                    elements.add(new SlotElement.InventoryLink(inventory, i));
                }
            }
        }
        
        setElements(elements);
        update();
    }
    
    public static final class Builder<C extends Inventory> extends AbstractBuilder<C> {
        
        @Override
        public ScrollGui<C> build() {
            if (structure == null)
                throw new IllegalStateException("Structure is not defined.");
            
            var gui = new ScrollInventoryGuiImpl<>(content, structure);
            applyModifiers(gui);
            return gui;
        }
        
    }
    
}
