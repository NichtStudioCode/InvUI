package de.studiocode.invgui.window.impl;

import de.studiocode.invgui.InvGui;
import de.studiocode.invgui.animation.Animation;
import de.studiocode.invgui.gui.GUI;
import de.studiocode.invgui.gui.SlotElement.ItemSlotElement;
import de.studiocode.invgui.gui.SlotElement.ItemStackHolder;
import de.studiocode.invgui.gui.SlotElement.VISlotElement;
import de.studiocode.invgui.item.Item;
import de.studiocode.invgui.util.ArrayUtils;
import de.studiocode.invgui.virtualinventory.VirtualInventory;
import de.studiocode.invgui.window.Window;
import de.studiocode.invgui.window.WindowManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.function.Predicate;

public abstract class BaseWindow implements Window {
    
    private final GUI gui;
    private final int size;
    private final UUID viewerUUID;
    private final Inventory inventory;
    private final boolean closeOnEvent;
    private final ItemStackHolder[] itemsDisplayed;
    
    private Animation animation;
    private boolean closeable;
    private boolean closed;
    
    public BaseWindow(UUID viewerUUID, GUI gui, Inventory inventory, boolean closeable, boolean closeOnEvent) {
        this.gui = gui;
        this.size = gui.getSize();
        this.viewerUUID = viewerUUID;
        this.inventory = inventory;
        this.closeable = closeable;
        this.closeOnEvent = closeOnEvent;
        this.itemsDisplayed = new ItemStackHolder[size];
        
        initItems();
        WindowManager.getInstance().addWindow(this);
    }
    
    private void initItems() {
        for (int i = 0; i < size; i++) {
            ItemStackHolder holder = gui.getItemStackHolder(i);
            if (holder != null) redrawItem(i, holder, true);
        }
    }
    
    private void redrawItem(int index, ItemStackHolder holder, boolean setItem) {
        // put ItemStack in inventory
        ItemStack itemStack = holder == null ? null : holder.getItemStack(viewerUUID);
        inventory.setItem(index, itemStack);
        
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
    public void handleTick() {
        for (int i = 0; i < size; i++) {
            ItemStackHolder holder = gui.getItemStackHolder(i);
            if (itemsDisplayed[i] != holder) redrawItem(i, holder, true);
        }
    }
    
    @Override
    public void handleClick(InventoryClickEvent event) {
        if (animation == null) { // if not in animation, let the gui handle the click
            gui.handleClick(event.getSlot(), (Player) event.getWhoClicked(), event.getClick(), event);
        } else event.setCancelled(true);
    }
    
    @Override
    public void handleItemShift(InventoryClickEvent event) {
        if (animation == null) { // if not in animation, let the gui handle the item shift
            gui.handleItemShift(event);
        } else event.setCancelled(true);
    }
    
    @Override
    public void handleOpen(InventoryOpenEvent event) {
        if (!event.getPlayer().equals(getViewer()))
            event.setCancelled(true);
    }
    
    @Override
    public void handleClose(Player player) {
        if (closeable) {
            stopAnimation();
            if (closeOnEvent) close(false);
        } else {
            if (player.equals(getViewer()))
                Bukkit.getScheduler().runTaskLater(InvGui.getInstance().getPlugin(),
                    () -> player.openInventory(inventory), 0);
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
    
    private Map<Integer, ItemStackHolder> getItemSlotElements(Item item) {
        return ArrayUtils.findAllOccurrences(itemsDisplayed, holder -> holder instanceof ItemSlotElement
            && ((ItemSlotElement) holder).getItem() == item);
    }
    
    private Map<Integer, ItemStackHolder> getVISlotElements(VirtualInventory virtualInventory) {
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
        
        if (closeForViewer) closeForViewer();
    }
    
    @Override
    public void closeForViewer() {
        closeable = true;
        new ArrayList<>(inventory.getViewers()).forEach(HumanEntity::closeInventory); // clone list to prevent ConcurrentModificationException
    }
    
    @Override
    public void show() {
        if (closed) throw new IllegalStateException("The Window has already been closed.");
        if (inventory.getViewers().size() != 0) throw new IllegalStateException("A Window can only have one viewer.");
        
        Player viewer = getViewer();
        if (viewer == null) throw new IllegalStateException("The player is not online.");
        viewer.openInventory(inventory);
    }
    
    @Override
    public void playAnimation(Animation animation, Predicate<ItemStackHolder> filter) {
        if (getViewer() != null) {
            this.animation = animation;
            
            List<Integer> slots = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                ItemStackHolder holder = itemsDisplayed[i];
                if (holder != null && filter.test(holder)) {
                    slots.add(i);
                    inventory.setItem(i, null);
                }
            }
            
            animation.setBounds(getGui().getWidth(), getGui().getHeight());
            animation.setPlayer(getViewer());
            animation.addShowHandler((frame, index) -> redrawItem(index, itemsDisplayed[index], false));
            animation.addFinishHandler(() -> this.animation = null);
            animation.setSlots(slots);
            
            animation.start();
        }
    }
    
    private void stopAnimation() {
        if (this.animation != null) {
            // cancel the scheduler task and set animation to null
            animation.cancel();
            animation = null;
            
            // show all items again
            for (int i = 0; i < gui.getSize(); i++)
                redrawItem(i, itemsDisplayed[i], false);
        }
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
    public Inventory getInventory() {
        return inventory;
    }
    
    @Override
    public GUI getGui() {
        return gui;
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
    
}