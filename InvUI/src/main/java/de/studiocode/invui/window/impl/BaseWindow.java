package de.studiocode.invui.window.impl;

import de.studiocode.inventoryaccess.api.version.InventoryAccess;
import de.studiocode.invui.InvUI;
import de.studiocode.invui.gui.GUI;
import de.studiocode.invui.gui.SlotElement;
import de.studiocode.invui.gui.SlotElement.ItemSlotElement;
import de.studiocode.invui.gui.SlotElement.VISlotElement;
import de.studiocode.invui.item.Item;
import de.studiocode.invui.item.ItemBuilder;
import de.studiocode.invui.util.ArrayUtils;
import de.studiocode.invui.util.Pair;
import de.studiocode.invui.virtualinventory.VirtualInventory;
import de.studiocode.invui.virtualinventory.event.PlayerUpdateReason;
import de.studiocode.invui.virtualinventory.event.UpdateReason;
import de.studiocode.invui.window.Window;
import de.studiocode.invui.window.WindowManager;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public abstract class BaseWindow implements Window {
    
    private final UUID viewerUUID;
    private final boolean closeOnEvent;
    private final SlotElement[] elementsDisplayed;
    
    private BaseComponent[] title;
    
    private boolean closeable;
    private boolean closed;
    
    public BaseWindow(UUID viewerUUID, BaseComponent[] title, int size, boolean closeable, boolean closeOnEvent) {
        this.viewerUUID = viewerUUID;
        this.title = title;
        this.closeable = closeable;
        this.closeOnEvent = closeOnEvent;
        this.elementsDisplayed = new SlotElement[size];
        
        WindowManager.getInstance().addWindow(this);
    }
    
    protected void redrawItem(int index) {
        redrawItem(index, getSlotElement(index), false);
    }
    
    protected void redrawItem(int index, SlotElement element, boolean setItem) {
        // put ItemStack in inventory
        ItemStack itemStack;
        if (element == null || (element instanceof VISlotElement && element.getItemStack(viewerUUID) == null)) {
            ItemBuilder background = getGuiAt(index).getFirst().getBackground();
            itemStack = background == null ? null : background.buildFor(viewerUUID);
        } else itemStack = element.getItemStack(viewerUUID);
        setInvItem(index, itemStack);
        
        if (setItem) {
            // tell the previous item (if there is one) that this is no longer its window
            SlotElement previousElement = elementsDisplayed[index];
            if (previousElement instanceof ItemSlotElement) {
                ItemSlotElement itemSlotElement = (ItemSlotElement) previousElement;
                Item item = itemSlotElement.getItem();
                // check if the Item isn't still present on another index
                if (getItemSlotElements(item).size() == 1) {
                    // only if not, remove Window from list in Item
                    item.removeWindow(this);
                }
            } else if (previousElement instanceof VISlotElement) {
                VISlotElement viSlotElement = (VISlotElement) previousElement;
                VirtualInventory virtualInventory = viSlotElement.getVirtualInventory();
                // check if the VirtualInventory isn't still present on another index
                if (getVISlotElements(viSlotElement.getVirtualInventory()).size() == 1) {
                    // only if not, remove Window from list in VirtualInventory
                    virtualInventory.removeWindow(this);
                }
            }
            
            if (element != null) {
                // tell the Item or VirtualInventory that it is being displayed in this Window
                SlotElement holdingElement = element.getHoldingElement();
                if (holdingElement instanceof ItemSlotElement) {
                    ((ItemSlotElement) holdingElement).getItem().addWindow(this);
                } else if (holdingElement instanceof VISlotElement) {
                    ((VISlotElement) holdingElement).getVirtualInventory().addWindow(this);
                }
                
                elementsDisplayed[index] = holdingElement;
            } else {
                elementsDisplayed[index] = null;
            }
        }
    }
    
    @Override
    public void handleDrag(InventoryDragEvent event) {
        Player player = ((Player) event.getWhoClicked()).getPlayer();
        UpdateReason updateReason = new PlayerUpdateReason(player, event);
        Map<Integer, ItemStack> newItems = event.getNewItems();
        
        int itemsLeft = event.getCursor() == null ? 0 : event.getCursor().getAmount();
        for (int rawSlot : event.getRawSlots()) { // loop over all affected slots
            ItemStack currentStack = event.getView().getItem(rawSlot);
            if (currentStack != null && currentStack.getType() == Material.AIR) currentStack = null;
            
            // get the GUI at that index and ask for permission to drag an Item there
            Pair<GUI, Integer> pair = getGuiAt(rawSlot);
            if (pair != null && !pair.getFirst().handleItemDrag(updateReason, pair.getSecond(), currentStack, newItems.get(rawSlot))) {
                // the drag was cancelled
                int currentAmount = currentStack == null ? 0 : currentStack.getAmount();
                int newAmount = newItems.get(rawSlot).getAmount();
                
                itemsLeft += newAmount - currentAmount;
                
                // Redraw cancelled items after the event so there won't be any Items that aren't actually there
                Bukkit.getScheduler().runTask(InvUI.getInstance().getPlugin(), () -> redrawItem(rawSlot));
            }
        }
        
        // update the amount on the cursor
        ItemStack cursorStack = event.getOldCursor();
        cursorStack.setAmount(itemsLeft);
        event.setCursor(cursorStack);
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
    
    protected Map<Integer, SlotElement> getItemSlotElements(Item item) {
        return ArrayUtils.findAllOccurrences(elementsDisplayed, element -> element instanceof ItemSlotElement
            && ((ItemSlotElement) element).getItem() == item);
    }
    
    protected Map<Integer, SlotElement> getVISlotElements(VirtualInventory virtualInventory) {
        return ArrayUtils.findAllOccurrences(elementsDisplayed, element -> element instanceof VISlotElement
            && ((VISlotElement) element).getVirtualInventory() == virtualInventory);
    }
    
    @Override
    public void close(boolean closeForViewer) {
        closed = true;
        
        WindowManager.getInstance().removeWindow(this);
        
        Arrays.stream(elementsDisplayed)
            .filter(Objects::nonNull)
            .map(SlotElement::getHoldingElement)
            .forEach(slotElement -> {
                if (slotElement instanceof ItemSlotElement) {
                    ((ItemSlotElement) slotElement).getItem().removeWindow(this);
                } else if (slotElement instanceof VISlotElement) {
                    ((VISlotElement) slotElement).getVirtualInventory().removeWindow(this);
                }
            });
        
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
        InventoryAccess.getInventoryUtils().openCustomInventory(viewer, getInventories()[0], title);
    }
    
    @Override
    public void changeTitle(@NotNull BaseComponent[] title) {
        this.title = title;
        Player currentViewer = getCurrentViewer();
        if (currentViewer != null) {
            InventoryAccess.getInventoryUtils().updateOpenInventoryTitle(currentViewer, title);
        }
    }
    
    @Override
    public void changeTitle(@NotNull String title) {
        changeTitle(TextComponent.fromLegacyText(title));
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
    
    protected abstract SlotElement getSlotElement(int index);
    
    protected abstract Pair<GUI, Integer> getGuiAt(int index);
    
    protected abstract void handleOpened();
    
    protected abstract void handleClosed();
    
}