package xyz.xenondevs.invui.inventory;

/**
 * A record encapsulating a slot of an {@link Inventory}.
 */
public record InventorySlot(Inventory inventory, int slot) {
    
    public InventorySlot {
        if (slot < 0 || slot >= inventory.getSize())
            throw new IllegalArgumentException("Slot " + slot + " out of bounds for size " + inventory.getSize());
    }
    
}
