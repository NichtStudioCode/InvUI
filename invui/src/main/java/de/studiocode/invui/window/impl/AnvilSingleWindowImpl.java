package de.studiocode.invui.window.impl;

import de.studiocode.inventoryaccess.InventoryAccess;
import de.studiocode.inventoryaccess.abstraction.inventory.AnvilInventory;
import de.studiocode.inventoryaccess.component.ComponentWrapper;
import de.studiocode.invui.gui.AbstractGUI;
import de.studiocode.invui.window.AbstractSingleWindow;
import de.studiocode.invui.window.AnvilWindow;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public final class AnvilSingleWindowImpl extends AbstractSingleWindow implements AnvilWindow {
    
    private final AnvilInventory anvilInventory;
    
    public AnvilSingleWindowImpl(Player player, ComponentWrapper title, AbstractGUI gui, Consumer<String> renameHandler, boolean closable, boolean retain) {
        super(player.getUniqueId(), title, gui, null, false, closable, retain);
        anvilInventory = InventoryAccess.createAnvilInventory(player, title, renameHandler);
        inventory = anvilInventory.getBukkitInventory();
        
        initItems();
        register();
    }
    
    @Override
    protected void setInvItem(int slot, ItemStack itemStack) {
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
