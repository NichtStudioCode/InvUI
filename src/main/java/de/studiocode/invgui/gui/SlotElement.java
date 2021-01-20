package de.studiocode.invgui.gui;

import de.studiocode.invgui.item.Item;

public class SlotElement {
    
    private final Item item;
    private final GUI gui;
    private final int slotNumber;
    
    public SlotElement(Item item) {
        this.item = item;
        this.gui = null;
        this.slotNumber = -1;
    }
    
    public SlotElement(GUI gui, int slotNumber) {
        this.gui = gui;
        this.slotNumber = slotNumber;
        this.item = null;
    }
    
    public boolean isItem() {
        return item != null;
    }
    
    public Item getItem() {
        return item;
    }
    
    public boolean isGui() {
        return gui != null;
    }
    
    public GUI getGui() {
        return gui;
    }
    
    public int getSlotNumber() {
        return slotNumber;
    }
    
    public Item getItemFromGui() {
        return gui.getItem(slotNumber);
    }
    
}
