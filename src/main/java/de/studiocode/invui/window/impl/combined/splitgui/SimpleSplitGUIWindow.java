package de.studiocode.invui.window.impl.combined.splitgui;

import de.studiocode.invui.gui.GUI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class SimpleSplitGUIWindow extends SplitGUIWindow {
    
    public SimpleSplitGUIWindow(Player player, String title, GUI upperGui, GUI lowerGui, boolean closeable, boolean closeOnEvent) {
        super(player, upperGui, lowerGui, createInventory(upperGui, title), closeable, closeOnEvent);
    }
    
    private static Inventory createInventory(GUI gui, String title) {
        if (gui.getWidth() != 9)
            throw new IllegalArgumentException("GUI width has to be 9");
        
        return Bukkit.createInventory(null, gui.getSize(), title);
    }
    
}
