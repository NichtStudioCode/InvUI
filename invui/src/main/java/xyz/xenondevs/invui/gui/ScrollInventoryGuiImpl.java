package xyz.xenondevs.invui.gui;

import xyz.xenondevs.invui.inventory.Inventory;
import xyz.xenondevs.invui.inventory.VirtualInventory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

final class ScrollInventoryGuiImpl<C extends Inventory> extends AbstractScrollGui<C> {
    
    private final BiConsumer<Integer, Integer> resizeHandler = (from, to) -> bake();
    private final Set<VirtualInventory> lastKnownInventories = new HashSet<>();
    
    public ScrollInventoryGuiImpl(int width, int height, List<? extends C> inventories, int... contentListSlots) {
        super(width, height, false, contentListSlots);
        setContent(inventories);
    }
    
    public ScrollInventoryGuiImpl(Supplier<? extends List<? extends C>> inventories, Structure structure) {
        super(structure.getWidth(), structure.getHeight(), false, structure);
        setContentSupplier(inventories);
    }
    
    @Override
    public void bake() {
        List<? extends Inventory> inventories = getContent();
        List<SlotElement> elements = new ArrayList<>();
        for (Inventory inventory : inventories) {
            for (int i = 0; i < inventory.getSize(); i++) {
                elements.add(new SlotElement.InventoryLink(inventory, i));
            }
        }
        
        applyResizeHandlers(inventories);
        setElements(elements);
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
            super(ScrollInventoryGuiImpl::new);
        }
        
    }
    
}
