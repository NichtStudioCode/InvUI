package de.studiocode.invui.window.impl.combined.splitgui;

import de.studiocode.invui.gui.GUI;
import de.studiocode.invui.util.InventoryUtils;
import org.bukkit.entity.Player;

public class SimpleSplitGUIWindow extends SplitGUIWindow {
    
    public SimpleSplitGUIWindow(Player player, String title, GUI upperGui, GUI lowerGui, boolean closeable, boolean closeOnEvent) {
        super(player, upperGui, lowerGui, InventoryUtils.createMatchingInventory(upperGui, title), true, closeable, closeOnEvent);
    }
    
}
