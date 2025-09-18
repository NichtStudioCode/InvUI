package xyz.xenondevs.invui.gui;

/**
 * An ingredient in a gui structure.
 */
sealed interface Ingredient {
    
    /**
     * An ingredient that is a marker.
     * @param marker The marker
     */
    record Marker(xyz.xenondevs.invui.gui.Marker marker) implements Ingredient {}
    
    /**
     * An ingredient that is a slot element supplier.
     * @param supplier The slot element supplier
     */
    record Element(SlotElementSupplier supplier) implements Ingredient {}
    
}