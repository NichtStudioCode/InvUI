package xyz.xenondevs.invui.gui;

import xyz.xenondevs.invui.internal.util.CollectionUtils;

import java.util.List;
import java.util.function.Supplier;

/**
 * Generates {@link SlotElement SlotElements} for a given list of {@link Slot Slots}.
 */
@FunctionalInterface
public interface SlotElementSupplier {
    
    /**
     * Creates a {@link SlotElementSupplier} that generates {@link SlotElement SlotElements} by invoking
     * the given {@link Supplier} for each slot.
     *
     * @param supplier the supplier
     * @return the slot element supplier
     */
    static SlotElementSupplier fromSupplier(Supplier<? extends SlotElement> supplier) {
        return slots -> CollectionUtils.newList(slots.size(), i -> supplier.get());
    }
    
    /**
     * Generates {@link SlotElement SlotElements} for the given list of {@link Slot Slots}.
     *
     * @param slots The slots, in order of left-to-right, top-to-bottom
     * @return The generated slot elements
     */
    List<? extends SlotElement> generateSlotElements(List<? extends Slot> slots);
    
}
