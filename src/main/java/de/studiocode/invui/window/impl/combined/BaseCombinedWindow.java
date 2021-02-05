package de.studiocode.invui.window.impl.combined;

import de.studiocode.invui.gui.SlotElement.ItemStackHolder;
import de.studiocode.invui.gui.SlotElement.VISlotElement;
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
public abstract class BaseCombinedWindow extends BaseWindow {
    
    private final Inventory playerInventory;
    private final ItemStack[] playerItems = new ItemStack[36];
    protected Inventory upperInventory;
    private boolean isCurrentlyOpened;
    
    public BaseCombinedWindow(Player player, int size, Inventory upperInventory, boolean closeable, boolean closeOnEvent) {
        super(player.getUniqueId(), size, closeable, closeOnEvent);
        this.upperInventory = upperInventory;
        this.playerInventory = player.getInventory();
    }
    
    protected void initUpperItems() {
        for (int i = 0; i < upperInventory.getSize(); i++) {
            ItemStackHolder holder = getItemStackHolder(i);
            if (holder != null) redrawItem(i, holder, true);
        }
    }
    
    private void initPlayerItems() {
        for (int i = upperInventory.getSize(); i < upperInventory.getSize() + 36; i++) {
            ItemStackHolder holder = getItemStackHolder(i);
            if (holder != null) redrawItem(i, holder, true);
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
    protected void redrawItem(int index, ItemStackHolder holder, boolean setItem) {
        if (holder instanceof VISlotElement)
            throw new IllegalArgumentException("VirtualInventories are not allowed in CombinedWindows");
        
        super.redrawItem(index, holder, setItem);
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
        isCurrentlyOpened = true;
        clearPlayerInventory();
        initPlayerItems();
    }
    
    @Override
    protected void handleClosed() {
        isCurrentlyOpened = false;
        restorePlayerInventory();
    }
    
    @Override
    public void handleItemShift(InventoryClickEvent event) {
        event.setCancelled(true);
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
    
    protected abstract ItemStackHolder getItemStackHolder(int index);
    
}
