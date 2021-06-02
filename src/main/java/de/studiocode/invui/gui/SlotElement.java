package de.studiocode.invui.gui;

import de.studiocode.invui.item.Item;
import de.studiocode.invui.virtualinventory.VirtualInventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public interface SlotElement {
    
    ItemStack getItemStack(UUID viewerUUID);
    
    SlotElement getHoldingElement();
    
    /**
     * Contains an Item
     */
    class ItemSlotElement implements SlotElement {
        
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
        public SlotElement getHoldingElement() {
            return this;
        }
        
    }
    
    /**
     * Links to a slot in a virtual inventory
     */
    class VISlotElement implements SlotElement {
        
        private final VirtualInventory virtualInventory;
        private final int slot;
        
        public VISlotElement(VirtualInventory virtualInventory, int slot) {
            this.virtualInventory = virtualInventory;
            this.slot = slot;
        }
        
        public VirtualInventory getVirtualInventory() {
            return virtualInventory;
        }
        
        public int getSlot() {
            return slot;
        }
        
        public ItemStack getItemStack() {
            return virtualInventory.getUnsafeItemStack(slot);
        }
        
        @Override
        public ItemStack getItemStack(UUID viewerUUID) {
            return getItemStack();
        }
        
        @Override
        public SlotElement getHoldingElement() {
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
        
        @Override
        public SlotElement getHoldingElement() {
            LinkedSlotElement element = this;
            while (true) {
                SlotElement below = element.getGui().getSlotElement(element.getSlotIndex());
                if (below instanceof LinkedSlotElement) element = (LinkedSlotElement) below;
                else return below;
            }
        }
        
        @Override
        public ItemStack getItemStack(UUID viewerUUID) {
            return getHoldingElement().getItemStack(viewerUUID);
        }
        
    }
    
}
