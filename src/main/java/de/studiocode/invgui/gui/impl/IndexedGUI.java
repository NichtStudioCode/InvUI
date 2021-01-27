package de.studiocode.invgui.gui.impl;

import de.studiocode.invgui.gui.GUI;
import de.studiocode.invgui.gui.SlotElement;
import de.studiocode.invgui.gui.SlotElement.ItemSlotElement;
import de.studiocode.invgui.gui.SlotElement.ItemStackHolder;
import de.studiocode.invgui.gui.SlotElement.LinkedSlotElement;
import de.studiocode.invgui.gui.SlotElement.VISlotElement;
import de.studiocode.invgui.item.Item;
import de.studiocode.invgui.util.ArrayUtils;
import de.studiocode.invgui.virtualinventory.VirtualInventory;
import de.studiocode.invgui.virtualinventory.event.ItemUpdateEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

abstract class IndexedGUI implements GUI {
    
    protected final int size;
    protected final SlotElement[] slotElements;
    
    public IndexedGUI(int size) {
        this.size = size;
        slotElements = new SlotElement[size];
    }
    
    @Override
    public void handleClick(int slotNumber, Player player, ClickType clickType, InventoryClickEvent event) {
        SlotElement slotElement = slotElements[slotNumber];
        if (slotElement instanceof LinkedSlotElement) {
            LinkedSlotElement linkedElement = (LinkedSlotElement) slotElement;
            linkedElement.getGui().handleClick(linkedElement.getSlotIndex(), player, clickType, event);
        } else if (slotElement instanceof ItemSlotElement) {
            event.setCancelled(true); // if it is an Item, don't let the player move it
            ItemSlotElement itemElement = (ItemSlotElement) slotElement;
            itemElement.getItem().handleClick(clickType, player, event);
        } else if (slotElement instanceof VISlotElement) {
            handleVISlotElementClick((VISlotElement) slotElement, event);
        } else event.setCancelled(true); // Only VISlotElements have allowed interactions
    }
    
    private void handleVISlotElementClick(VISlotElement element, InventoryClickEvent event) {
        VirtualInventory virtualInventory = element.getVirtualInventory();
        int index = element.getIndex();
        
        Player player = (Player) event.getWhoClicked();
        ItemStack cursor = event.getCursor();
        ItemStack clicked = event.getCurrentItem();
        
        if (virtualInventory.isSynced(index, clicked)) {
            boolean cancelled = false;
            
            switch (event.getAction()) {
                
                case CLONE_STACK:
                case DROP_ALL_CURSOR:
                case DROP_ONE_CURSOR:
                    // empty, this does not affect anything
                    break;
                
                case DROP_ONE_SLOT:
                case PICKUP_ONE:
                    cancelled = virtualInventory.removeOne(player, index);
                    break;
                
                case DROP_ALL_SLOT:
                case PICKUP_ALL:
                    cancelled = virtualInventory.removeItem(player, index);
                    break;
                
                case PICKUP_HALF:
                    cancelled = virtualInventory.removeHalf(player, index);
                    break;
                
                case PLACE_ALL:
                    cancelled = virtualInventory.place(player, index, cursor);
                    break;
                
                case PLACE_ONE:
                    cancelled = virtualInventory.placeOne(player, index, cursor);
                    break;
                
                case PLACE_SOME:
                    cancelled = virtualInventory.setToMaxAmount(player, index);
                    break;
                
                case MOVE_TO_OTHER_INVENTORY:
                    event.setCancelled(true);
                    
                    ItemStack invStack = virtualInventory.getItemStack(index);
                    ItemUpdateEvent updateEvent = new ItemUpdateEvent(virtualInventory, player, invStack,
                        index, invStack.getAmount(), -1);
                    Bukkit.getPluginManager().callEvent(updateEvent);
                    
                    if (!updateEvent.isCancelled()) {
                        int leftOverAmount = 0;
                        HashMap<Integer, ItemStack> leftover =
                            event.getWhoClicked().getInventory().addItem(virtualInventory.getItemStack(index));
                        
                        if (!leftover.isEmpty()) leftOverAmount = leftover.get(0).getAmount();
                        
                        virtualInventory.setAmountSilently(index, leftOverAmount);
                    }
                    
                    break;
                
                default:
                    // action not supported
                    event.setCancelled(true);
                    break;
            }
            
            if (cancelled) event.setCancelled(true);
        } else event.setCancelled(true);
    }
    
    @Override
    public void handleItemShift(InventoryClickEvent event) {
        event.setCancelled(true);
        
        Player player = (Player) event.getWhoClicked();
        ItemStack clicked = event.getCurrentItem();
        
        List<VirtualInventory> virtualInventories = getAllVirtualInventories();
        if (virtualInventories.size() > 0) {
            int amountLeft = clicked.getAmount();
            for (VirtualInventory virtualInventory : virtualInventories) {
                ItemStack toAdd = clicked.clone();
                toAdd.setAmount(amountLeft);
                amountLeft = virtualInventory.addItem(player, toAdd);
                
                if (amountLeft == 0) break;
            }
            
            if (amountLeft != 0) event.getCurrentItem().setAmount(amountLeft);
            else event.getClickedInventory().setItem(event.getSlot(), null);
        }
    }
    
    private List<VirtualInventory> getAllVirtualInventories() {
        List<VirtualInventory> virtualInventories = new ArrayList<>();
        Arrays.stream(slotElements)
            .filter(Objects::nonNull)
            .map(SlotElement::getItemStackHolder)
            .filter(holder -> holder instanceof VISlotElement)
            .map(holder -> ((VISlotElement) holder).getVirtualInventory())
            .forEach(vi -> {
                if (!virtualInventories.contains(vi)) virtualInventories.add(vi);
            });
        
        return virtualInventories;
    }
    
    @Override
    public void setSlotElement(int index, @NotNull SlotElement slotElement) {
        slotElements[index] = slotElement;
    }
    
    @Override
    public SlotElement getSlotElement(int index) {
        return slotElements[index];
    }
    
    @Override
    public boolean hasSlotElement(int index) {
        return slotElements[index] != null;
    }
    
    @Override
    public SlotElement[] getSlotElements() {
        return slotElements.clone();
    }
    
    @Override
    public void setItem(int index, Item item) {
        remove(index);
        if (item != null) slotElements[index] = new ItemSlotElement(item);
    }
    
    @Override
    public void addItems(@NotNull Item... items) {
        for (Item item : items) {
            int emptyIndex = ArrayUtils.findFirstEmptyIndex(items);
            if (emptyIndex == -1) break;
            setItem(emptyIndex, item);
        }
    }
    
    @Override
    public Item getItem(int index) {
        SlotElement slotElement = slotElements[index];
        
        if (slotElement != null) {
            ItemStackHolder holder = slotElement.getItemStackHolder();
            if (holder instanceof ItemSlotElement) return ((ItemSlotElement) holder).getItem();
        }
        
        return null;
    }
    
    @Override
    public ItemStackHolder getItemStackHolder(int index) {
        SlotElement slotElement = slotElements[index];
        return slotElement == null ? null : slotElement.getItemStackHolder();
    }
    
    @Override
    public void remove(int index) {
        slotElements[index] = null;
    }
    
    @Override
    public int getSize() {
        return size;
    }
    
}
