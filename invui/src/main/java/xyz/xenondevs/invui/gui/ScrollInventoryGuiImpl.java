package xyz.xenondevs.invui.gui;

import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.inventory.Inventory;
import xyz.xenondevs.invui.inventory.VirtualInventory;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.state.MutableProperty;

import java.util.*;
import java.util.function.BiConsumer;

final class ScrollInventoryGuiImpl<C extends Inventory> extends AbstractScrollGui<C> {
    
    private final BiConsumer<Integer, Integer> resizeHandler = (from, to) -> bake();
    private final Set<VirtualInventory> lastKnownInventories = new HashSet<>();
    
    public ScrollInventoryGuiImpl(
        int width, int height,
        List<? extends C> inventories,
        SequencedSet<? extends Slot> contentListSlots,
        boolean horizontalLines
    ) {
        super(width, height, contentListSlots, horizontalLines, MutableProperty.of(inventories));
        bake();
    }
    
    public ScrollInventoryGuiImpl(
        Structure structure,
        MutableProperty<Integer> line,
        MutableProperty<List<? extends C>> inventories,
        MutableProperty<Boolean> frozen,
        MutableProperty<Boolean> ignoreObscuredInventorySlots,
        MutableProperty<@Nullable ItemProvider> background
    ) {
        super(structure, line, inventories, frozen, ignoreObscuredInventorySlots, background);
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
        setLine(getLine()); // corrects line and refreshes content
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
