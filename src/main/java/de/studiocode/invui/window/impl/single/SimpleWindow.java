package de.studiocode.invui.window.impl.single;

import de.studiocode.invui.gui.GUI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.UUID;

public final class SimpleWindow extends SingleWindow {
    
    public SimpleWindow(UUID viewerUUID, String title, GUI gui, boolean closeable, boolean closeOnEvent) {
        super(viewerUUID, gui, createInventory(gui, title), closeable, closeOnEvent);
    }
    
    public SimpleWindow(Player player, String title, GUI gui, boolean closeable, boolean closeOnEvent) {
        this(player.getUniqueId(), title, gui, closeable, closeOnEvent);
    }
    
    private static Inventory createInventory(GUI gui, String title) {
        if (gui.getWidth() != 9) throw new IllegalArgumentException("GUI width has to be 9.");
        return Bukkit.createInventory(null, gui.getSize(), title);
    }
    
}
