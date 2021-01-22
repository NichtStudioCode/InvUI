package de.studiocode.invgui.gui;

import de.studiocode.invgui.item.Item;
import de.studiocode.invgui.virtualinventory.VirtualInventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public interface SlotElement {
    
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
        
        public SlotElement getBottomSlotElement() {
            LinkedSlotElement element = this;
            while (true) {
                SlotElement below = element.getGui().getSlotElement(element.getSlotIndex());
                if (below instanceof LinkedSlotElement) element = (LinkedSlotElement) below;
                else return below;
            }
        }
        
        public ItemStackHolder getItemStackHolder() {
            return (ItemStackHolder) getBottomSlotElement();
        }
        
    }
    
}
