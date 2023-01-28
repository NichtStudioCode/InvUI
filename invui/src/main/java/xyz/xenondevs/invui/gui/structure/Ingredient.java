package xyz.xenondevs.invui.gui.structure;

import xyz.xenondevs.invui.gui.SlotElement;

import java.util.function.Supplier;

class Ingredient {
    
    private final SlotElement slotElement;
    private final Marker marker;
    private final Supplier<? extends SlotElement> elementSupplier;
    
    public Ingredient(SlotElement slotElement) {
        this.slotElement = slotElement;
        this.elementSupplier = null;
        this.marker = null;
    }
    
    public Ingredient(Supplier<? extends SlotElement> elementSupplier) {
        this.elementSupplier = elementSupplier;
        this.slotElement = null;
        this.marker = null;
    }
    
    public Ingredient(Marker marker) {
        this.marker = marker;
        this.slotElement = null;
        this.elementSupplier = null;
    }
    
    public SlotElement getSlotElement() {
        return slotElement == null ? elementSupplier.get() : slotElement;
    }
    
    public Marker getMarker() {
        return marker;
    }
    
    public boolean isSlotElement() {
        return slotElement != null || elementSupplier != null;
    }
    
    public boolean isMarker() {
        return marker != null;
    }
    
}