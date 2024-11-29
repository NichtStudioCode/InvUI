package xyz.xenondevs.invui.gui;

import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.gui.structure.Structure;
import xyz.xenondevs.invui.inventory.Inventory;
import xyz.xenondevs.invui.inventory.VirtualInventory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

final class ScrollInventoryGuiImpl extends AbstractScrollGui<Inventory> {
    
    private final BiConsumer<Integer, Integer> resizeHandler = (from, to) -> bake();
    
    public ScrollInventoryGuiImpl(int width, int height, @Nullable List<Inventory> inventories, int... contentListSlots) {
        super(width, height, false, contentListSlots);
        setContent(inventories);
    }
    
    public ScrollInventoryGuiImpl(@Nullable List<Inventory> inventories, Structure structure) {
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
        List<SlotElement> elements = new ArrayList<>();
        for (Inventory inventory : content) {
            for (int i = 0; i < inventory.getSize(); i++) {
                elements.add(new SlotElement.InventorySlotElement(inventory, i));
            }
        }
        
        this.elements = elements;
        update();
    }
    
    public static final class Builder extends AbstractBuilder<Inventory> {
        
        @Override
        public ScrollGui<Inventory> build() {
            if (structure == null)
                throw new IllegalStateException("Structure is not defined.");
            
            var gui = new ScrollInventoryGuiImpl(content, structure);
            applyModifiers(gui);
            return gui;
        }
        
    }
    
}
