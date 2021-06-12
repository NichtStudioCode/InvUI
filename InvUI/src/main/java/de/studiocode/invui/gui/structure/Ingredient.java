package de.studiocode.invui.gui.structure;

import de.studiocode.invui.gui.SlotElement;
import de.studiocode.invui.gui.SlotElement.ItemSlotElement;
import de.studiocode.invui.item.Item;

import java.util.function.Supplier;

class Ingredient {
    
    private final SlotElement slotElement;
    private final Marker marker;
    private final Supplier<Item> itemSupplier;
    
    public Ingredient(SlotElement slotElement) {
        this.slotElement = slotElement;
        this.itemSupplier = null;
        this.marker = null;
    }
    
    public Ingredient(Supplier<Item> itemSupplier) {
        this.itemSupplier = itemSupplier;
        this.slotElement = null;
        this.marker = null;
    }
    
    public Ingredient(Marker marker) {
        this.marker = marker;
        this.slotElement = null;
        this.itemSupplier = null;
    }
    
    public SlotElement getSlotElement() {
        return slotElement == null ? new ItemSlotElement(itemSupplier.get()) : slotElement;
    }
    
    public Marker getMarker() {
        return marker;
    }
    
    public boolean isSlotElement() {
        return slotElement != null || itemSupplier != null;
    }
    
    public boolean isMarker() {
        return marker != null;
    }
    
}