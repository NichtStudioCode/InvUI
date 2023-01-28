package de.studiocode.invui.window;

import de.studiocode.inventoryaccess.component.ComponentWrapper;
import de.studiocode.invui.gui.AbstractGui;
import de.studiocode.invui.gui.Gui;
import de.studiocode.invui.gui.SlotElement;
import de.studiocode.invui.util.Pair;
import de.studiocode.invui.util.SlotUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

/**
 * A {@link Window} where top and player {@link Inventory} are affected by different {@link Gui}s.
 */
public abstract class AbstractSplitWindow extends AbstractDoubleWindow {
    
    private final AbstractGui upperGui;
    private final AbstractGui lowerGui;
    
    public AbstractSplitWindow(Player player, ComponentWrapper title, AbstractGui upperGui, AbstractGui lowerGui, Inventory upperInventory, boolean initItems, boolean closeable, boolean retain) {
        super(player, title, upperGui.getSize() + lowerGui.getSize(), upperInventory, closeable, retain);
        this.upperGui = upperGui;
        this.lowerGui = lowerGui;
        
        upperGui.addParent(this);
        lowerGui.addParent(this);
        if (initItems) initUpperItems();
    }
    
    @Override
    public void handleSlotElementUpdate(Gui child, int slotIndex) {
        redrawItem(child == upperGui ? slotIndex : upperGui.getSize() + slotIndex,
            child.getSlotElement(slotIndex), true);
    }
    
    @Override
    public SlotElement getSlotElement(int index) {
        if (index >= upperGui.getSize()) return lowerGui.getSlotElement(index - upperGui.getSize());
        else return upperGui.getSlotElement(index);
    }
    
    @Override
    protected Pair<AbstractGui, Integer> getWhereClicked(InventoryClickEvent event) {
        Inventory clicked = event.getClickedInventory();
        if (clicked == getUpperInventory()) {
            return new Pair<>(upperGui, event.getSlot());
        } else {
            int index = SlotUtils.translatePlayerInvToGui(event.getSlot());
            return new Pair<>(lowerGui, index);
        }
    }
    
    @Override
    protected Pair<AbstractGui, Integer> getGuiAt(int index) {
        if (index < upperGui.getSize()) return new Pair<>(upperGui, index);
        else if (index < (upperGui.getSize() + lowerGui.getSize()))
            return new Pair<>(lowerGui, index - upperGui.getSize());
        else return null;
    }
    
    @Override
    public AbstractGui[] getGuis() {
        return new AbstractGui[] {upperGui, lowerGui};
    }
    
}
