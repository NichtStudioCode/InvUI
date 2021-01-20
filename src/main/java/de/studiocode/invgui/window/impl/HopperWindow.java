package de.studiocode.invgui.window.impl;

import de.studiocode.invgui.gui.GUI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import java.util.UUID;

public class HopperWindow extends BaseWindow {
    
    public HopperWindow(UUID viewerUUID, GUI gui, String title, boolean closeable, boolean closeOnEvent) {
        super(viewerUUID, gui, createInventory(gui, title), closeable, closeOnEvent);
    }
    
    public HopperWindow(Player player, GUI gui, String title, boolean closeable, boolean closeOnEvent) {
        this(player.getUniqueId(), gui, title, closeable, closeOnEvent);
    }
    
    private static Inventory createInventory(GUI gui, String title) {
        if (gui.getWidth() != 5 || gui.getHeight() != 1)
            throw new IllegalArgumentException("GUI width has to be 5, height 1.");
        return Bukkit.createInventory(null, InventoryType.HOPPER, title);
    }
    
}
