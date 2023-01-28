package xyz.xenondevs.invui.window.impl;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.xenondevs.inventoryaccess.InventoryAccess;
import xyz.xenondevs.inventoryaccess.abstraction.inventory.AnvilInventory;
import xyz.xenondevs.inventoryaccess.component.ComponentWrapper;
import xyz.xenondevs.invui.gui.AbstractGui;
import xyz.xenondevs.invui.window.AbstractSplitWindow;
import xyz.xenondevs.invui.window.AnvilWindow;

import java.util.function.Consumer;

public final class AnvilSplitWindowImpl extends AbstractSplitWindow implements AnvilWindow {
    
    private final AnvilInventory anvilInventory;
    
    public AnvilSplitWindowImpl(Player player, ComponentWrapper title, AbstractGui upperGui, AbstractGui lowerGui, Consumer<String> renameHandler, boolean closeable, boolean retain) {
        super(player, title, upperGui, lowerGui, null, false, closeable, retain);
        
        anvilInventory = InventoryAccess.createAnvilInventory(player, title, renameHandler);
        upperInventory = anvilInventory.getBukkitInventory();
        
        initUpperItems();
        register();
    }
    
    @Override
    protected void setUpperInvItem(int slot, ItemStack itemStack) {
        anvilInventory.setItem(slot, itemStack);
    }
    
    @Override
    public void show() {
        if (isRemoved()) throw new IllegalStateException("The Window has already been closed.");
        
        Player viewer = getViewer();
        if (viewer == null) throw new IllegalStateException("The player is not online.");
        anvilInventory.open();
    }
    
    @Override
    public String getRenameText() {
        return anvilInventory.getRenameText();
    }
    
}
