package de.studiocode.invui.window.impl.single;

import de.studiocode.invui.gui.GUI;
import de.studiocode.invui.util.InventoryUtils;
import org.bukkit.entity.Player;

import java.util.UUID;

public class SimpleWindow extends SingleWindow {
    
    public SimpleWindow(UUID viewerUUID, String title, GUI gui, boolean closeable, boolean closeOnEvent) {
        super(viewerUUID, gui, InventoryUtils.createMatchingInventory(gui, title), true, closeable, closeOnEvent);
    }
    
    public SimpleWindow(Player player, String title, GUI gui, boolean closeable, boolean closeOnEvent) {
        this(player.getUniqueId(), title, gui, closeable, closeOnEvent);
    }
    
}
