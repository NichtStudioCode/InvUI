package de.studiocode.invui.window.impl.single;

import de.studiocode.invui.gui.GUI;
import de.studiocode.invui.gui.SlotElement.ItemStackHolder;
import de.studiocode.invui.window.Window;
import de.studiocode.invui.window.impl.BaseWindow;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * A {@link Window} that just uses the top {@link Inventory}.
 */
public abstract class SingleWindow extends BaseWindow {
    
    private final GUI gui;
    private final int size;
    protected Inventory inventory;
    
    public SingleWindow(UUID viewerUUID, GUI gui, Inventory inventory, boolean initItems, boolean closeable, boolean closeOnEvent) {
        super(viewerUUID, gui.getSize(), closeable, closeOnEvent);
        this.gui = gui;
        this.size = gui.getSize();
        this.inventory = inventory;
        
        gui.addParent(this);
        if (initItems) initItems();
    }
    
    protected void initItems() {
        for (int i = 0; i < size; i++) {
            ItemStackHolder holder = gui.getItemStackHolder(i);
            if (holder != null) redrawItem(i, holder, true);
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
        redrawItem(slotIndex, gui.getItemStackHolder(slotIndex), true);
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
    public void handleViewerDeath(PlayerDeathEvent event) {
        // empty
    }
    
    @Override
    public Inventory[] getInventories() {
        return new Inventory[] {inventory};
    }
    
    @Override
    public GUI[] getGuis() {
        return new GUI[] {gui};
    }
    
}
