package xyz.xenondevs.invui.window.impl;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.inventoryaccess.component.ComponentWrapper;
import xyz.xenondevs.invui.gui.AbstractGui;
import xyz.xenondevs.invui.util.InventoryUtils;
import xyz.xenondevs.invui.window.AbstractSplitWindow;

public final class NormalSplitWindowImpl extends AbstractSplitWindow {
    
    public NormalSplitWindowImpl(
        @NotNull Player player,
        @Nullable ComponentWrapper title,
        @NotNull AbstractGui upperGui,
        @NotNull AbstractGui lowerGui,
        boolean closeable,
        boolean retain
    ) {
        super(player, title, upperGui, lowerGui, InventoryUtils.createMatchingInventory(upperGui, ""), true, closeable, retain);
        register();
    }
    
}
