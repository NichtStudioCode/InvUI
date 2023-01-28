package de.studiocode.invui.window.impl;

import de.studiocode.inventoryaccess.InventoryAccess;
import de.studiocode.inventoryaccess.abstraction.inventory.AnvilInventory;
import de.studiocode.inventoryaccess.component.ComponentWrapper;
import de.studiocode.invui.gui.AbstractGUI;
import de.studiocode.invui.window.AbstractSplitWindow;
import de.studiocode.invui.window.AnvilWindow;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public final class AnvilSplitWindowImpl extends AbstractSplitWindow implements AnvilWindow {
    
    private final AnvilInventory anvilInventory;
    
    public AnvilSplitWindowImpl(Player player, ComponentWrapper title, AbstractGUI upperGui, AbstractGUI lowerGui, Consumer<String> renameHandler, boolean closeable, boolean retain) {
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
