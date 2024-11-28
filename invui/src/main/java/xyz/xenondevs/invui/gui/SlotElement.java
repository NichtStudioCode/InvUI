package xyz.xenondevs.invui.gui;

import org.bukkit.inventory.ItemStack;
import xyz.xenondevs.invui.inventory.Inventory;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.ItemProvider;

import java.util.ArrayList;
import java.util.List;

public interface SlotElement {
    
    ItemStack getItemStack(String lang);
    
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
        public ItemStack getItemStack(String lang) {
            return item.getItemProvider().get(lang);
        }
        
        @Override
        public SlotElement getHoldingElement() {
            return this;
        }
        
    }
    
    /**
     * Links to a slot in a {@link Inventory}
     */
    class InventorySlotElement implements SlotElement {
        
        private final Inventory inventory;
        private final int slot;
        private final ItemProvider background;
        
        public InventorySlotElement(Inventory inventory, int slot) {
            this.inventory = inventory;
            this.slot = slot;
            this.background = null;
        }
        
        public InventorySlotElement(Inventory inventory, int slot, ItemProvider background) {
            this.inventory = inventory;
            this.slot = slot;
            this.background = background;
        }
        
        public Inventory getInventory() {
            return inventory;
        }
        
        public int getSlot() {
            return slot;
        }
        
        public ItemProvider getBackground() {
            return background;
        }
        
        @Override
        public ItemStack getItemStack(String lang) {
            ItemStack itemStack = inventory.getUnsafeItem(slot);
            if (itemStack == null && background != null) itemStack = background.get(lang);
            return itemStack;
        }
        
        @Override
        public SlotElement getHoldingElement() {
            return this;
        }
        
    }
    
    /**
     * Links to a slot in another {@link Gui}
     */
    class LinkedSlotElement implements SlotElement {
        
        private final Gui gui;
        
        private final int slot;
        
        public LinkedSlotElement(Gui gui, int slot) {
            if (!(gui instanceof AbstractGui))
                throw new IllegalArgumentException("Illegal Gui implementation");
            
            this.gui = gui;
            this.slot = slot;
        }
        
        public Gui getGui() {
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
        
        public List<Gui> getGuiList() {
            ArrayList<Gui> guis = new ArrayList<>();
            LinkedSlotElement element = this;
            while (true) {
                guis.add(element.getGui());
                SlotElement below = element.getGui().getSlotElement(element.getSlotIndex());
                if (below instanceof LinkedSlotElement)
                    element = (LinkedSlotElement) below;
                else break;
            }
            
            return guis;
        }
        
        @Override
        public ItemStack getItemStack(String lang) {
            SlotElement holdingElement = getHoldingElement();
            return holdingElement != null ? holdingElement.getItemStack(lang) : null;
        }
        
    }
    
}
