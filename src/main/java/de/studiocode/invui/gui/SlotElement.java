package de.studiocode.invui.gui;

import de.studiocode.invui.item.Item;
import de.studiocode.invui.virtualinventory.VirtualInventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public interface SlotElement {
    
    ItemStackHolder getItemStackHolder();
    
    interface ItemStackHolder {
        
        ItemStack getItemStack(UUID viewerUUID);
        
    }
    
    /**
     * Contains an Item
     */
    class ItemSlotElement implements SlotElement, ItemStackHolder {
        
        private final Item item;
        
        public ItemSlotElement(Item item) {
            this.item = item;
        }
        
        public Item getItem() {
            return item;
        }
        
        @Override
        public ItemStack getItemStack(UUID viewerUUID) {
            return item.getItemBuilder().buildFor(viewerUUID);
        }
        
        @Override
        public ItemStackHolder getItemStackHolder() {
            return this;
        }
        
    }
    
    /**
     * Links to a slot in a virtual inventory
     */
    class VISlotElement implements SlotElement, ItemStackHolder {
        
        private final VirtualInventory virtualInventory;
        private final int index;
        
        public VISlotElement(VirtualInventory virtualInventory, int index) {
            this.virtualInventory = virtualInventory;
            this.index = index;
        }
        
        public VirtualInventory getVirtualInventory() {
            return virtualInventory;
        }
        
        public int getIndex() {
            return index;
        }
        
        public ItemStack getItemStack() {
            return virtualInventory.getItemStack(index);
        }
        
        @Override
        public ItemStack getItemStack(UUID viewerUUID) {
            return getItemStack();
        }
        
        @Override
        public ItemStackHolder getItemStackHolder() {
            return this;
        }
        
    }
    
    /**
     * Links to a slot in another GUI
     */
    class LinkedSlotElement implements SlotElement {
        
        private final GUI gui;
        
        private final int slot;
        
        public LinkedSlotElement(GUI gui, int slot) {
            this.gui = gui;
            this.slot = slot;
        }
        
        public GUI getGui() {
            return gui;
        }
        
        public int getSlotIndex() {
            return slot;
        }
        
        public ItemStackHolder getItemStackHolder() {
            LinkedSlotElement element = this;
            while (true) {
                SlotElement below = element.getGui().getSlotElement(element.getSlotIndex());
                if (below instanceof LinkedSlotElement) element = (LinkedSlotElement) below;
                else return (ItemStackHolder) below;
            }
        }
        
    }
    
}
