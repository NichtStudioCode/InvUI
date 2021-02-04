package de.studiocode.invui.window.impl;

import de.studiocode.inventoryaccess.api.version.InventoryAccess;
import de.studiocode.invui.InvUI;
import de.studiocode.invui.gui.SlotElement.ItemSlotElement;
import de.studiocode.invui.gui.SlotElement.ItemStackHolder;
import de.studiocode.invui.gui.SlotElement.VISlotElement;
import de.studiocode.invui.item.Item;
import de.studiocode.invui.util.ArrayUtils;
import de.studiocode.invui.virtualinventory.VirtualInventory;
import de.studiocode.invui.window.Window;
import de.studiocode.invui.window.WindowManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public abstract class BaseWindow implements Window {
    
    private final UUID viewerUUID;
    private final boolean closeOnEvent;
    private final ItemStackHolder[] itemsDisplayed;
    
    private boolean closeable;
    private boolean closed;
    
    public BaseWindow(UUID viewerUUID, int size, boolean closeable, boolean closeOnEvent) {
        this.viewerUUID = viewerUUID;
        this.closeable = closeable;
        this.closeOnEvent = closeOnEvent;
        this.itemsDisplayed = new ItemStackHolder[size];
        
        WindowManager.getInstance().addWindow(this);
    }
    
    protected void redrawItem(int index, ItemStackHolder holder, boolean setItem) {
        // put ItemStack in inventory
        ItemStack itemStack = holder == null ? null : holder.getItemStack(viewerUUID);
        setInvItem(index, itemStack);
        
        if (setItem) {
            // tell the previous item (if there is one) that this is no longer its window
            ItemStackHolder previousHolder = itemsDisplayed[index];
            if (previousHolder instanceof ItemSlotElement) {
                ItemSlotElement element = (ItemSlotElement) previousHolder;
                Item item = element.getItem();
                // check if the Item isn't still present on another index
                if (getItemSlotElements(item).size() == 1) {
                    // only if not, remove Window from list in Item
                    item.removeWindow(this);
                }
            } else if (previousHolder instanceof VISlotElement) {
                VISlotElement element = (VISlotElement) previousHolder;
                VirtualInventory virtualInventory = element.getVirtualInventory();
                // check if the VirtualInventory isn't still present on another index
                if (getVISlotElements(element.getVirtualInventory()).size() == 1) {
                    // only if not, remove Window from list in VirtualInventory
                    virtualInventory.removeWindow(this);
                }
            }
            
            // tell the Item or VirtualInventory that it is being displayed in this Window
            if (holder instanceof ItemSlotElement) {
                ((ItemSlotElement) holder).getItem().addWindow(this);
            } else if (holder instanceof VISlotElement) {
                ((VISlotElement) holder).getVirtualInventory().addWindow(this);
            }
            
            itemsDisplayed[index] = holder;
        }
    }
    
    @Override
    public void handleOpen(InventoryOpenEvent event) {
        if (!event.getPlayer().equals(getViewer()))
            event.setCancelled(true);
        else handleOpened();
    }
    
    @Override
    public void handleClose(Player player) {
        if (closeable) {
            if (closeOnEvent) close(false);
            handleClosed();
        } else {
            if (player.equals(getViewer()))
                Bukkit.getScheduler().runTaskLater(InvUI.getInstance().getPlugin(), this::show, 0);
        }
    }
    
    @Override
    public void handleItemBuilderUpdate(Item item) {
        getItemSlotElements(item).forEach((index, slotElement) ->
            redrawItem(index, slotElement, false));
    }
    
    @Override
    public void handleVirtualInventoryUpdate(VirtualInventory virtualInventory) {
        getVISlotElements(virtualInventory).forEach((index, slotElement) ->
            redrawItem(index, slotElement, false));
    }
    
    protected Map<Integer, ItemStackHolder> getItemSlotElements(Item item) {
        return ArrayUtils.findAllOccurrences(itemsDisplayed, holder -> holder instanceof ItemSlotElement
            && ((ItemSlotElement) holder).getItem() == item);
    }
    
    protected Map<Integer, ItemStackHolder> getVISlotElements(VirtualInventory virtualInventory) {
        return ArrayUtils.findAllOccurrences(itemsDisplayed, holder -> holder instanceof VISlotElement
            && ((VISlotElement) holder).getVirtualInventory() == virtualInventory);
    }
    
    @Override
    public void close(boolean closeForViewer) {
        closed = true;
        
        WindowManager.getInstance().removeWindow(this);
        
        Arrays.stream(itemsDisplayed)
            .filter(Objects::nonNull)
            .filter(holder -> holder instanceof ItemSlotElement)
            .map(holder -> ((ItemSlotElement) holder).getItem())
            .forEach(item -> item.removeWindow(this));
        
        Arrays.stream(getGuis())
            .forEach(gui -> gui.removeParent(this));
        
        if (closeForViewer) closeForViewer();
    }
    
    @Override
    public void closeForViewer() {
        closeable = true;
        // clone list to prevent ConcurrentModificationException
        new ArrayList<>(getInventories()[0].getViewers()).forEach(HumanEntity::closeInventory);
        
        handleClosed();
    }
    
    @Override
    public void show() {
        if (closed) throw new IllegalStateException("The Window has already been closed.");
        
        Player viewer = getViewer();
        if (viewer == null) throw new IllegalStateException("The player is not online.");
        InventoryAccess.getInventoryUtils().openCustomInventory(viewer, getInventories()[0]);
    }
    
    @Override
    public Player getCurrentViewer() {
        List<HumanEntity> viewers = getInventories()[0].getViewers();
        return viewers.isEmpty() ? null : (Player) viewers.get(0);
    }
    
    @Override
    public Player getViewer() {
        return Bukkit.getPlayer(viewerUUID);
    }
    
    @Override
    public UUID getViewerUUID() {
        return viewerUUID;
    }
    
    @Override
    public boolean isCloseable() {
        return closeable;
    }
    
    @Override
    public void setCloseable(boolean closeable) {
        this.closeable = closeable;
    }
    
    @Override
    public boolean isClosed() {
        return closed;
    }
    
    protected abstract void setInvItem(int slot, ItemStack itemStack);
    
    protected abstract void handleOpened();
    
    protected abstract void handleClosed();
    
}