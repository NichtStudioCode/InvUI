package xyz.xenondevs.invui.gui;

import org.jspecify.annotations.Nullable;

import java.util.function.Supplier;

/**
 * Ingredients are used in {@link Structure} and can either be a {@link SlotElement},
 * a {@link Supplier} for {@link SlotElement SlotElements} or a {@link Marker}.
 */
class Ingredient {
    
    private final @Nullable Marker marker;
    private final @Nullable Supplier<? extends SlotElement> elementSupplier;
    
    /**
     * Creates a new {@link Ingredient} with the given {@link Marker}.
     *
     * @param marker The {@link Marker} of this {@link Ingredient}.
     */
    public Ingredient(Marker marker) {
        this.marker = marker;
        this.elementSupplier = null;
    }
    
    /**
     * Creates a new {@link Ingredient} with the given {@link SlotElement} supplier.
     *
     * @param elementSupplier The {@link Supplier} for the {@link SlotElement}.
     */
    public Ingredient(Supplier<? extends SlotElement> elementSupplier) {
        this.elementSupplier = elementSupplier;
        this.marker = null;
    }
    
    /**
     * Creates a new {@link Ingredient} with the given {@link SlotElement}.
     *
     * @param element The {@link SlotElement} of this {@link Ingredient}.
     */
    public Ingredient(SlotElement element) {
        this.elementSupplier = () -> element;
        this.marker = null;
    }
    
    /**
     * Gets the {@link SlotElement} of this {@link Ingredient}.
     *
     * @return The {@link SlotElement} or null if this {@link Ingredient} is a {@link Marker}
     */
    @Nullable
    SlotElement getSlotElement() {
        return elementSupplier != null ? elementSupplier.get() : null;
    }
    
    /**
     * Gets the {@link Marker} of this {@link Ingredient}.
     *
     * @return The {@link Marker} or null if this {@link Ingredient} is a {@link SlotElement} or a {@link Supplier}
     * for {@link SlotElement SlotElements}.
     */
    @Nullable
    Marker getMarker() {
        return marker;
    }
    
    /**
     * Checks if this {@link Ingredient} is a {@link SlotElement}.
     *
     * @return Whether this {@link Ingredient} is a {@link SlotElement}.
     */
    boolean isSlotElement() {
        return elementSupplier != null;
    }
    
    /**
     * Checks if this {@link Ingredient} is a {@link Marker}.
     *
     * @return Whether this {@link Ingredient} is a {@link Marker}.
     */
    boolean isMarker() {
        return marker != null;
    }
    
}