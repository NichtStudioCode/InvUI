package xyz.xenondevs.invui.window.impl;

import xyz.xenondevs.inventoryaccess.component.ComponentWrapper;
import xyz.xenondevs.invui.gui.AbstractGui;
import xyz.xenondevs.invui.util.InventoryUtils;
import xyz.xenondevs.invui.window.AbstractSingleWindow;

import java.util.UUID;

public final class NormalSingleWindowImpl extends AbstractSingleWindow {
    
    public NormalSingleWindowImpl(UUID viewerUUID, ComponentWrapper title, AbstractGui gui, boolean closeable, boolean retain) {
        super(viewerUUID, title, gui, InventoryUtils.createMatchingInventory(gui, ""), true, closeable, retain);
        register();
    }
    
}
