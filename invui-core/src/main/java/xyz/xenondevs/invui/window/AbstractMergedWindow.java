package xyz.xenondevs.invui.window;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import xyz.xenondevs.inventoryaccess.component.ComponentWrapper;
import xyz.xenondevs.invui.gui.AbstractGui;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.SlotElement;
import xyz.xenondevs.invui.inventory.ReferencingInventory;
import xyz.xenondevs.invui.util.Pair;
import xyz.xenondevs.invui.util.SlotUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link Window} where top and player {@link Inventory} are affected by the same {@link Gui}.
 */
public abstract class AbstractMergedWindow extends AbstractDoubleWindow {
    
    private final AbstractGui gui;
    
    public AbstractMergedWindow(Player player, ComponentWrapper title, AbstractGui gui, Inventory upperInventory, boolean closeable) {
        super(player, title, gui.getSize(), upperInventory, closeable);
        this.gui = gui;
        
        gui.addParent(this);
    }
    
    @Override
    public void handleSlotElementUpdate(Gui child, int slotIndex) {
        redrawItem(slotIndex, gui.getSlotElement(slotIndex), true);
    }
    
    @Override
    protected SlotElement getSlotElement(int index) {
        return gui.getSlotElement(index);
    }
    
    @Override
    protected Pair<AbstractGui, Integer> getWhereClicked(InventoryClickEvent event) {
        Inventory clicked = event.getClickedInventory();
        int slot = event.getSlot();
        int clickedIndex = clicked == getUpperInventory() ? slot
            : getUpperInventory().getSize() + SlotUtils.translatePlayerInvToGui(slot);
        return new Pair<>(gui, clickedIndex);
    }
    
    @Override
    protected Pair<AbstractGui, Integer> getGuiAt(int index) {
        return index < gui.getSize() ? new Pair<>(gui, index) : null;
    }
    
    @Override
    public AbstractGui[] getGuis() {
        return new AbstractGui[] {gui};
    }
    
    @Override
    protected List<xyz.xenondevs.invui.inventory.Inventory> getContentInventories() {
        List<xyz.xenondevs.invui.inventory.Inventory> inventories = new ArrayList<>(gui.getAllInventories());
        inventories.add(ReferencingInventory.fromStorageContents(getViewer().getInventory()));
        return inventories;
    }
    
}
