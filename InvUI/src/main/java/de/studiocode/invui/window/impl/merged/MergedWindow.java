package de.studiocode.invui.window.impl.merged;

import de.studiocode.inventoryaccess.component.ComponentWrapper;
import de.studiocode.inventoryaccess.version.InventoryAccess;
import de.studiocode.invui.gui.GUI;
import de.studiocode.invui.gui.SlotElement;
import de.studiocode.invui.util.Pair;
import de.studiocode.invui.util.SlotUtils;
import de.studiocode.invui.window.Window;
import de.studiocode.invui.window.impl.BaseWindow;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * A {@link Window} that uses both top and player {@link Inventory}.
 */
public abstract class MergedWindow extends BaseWindow {
    
    private final Inventory playerInventory;
    private final ItemStack[] playerItems = new ItemStack[36];
    protected Inventory upperInventory;
    private boolean isCurrentlyOpened;
    
    public MergedWindow(Player player, ComponentWrapper title, int size, Inventory upperInventory, boolean closeable, boolean removeOnClose) {
        super(player.getUniqueId(), title, size, closeable, removeOnClose);
        this.upperInventory = upperInventory;
        this.playerInventory = player.getInventory();
    }
    
    protected void initUpperItems() {
        for (int i = 0; i < upperInventory.getSize(); i++) {
            SlotElement element = getSlotElement(i);
            redrawItem(i, element, true);
        }
    }
    
    private void initPlayerItems() {
        for (int i = upperInventory.getSize(); i < upperInventory.getSize() + 36; i++) {
            SlotElement element = getSlotElement(i);
            redrawItem(i, element, true);
        }
    }
    
    private void clearPlayerInventory() {
        Inventory inventory = getViewer().getInventory();
        for (int i = 0; i < 36; i++) {
            playerItems[i] = inventory.getItem(i);
            inventory.setItem(i, null);
        }
    }
    
    private void restorePlayerInventory() {
        Inventory inventory = getViewer().getInventory();
        for (int i = 0; i < 36; i++) {
            inventory.setItem(i, playerItems[i]);
        }
    }
    
    @Override
    protected void redrawItem(int index, SlotElement element, boolean setItem) {
        super.redrawItem(index, element, setItem);
        if (getViewer() != null)
            getViewer().updateInventory(); // fixes a bug where some items wouldn't be displayed correctly
    }
    
    @Override
    protected void setInvItem(int slot, ItemStack itemStack) {
        if (slot >= upperInventory.getSize()) {
            if (isCurrentlyOpened) {
                int invSlot = SlotUtils.translateGuiToPlayerInv(slot - upperInventory.getSize());
                setPlayerInvItem(invSlot, itemStack);
            }
        } else setUpperInvItem(slot, itemStack);
    }
    
    protected void setUpperInvItem(int slot, ItemStack itemStack) {
        upperInventory.setItem(slot, itemStack);
    }
    
    protected void setPlayerInvItem(int slot, ItemStack itemStack) {
        playerInventory.setItem(slot, itemStack);
    }
    
    @Override
    public void handleViewerDeath(PlayerDeathEvent event) {
        if (isCurrentlyOpened) {
            List<ItemStack> drops = event.getDrops();
            if (!event.getKeepInventory()) {
                drops.clear();
                Arrays.stream(playerItems)
                    .filter(Objects::nonNull)
                    .forEach(drops::add);
            }
        }
    }
    
    @Override
    protected void handleOpened() {
        // Prevent players from receiving advancements from UI items
        InventoryAccess.getPlayerUtils().stopAdvancementListening(getViewer());
        
        isCurrentlyOpened = true;
        clearPlayerInventory();
        initPlayerItems();
    }
    
    @Override
    protected void handleClosed() {
        isCurrentlyOpened = false;
        restorePlayerInventory();
        
        // Start the advancement listeners again
        InventoryAccess.getPlayerUtils().startAdvancementListening(getViewer());
    }
    
    @Override
    public void handleClick(InventoryClickEvent event) {
        Pair<GUI, Integer> clicked = getWhereClicked(event);
        clicked.getFirst().handleClick(clicked.getSecond(), (Player) event.getWhoClicked(), event.getClick(), event);
    }
    
    @Override
    public void handleItemShift(InventoryClickEvent event) {
        // empty, should not be called by the WindowManager
    }
    
    @Override
    public void handleCursorCollect(InventoryClickEvent event) {
        // empty, should not be called by the WindowManager
    }
    
    @Override
    public Inventory[] getInventories() {
        return isCurrentlyOpened ? new Inventory[] {upperInventory, playerInventory} : new Inventory[] {upperInventory};
    }
    
    public Inventory getUpperInventory() {
        return upperInventory;
    }
    
    public Inventory getPlayerInventory() {
        return playerInventory;
    }
    
    protected abstract SlotElement getSlotElement(int index);
    
    protected abstract Pair<GUI, Integer> getWhereClicked(InventoryClickEvent event);
    
}
