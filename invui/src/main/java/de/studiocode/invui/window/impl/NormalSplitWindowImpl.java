package de.studiocode.invui.window.impl;

import de.studiocode.inventoryaccess.component.ComponentWrapper;
import de.studiocode.invui.gui.AbstractGUI;
import de.studiocode.invui.util.InventoryUtils;
import de.studiocode.invui.window.AbstractSplitWindow;
import org.bukkit.entity.Player;

public final class NormalSplitWindowImpl extends AbstractSplitWindow {
    
    public NormalSplitWindowImpl(Player player, ComponentWrapper title, AbstractGUI upperGui, AbstractGUI lowerGui, boolean closeable, boolean retain) {
        super(player, title, upperGui, lowerGui, InventoryUtils.createMatchingInventory(upperGui, ""), true, closeable, retain);
        register();
    }
    
}
