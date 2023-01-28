package xyz.xenondevs.invui.window;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import xyz.xenondevs.inventoryaccess.component.ComponentWrapper;
import xyz.xenondevs.invui.gui.AbstractGui;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.SlotElement;
import xyz.xenondevs.invui.util.InventoryUtils;
import xyz.xenondevs.invui.util.Pair;

import java.util.UUID;

/**
 * A {@link Window} that just uses the top {@link Inventory}.
 */
public abstract class AbstractSingleWindow extends AbstractWindow {
    
    private final AbstractGui gui;
    private final int size;
    protected Inventory inventory;
    
    public AbstractSingleWindow(UUID viewerUUID, ComponentWrapper title, AbstractGui gui, Inventory inventory, boolean initItems, boolean closeable, boolean retain) {
        super(viewerUUID, title, gui.getSize(), closeable, retain);
        this.gui = gui;
        this.size = gui.getSize();
        this.inventory = inventory;
        
        gui.addParent(this);
        if (initItems) initItems();
    }
    
    protected void initItems() {
        for (int i = 0; i < size; i++) {
            SlotElement element = gui.getSlotElement(i);
            redrawItem(i, element, true);
        }
    }
    
    @Override
    protected void setInvItem(int slot, ItemStack itemStack) {
        inventory.setItem(slot, itemStack);
    }
    
    @Override
    protected void handleOpened() {
        // empty
    }
    
    @Override
    protected void handleClosed() {
        // empty
    }
    
    @Override
    public void handleSlotElementUpdate(Gui child, int slotIndex) {
        redrawItem(slotIndex, gui.getSlotElement(slotIndex), true);
    }
    
    @Override
    public void handleClick(InventoryClickEvent event) {
        gui.handleClick(event.getSlot(), (Player) event.getWhoClicked(), event.getClick(), event);
    }
    
    @Override
    public void handleItemShift(InventoryClickEvent event) {
        gui.handleItemShift(event);
    }
    
    @Override
    public void handleCursorCollect(InventoryClickEvent event) {
        // TODO: Allow collecting from player inventory and VirtualInventory
        // only cancel when this would affect the window inventory
        if (InventoryUtils.containsSimilar(inventory, event.getCursor())) event.setCancelled(true);
    }
    
    @Override
    protected Pair<AbstractGui, Integer> getGuiAt(int index) {
        return index < gui.getSize() ? new Pair<>(gui, index) : null;
    }
    
    @Override
    protected SlotElement getSlotElement(int index) {
        return gui.getSlotElement(index);
    }
    
    @Override
    public void handleViewerDeath(PlayerDeathEvent event) {
        // empty
    }
    
    @Override
    public Inventory[] getInventories() {
        return new Inventory[] {inventory};
    }
    
    @Override
    public AbstractGui[] getGuis() {
        return new AbstractGui[] {gui};
    }
    
    public AbstractGui getGui() {
        return gui;
    }
    
}
