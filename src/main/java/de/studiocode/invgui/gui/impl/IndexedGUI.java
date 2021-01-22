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
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

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
    
        ItemStack cursor = event.getCursor();
        ItemStack clicked = event.getCurrentItem();
        int clickedAmount = clicked == null ? 0 : clicked.getAmount();
        
        switch (event.getAction()) {
            
            case CLONE_STACK:
            case DROP_ALL_CURSOR:
            case DROP_ONE_CURSOR:
                // empty, this does not affect anything
                break;
            
            case DROP_ONE_SLOT:
            case PICKUP_ONE:
                if (!virtualInventory.removeOne(index))
                    event.setCancelled(true);
                break;
            
            case DROP_ALL_SLOT:
            case PICKUP_ALL:
                if (!virtualInventory.removeItem(index))
                    event.setCancelled(true);
                break;
            
            case PLACE_ALL:
                if (!virtualInventory.place(index, cursor, clickedAmount))
                    event.setCancelled(true);
                break;
            
            case PLACE_ONE:
                if (!virtualInventory.placeOne(index, cursor, clickedAmount))
                    event.setCancelled(true);
                break;
            
            case PICKUP_HALF:
                if (!virtualInventory.removeHalf(index, clickedAmount))
                    event.setCancelled(true);
                break;
            
            default:
                // action not supported
                event.setCancelled(true);
                break;
        }
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
            if (slotElement instanceof ItemSlotElement) {
                return ((ItemSlotElement) slotElement).getItem();
            } else if (slotElement instanceof LinkedSlotElement) {
                SlotElement bottom = ((LinkedSlotElement) slotElement).getBottomSlotElement();
                if (bottom instanceof ItemSlotElement) return ((ItemSlotElement) bottom).getItem();
            }
        }
        
        return null;
    }
    
    @Override
    public ItemStackHolder getItemStackHolder(int index) {
        SlotElement slotElement = slotElements[index];
        if (slotElement instanceof ItemStackHolder) {
            return (ItemStackHolder) slotElement;
        } else if (slotElement instanceof LinkedSlotElement) {
            return ((LinkedSlotElement) slotElement).getItemStackHolder();
        } else return null;
    }
    
    @Override
    public void remove(int index) {
        slotElements[index] = null;
    }
    
    @Override
    public void nest(int offset, @NotNull GUI gui) {
        for (int i = 0; i < gui.getSize(); i++) slotElements[i + offset] = new LinkedSlotElement(gui, i);
    }
    
    @Override
    public int getSize() {
        return size;
    }
    
}
