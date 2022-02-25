package de.studiocode.invui.window.impl.single;

import de.studiocode.invui.gui.GUI;
import de.studiocode.invui.gui.SlotElement;
import de.studiocode.invui.util.InventoryUtils;
import de.studiocode.invui.util.Pair;
import de.studiocode.invui.window.Window;
import de.studiocode.invui.window.impl.BaseWindow;
import net.md_5.bungee.api.chat.BaseComponent;
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
    
    public SingleWindow(UUID viewerUUID, BaseComponent[] title, GUI gui, Inventory inventory, boolean initItems, boolean closeable, boolean removeOnClose) {
        super(viewerUUID, title, gui.getSize(), closeable, removeOnClose);
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
    protected Pair<GUI, Integer> getGuiAt(int index) {
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
    public GUI[] getGuis() {
        return new GUI[] {gui};
    }
    
    public GUI getGui() {
        return gui;
    }
}
