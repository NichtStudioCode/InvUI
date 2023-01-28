package de.studiocode.invui.window;

import de.studiocode.inventoryaccess.component.ComponentWrapper;
import de.studiocode.invui.gui.AbstractGUI;
import de.studiocode.invui.gui.GUI;
import de.studiocode.invui.gui.SlotElement;
import de.studiocode.invui.util.InventoryUtils;
import de.studiocode.invui.util.Pair;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * A {@link Window} that just uses the top {@link Inventory}.
 */
public abstract class AbstractSingleWindow extends AbstractWindow {
    
    private final AbstractGUI gui;
    private final int size;
    protected Inventory inventory;
    
    public AbstractSingleWindow(UUID viewerUUID, ComponentWrapper title, AbstractGUI gui, Inventory inventory, boolean initItems, boolean closeable, boolean retain) {
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
    public void handleSlotElementUpdate(GUI child, int slotIndex) {
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
    protected Pair<AbstractGUI, Integer> getGUIAt(int index) {
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
    public AbstractGUI[] getGUIs() {
        return new AbstractGUI[] {gui};
    }
    
    public AbstractGUI getGUI() {
        return gui;
    }
    
}
