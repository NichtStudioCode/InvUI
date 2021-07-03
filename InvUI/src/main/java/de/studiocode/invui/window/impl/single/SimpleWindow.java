package de.studiocode.invui.window.impl.single;

import de.studiocode.invui.gui.GUI;
import de.studiocode.invui.util.InventoryUtils;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.util.UUID;

public class SimpleWindow extends SingleWindow {
    
    public SimpleWindow(UUID viewerUUID, BaseComponent[] title, GUI gui, boolean closeable, boolean closeOnEvent) {
        super(viewerUUID, title, gui, InventoryUtils.createMatchingInventory(gui, ""), true, closeable, closeOnEvent);
    }
    
    public SimpleWindow(UUID viewerUUID, BaseComponent[] title, GUI gui) {
        this(viewerUUID, title, gui, true, true);
    }
    
    public SimpleWindow(Player player, BaseComponent[] title, GUI gui, boolean closeable, boolean closeOnEvent) {
        this(player.getUniqueId(), title, gui, closeable, closeOnEvent);
    }
    
    public SimpleWindow(Player player, BaseComponent[] title, GUI gui) {
        this(player, title, gui, true, true);
    }
    
    public SimpleWindow(UUID viewerUUID, String title, GUI gui, boolean closeable, boolean closeOnEvent) {
        this(viewerUUID, TextComponent.fromLegacyText(title), gui, closeable, closeOnEvent);
    }
    
    public SimpleWindow(UUID viewerUUID, String title, GUI gui) {
        this(viewerUUID, title, gui, true, true);
    }
    
    public SimpleWindow(Player player, String title, GUI gui, boolean closeable, boolean closeOnEvent) {
        this(player.getUniqueId(), title, gui, closeable, closeOnEvent);
    }
    
    public SimpleWindow(Player player, String title, GUI gui) {
        this(player, title, gui, true, true);
    }
    
}
