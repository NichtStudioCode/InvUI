package xyz.xenondevs.invui.window;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class WindowInventoryHolder implements InventoryHolder {

    private final Window window;
    private Inventory inventory = null;

    public WindowInventoryHolder(Window window) {
        this.window = window;
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        if (inventory == null) {
            throw new IllegalStateException("Inventory is not set.");
        }

        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public Window getWindow() {
        return window;
    }
}
