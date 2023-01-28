package de.studiocode.invui.window.impl;

import de.studiocode.inventoryaccess.component.ComponentWrapper;
import de.studiocode.invui.gui.AbstractGUI;
import de.studiocode.invui.gui.GUI;
import de.studiocode.invui.window.AbstractMergedWindow;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public final class NormalMergedWindowImpl extends AbstractMergedWindow {
    
    public NormalMergedWindowImpl(Player player, ComponentWrapper title, AbstractGUI gui, boolean closeable, boolean retain) {
        super(player, title, gui, createInventory(gui), closeable, retain);
        register();
    }
    
    private static Inventory createInventory(GUI gui) {
        if (gui.getWidth() != 9)
            throw new IllegalArgumentException("GUI width has to be 9");
        if (gui.getHeight() <= 4)
            throw new IllegalArgumentException("GUI height has to be bigger than 4");
        
        return Bukkit.createInventory(null, gui.getSize() - 36);
    }
    
}
