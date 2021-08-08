package de.studiocode.invui.gui;

import de.studiocode.invui.item.Item;
import de.studiocode.invui.item.ItemProvider;
import de.studiocode.invui.virtualinventory.VirtualInventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public interface SlotElement {
    
    ItemStack getItemStack(UUID viewerUUID);
    
    SlotElement getHoldingElement();
    
    /**
     * Contains an {@link Item}
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
            return item.getItemBuilder().getFor(viewerUUID);
        }
        
        @Override
        public SlotElement getHoldingElement() {
            return this;
        }
        
    }
    
    /**
     * Links to a slot in a {@link VirtualInventory}
     */
    class VISlotElement implements SlotElement {
        
        private final VirtualInventory virtualInventory;
        private final int slot;
        private final ItemProvider background;
        
        public VISlotElement(VirtualInventory virtualInventory, int slot) {
            this.virtualInventory = virtualInventory;
            this.slot = slot;
            this.background = null;
        }
        
        public VISlotElement(VirtualInventory virtualInventory, int slot, ItemProvider background) {
            this.virtualInventory = virtualInventory;
            this.slot = slot;
            this.background = background;
        }
        
        public VirtualInventory getVirtualInventory() {
            return virtualInventory;
        }
        
        public int getSlot() {
            return slot;
        }
        
        public ItemProvider getBackground() {
            return background;
        }
        
        @Override
        public ItemStack getItemStack(UUID viewerUUID) {
            ItemStack itemStack = virtualInventory.getUnsafeItemStack(slot);
            if (itemStack == null && background != null) itemStack = background.getFor(viewerUUID);
            return itemStack;
        }
        
        @Override
        public SlotElement getHoldingElement() {
            return this;
        }
        
    }
    
    /**
     * Links to a slot in another {@link GUI}
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
            SlotElement holdingElement = getHoldingElement();
            return holdingElement != null ? holdingElement.getItemStack(viewerUUID) : null;
        }
        
    }
    
}
