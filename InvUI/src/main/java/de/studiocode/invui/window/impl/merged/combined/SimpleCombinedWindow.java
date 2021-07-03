package de.studiocode.invui.window.impl.merged.combined;

import de.studiocode.invui.gui.GUI;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class SimpleCombinedWindow extends CombinedWindow {
    
    public SimpleCombinedWindow(Player player, BaseComponent[] title, GUI gui, boolean closeable, boolean closeOnEvent) {
        super(player, title, gui, createInventory(gui), closeable, closeOnEvent);
    }
    
    public SimpleCombinedWindow(Player player, String title, GUI gui, boolean closeable, boolean closeOnEvent) {
        this(player, TextComponent.fromLegacyText(title), gui, closeable, closeOnEvent);
    }
    
    public SimpleCombinedWindow(Player player, String title, GUI gui) {
        this(player, title, gui, true, true);
    }
    
    public SimpleCombinedWindow(Player player, BaseComponent[] title, GUI gui) {
        this(player, title, gui, true, true);
    }
    
    private static Inventory createInventory(GUI gui) {
        if (gui.getWidth() != 9)
            throw new IllegalArgumentException("GUI width has to be 9");
        if (gui.getHeight() <= 4)
            throw new IllegalArgumentException("GUI height has to be bigger than 4");
        
        return Bukkit.createInventory(null, gui.getSize() - 36);
    }
    
}
