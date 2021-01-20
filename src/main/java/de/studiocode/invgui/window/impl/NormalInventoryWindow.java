package de.studiocode.invgui.window.impl;

import de.studiocode.invgui.gui.GUI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.UUID;

public final class NormalInventoryWindow extends BaseWindow {
    
    public NormalInventoryWindow(UUID viewerUUID, GUI gui, String title, boolean closeable, boolean closeOnEvent) {
        super(viewerUUID, gui, createInventory(gui, title), closeable, closeOnEvent);
    }
    
    public NormalInventoryWindow(Player player, GUI gui, String title, boolean closeable, boolean closeOnEvent) {
        this(player.getUniqueId(), gui, title, closeable, closeOnEvent);
    }
    
    private static Inventory createInventory(GUI gui, String title) {
        if (gui.getWidth() != 9) throw new IllegalArgumentException("GUI width has to be 9.");
        return Bukkit.createInventory(null, gui.getSize(), title);
    }
    
}
