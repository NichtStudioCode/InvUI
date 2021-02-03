package de.studiocode.invui.util;

import de.studiocode.invui.gui.GUI;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

public class InventoryUtils {
    
    public static Inventory createMatchingInventory(GUI gui, String title) {
        InventoryType type;
        
        if (gui.getWidth() == 9) type = null;
        else if (gui.getWidth() == 3 && gui.getHeight() == 3) type = InventoryType.DROPPER;
        else if (gui.getWidth() == 5 && gui.getHeight() == 1) type = InventoryType.HOPPER;
        else throw new UnsupportedOperationException("Invalid bounds of GUI");
        
        if (type == null) return Bukkit.createInventory(null, gui.getSize(), title);
        else return Bukkit.createInventory(null, type, title);
    }
    
}
