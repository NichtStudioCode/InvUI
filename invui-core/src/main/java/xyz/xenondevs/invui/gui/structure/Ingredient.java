package xyz.xenondevs.invui.gui.structure;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.gui.SlotElement;

import java.util.function.Supplier;

class Ingredient {
    
    private final SlotElement slotElement;
    private final Marker marker;
    private final Supplier<? extends SlotElement> elementSupplier;
    
    /**
     * Creates a new {@link Ingredient} of the specified {@link SlotElement}.
     *
     * @param slotElement The {@link SlotElement}.
     */
    public Ingredient(@NotNull SlotElement slotElement) {
        this.slotElement = slotElement;
        this.elementSupplier = null;
        this.marker = null;
    }
    
    /**
     * Creates a new {@link Ingredient} of the specified {@link SlotElement} {@link Supplier}.
     * The {@link Supplier} will be invoked for each slot that the {@link Ingredient} is placed on.
     *
     * @param elementSupplier The {@link Supplier}.
     */
    public Ingredient(@NotNull Supplier<? extends SlotElement> elementSupplier) {
        this.elementSupplier = elementSupplier;
        this.slotElement = null;
        this.marker = null;
    }
    
    /**
     * Creates a new {@link Ingredient} of the specified {@link Marker}.
     *
     * @param marker The {@link Marker}.
     */
    public Ingredient(@NotNull Marker marker) {
        this.marker = marker;
        this.slotElement = null;
        this.elementSupplier = null;
    }
    
    /**
     * Gets the {@link SlotElement} of this {@link Ingredient}.
     *
     * @return The {@link SlotElement} or null if this {@link Ingredient} is a {@link Marker}.
     */
    public @Nullable SlotElement getSlotElement() {
        return slotElement == null ? elementSupplier.get() : slotElement;
    }
    
    /**
     * Gets the {@link Marker} of this {@link Ingredient}.
     *
     * @return The {@link Marker} or null if this {@link Ingredient} is a {@link SlotElement} or a {@link Supplier}
     * for {@link SlotElement SlotElements}.
     */
    public @Nullable Marker getMarker() {
        return marker;
    }
    
    /**
     * Checks if this {@link Ingredient} is a {@link SlotElement} or a {@link Supplier} for {@link SlotElement SlotElements}.
     *
     * @return Whether this {@link Ingredient} is a {@link SlotElement}.
     */
    public boolean isSlotElement() {
        return slotElement != null || elementSupplier != null;
    }
    
    /**
     * Checks if this {@link Ingredient} is a {@link Marker}.
     *
     * @return Whether this {@link Ingredient} is a {@link Marker}.
     */
    public boolean isMarker() {
        return marker != null;
    }
    
}