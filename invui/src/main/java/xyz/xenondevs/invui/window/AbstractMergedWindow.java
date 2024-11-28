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
 * <p>
 * Only in very rare circumstances should this class be used directly.
 * Instead, use {@link Window#merged()} to create such a {@link Window}.
 */
public abstract class AbstractMergedWindow extends AbstractDoubleWindow {
    
    private final AbstractGui gui;
    
    /**
     * Creates a new {@link AbstractMergedWindow}.
     * @param player The {@link Player} that views the window.
     * @param title The title of the window.
     * @param gui The {@link Gui} of the window.
     * @param upperInventory The {@link Inventory} of the window.
     * @param closeable Whether the window is closeable.
     */
    public AbstractMergedWindow(Player player, ComponentWrapper title, AbstractGui gui, Inventory upperInventory, boolean closeable) {
        super(player, title, gui.getSize(), upperInventory, closeable);
        this.gui = gui;
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
