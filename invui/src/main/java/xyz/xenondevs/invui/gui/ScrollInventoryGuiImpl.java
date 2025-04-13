package xyz.xenondevs.invui.gui;

import xyz.xenondevs.invui.inventory.Inventory;
import xyz.xenondevs.invui.inventory.VirtualInventory;
import xyz.xenondevs.invui.state.MutableProperty;
import xyz.xenondevs.invui.state.Property;

import java.util.*;
import java.util.function.BiConsumer;

final class ScrollInventoryGuiImpl<C extends Inventory> extends AbstractScrollGui<C> {
    
    private final BiConsumer<Integer, Integer> resizeHandler = (from, to) -> bake();
    private final Set<VirtualInventory> lastKnownInventories = new HashSet<>();
    
    public ScrollInventoryGuiImpl(
        int width, int height,
        List<? extends C> inventories,
        SequencedSet<Slot> contentListSlots,
        boolean horizontalLines
    ) {
        super(width, height, contentListSlots, horizontalLines, Property.of(inventories));
        bake();
    }
    
    public ScrollInventoryGuiImpl(
        Structure structure,
        MutableProperty<Integer> line,
        Property<? extends List<? extends C>> inventories
    ) {
        super(structure, line, inventories);
        bake();
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
