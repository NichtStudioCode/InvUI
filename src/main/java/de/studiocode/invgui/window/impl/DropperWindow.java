package de.studiocode.invgui.window.impl;

import de.studiocode.invgui.gui.GUI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import java.util.UUID;

public class DropperWindow extends BaseWindow {
    
    public DropperWindow(UUID viewerUUID, GUI gui, String title, boolean closeable, boolean closeOnEvent) {
        super(viewerUUID, gui, createInventory(gui, title), closeable, closeOnEvent);
    }
    
    public DropperWindow(Player player, GUI gui, String title, boolean closeable, boolean closeOnEvent) {
        this(player.getUniqueId(), gui, title, closeable, closeOnEvent);
    }
    
    private static Inventory createInventory(GUI gui, String title) {
        if (gui.getWidth() != 3 || gui.getHeight() != 3)
            throw new IllegalArgumentException("GUI width and height have to be 3.");
        return Bukkit.createInventory(null, InventoryType.DROPPER, title);
    }
    
}
