package de.studiocode.invui.window.impl;

import de.studiocode.inventoryaccess.component.ComponentWrapper;
import de.studiocode.invui.gui.AbstractGUI;
import de.studiocode.invui.util.InventoryUtils;
import de.studiocode.invui.window.AbstractSingleWindow;

import java.util.UUID;

public final class NormalSingleWindowImpl extends AbstractSingleWindow {
    
    public NormalSingleWindowImpl(UUID viewerUUID, ComponentWrapper title, AbstractGUI gui, boolean closeable, boolean retain) {
        super(viewerUUID, title, gui, InventoryUtils.createMatchingInventory(gui, ""), true, closeable, retain);
        register();
    }
    
}
