package de.studiocode.invui.window.impl.single;

import de.studiocode.inventoryaccess.component.BaseComponentWrapper;
import de.studiocode.inventoryaccess.component.ComponentWrapper;
import de.studiocode.invui.gui.GUI;
import de.studiocode.invui.util.InventoryUtils;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.util.UUID;

public final class SimpleWindow extends SingleWindow {
    
    public SimpleWindow(UUID viewerUUID, ComponentWrapper title, GUI gui, boolean closeable, boolean removeOnClose) {
        super(viewerUUID, title, gui, InventoryUtils.createMatchingInventory(gui, ""), true, closeable, removeOnClose);
        register();
    }
    
    public SimpleWindow(UUID viewerUUID, ComponentWrapper title, GUI gui) {
        this(viewerUUID, title, gui, true, true);
    }
    
    public SimpleWindow(Player player, ComponentWrapper title, GUI gui, boolean closeable, boolean removeOnClose) {
        this(player.getUniqueId(), title, gui, closeable, removeOnClose);
    }
    
    public SimpleWindow(Player player, ComponentWrapper title, GUI gui) {
        this(player, title, gui, true, true);
    }
    
    public SimpleWindow(UUID viewerUUID, BaseComponent[] title, GUI gui, boolean closeable, boolean removeOnClose) {
        this(viewerUUID, new BaseComponentWrapper(title), gui, closeable, removeOnClose);
    }
    
    public SimpleWindow(UUID viewerUUID, BaseComponent[] title, GUI gui) {
        this(viewerUUID, new BaseComponentWrapper(title), gui);
    }
    
    public SimpleWindow(Player player, BaseComponent[] title, GUI gui, boolean closeable, boolean removeOnClose) {
        this(player, new BaseComponentWrapper(title), gui, closeable, removeOnClose);
    }
    
    public SimpleWindow(Player player, BaseComponent[] title, GUI gui) {
        this(player, new BaseComponentWrapper(title), gui);
    }
    
    public SimpleWindow(UUID viewerUUID, String title, GUI gui, boolean closeable, boolean removeOnClose) {
        this(viewerUUID, TextComponent.fromLegacyText(title), gui, closeable, removeOnClose);
    }
    
    public SimpleWindow(UUID viewerUUID, String title, GUI gui) {
        this(viewerUUID, title, gui, true, true);
    }
    
    public SimpleWindow(Player player, String title, GUI gui, boolean closeable, boolean removeOnClose) {
        this(player.getUniqueId(), title, gui, closeable, removeOnClose);
    }
    
    public SimpleWindow(Player player, String title, GUI gui) {
        this(player, title, gui, true, true);
    }
    
}
