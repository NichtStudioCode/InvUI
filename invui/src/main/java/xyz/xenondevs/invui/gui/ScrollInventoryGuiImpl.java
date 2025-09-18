package xyz.xenondevs.invui.gui;

import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.inventory.Inventory;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.state.MutableProperty;

import java.util.List;

final class ScrollInventoryGuiImpl<C extends Inventory> extends AbstractScrollGui<C> {
    
    public ScrollInventoryGuiImpl(
        int width, int height,
        List<? extends C> inventories,
        List<? extends Slot> contentListSlots,
        LineOrientation direction
    ) {
        super(width, height, contentListSlots, direction, MutableProperty.of(inventories));
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
    protected void updateContent() {
        switch(getLineOrientation()) {
            case HORIZONTAL -> updateContentHorizontal();
            case VERTICAL -> updateContentVertical();
        }
    }
    
    private void updateContentHorizontal() {
        int topLine = getLine();
        List<Slot> cls = getContentListSlots();
        List<C> content = getContent();
        
        slot:
        for (Slot slot : cls) {
            int line = slot.y() - min.y() + topLine;
            int offset = slot.x() - min.x();
            int i = line * lineLength + offset;
            
            for (Inventory inv : content) {
                if (inv.getSize() > i) {
                    setSlotElement(slot, new SlotElement.InventoryLink(inv, i));
                    continue slot;
                }
                i -= inv.getSize();
            }
            
            setSlotElement(slot, null); // no inv for slot
        }
    }
    
    private void updateContentVertical() {
        int topLine = getLine();
        List<Slot> cls = getContentListSlots();
        List<C> content = getContent();
        
        slot:
        for (Slot slot : cls) {
            int line = slot.x() - min.x() + topLine;
            int offset = slot.y() - min.y();
            int i = line * lineLength + offset;
            
            for (Inventory inv : content) {
                if (inv.getSize() > i) {
                    setSlotElement(slot, new SlotElement.InventoryLink(inv, i));
                    continue slot;
                }
                i -= inv.getSize();
            }
            
            setSlotElement(slot, null); // no inv for slot
        }
    }
    
    @Override
    public int getLineCount() {
        if (lineLength <= 0)
            return 0;
        
        int slots = getContent().stream().mapToInt(Inventory::getSize).sum();
        return Math.ceilDiv(slots, lineLength);
    }
    
    public static final class Builder<C extends Inventory> extends AbstractBuilder<C> {
        
        public Builder() {
            super(ScrollInventoryGuiImpl::new);
        }
        
    }
    
}
