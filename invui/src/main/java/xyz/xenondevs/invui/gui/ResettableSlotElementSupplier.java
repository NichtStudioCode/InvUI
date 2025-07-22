package xyz.xenondevs.invui.gui;

import java.util.function.Supplier;

/**
 * A supplier for {@link SlotElement SlotElements} that needs to be reset before repeated use.
 *
 * @param <T> The type of the {@link SlotElement} supplied by this supplier.
 */
interface ResettableSlotElementSupplier<T extends SlotElement> extends Supplier<T> {
    
    /**
     * Resets the state of this element supplier, making it ready to be used again.
     */
    void reset();
    
}
