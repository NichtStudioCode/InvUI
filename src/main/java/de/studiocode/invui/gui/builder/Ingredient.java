package de.studiocode.invui.gui.builder;

import de.studiocode.invui.gui.GUI;
import de.studiocode.invui.gui.SlotElement;
import de.studiocode.invui.item.itembuilder.ItemBuilder;

import java.util.function.Function;

class Ingredient {
    
    private final SlotElement slotElement;
    private final Marker marker;
    private final Function<GUI, ItemBuilder> builderFunction;
    
    public Ingredient(SlotElement slotElement) {
        this.slotElement = slotElement;
        this.builderFunction = null;
        this.marker = null;
    }
    
    public Ingredient(Marker marker) {
        this.marker = marker;
        this.slotElement = null;
        this.builderFunction = null;
    }
    
    public Ingredient(Function<GUI, ItemBuilder> builderFunction) {
        this.builderFunction = builderFunction;
        this.slotElement = null;
        this.marker = null;
    }
    
    public SlotElement getSlotElement() {
        return slotElement;
    }
    
    public Marker getMarker() {
        return marker;
    }
    
    public Function<GUI, ItemBuilder> getBuilderFunction() {
        return builderFunction;
    }
    
    public boolean isSlotElement() {
        return slotElement != null;
    }
    
    public boolean isMarker() {
        return marker != null;
    }
    
    public boolean isBuilderFunction() {
        return builderFunction != null;
    }
    
}