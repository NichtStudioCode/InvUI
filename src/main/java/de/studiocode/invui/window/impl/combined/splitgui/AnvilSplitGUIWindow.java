package de.studiocode.invui.window.impl.combined.splitgui;

import de.studiocode.inventoryaccess.api.abstraction.inventory.AnvilInventory;
import de.studiocode.inventoryaccess.api.version.InventoryAccess;
import de.studiocode.invui.gui.GUI;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public class AnvilSplitGUIWindow extends SplitGUIWindow {
    
    private final AnvilInventory anvilInventory;
    
    public AnvilSplitGUIWindow(Player player, String title, GUI upperGui, GUI lowerGui,
                               boolean closeable, Consumer<String> renameHandler) {
        
        super(player, upperGui, lowerGui, null, false, closeable, true);
        
        anvilInventory = InventoryAccess.createAnvilInventory(player, title, renameHandler);
        upperInventory = anvilInventory.getBukkitInventory();
        
        initUpperItems();
    }
    
    @Override
    protected void setUpperInvItem(int slot, ItemStack itemStack) {
        anvilInventory.setItem(slot, itemStack);
    }
    
    @Override
    public void show() {
        if (isClosed()) throw new IllegalStateException("The Window has already been closed.");
        
        Player viewer = getViewer();
        if (viewer == null) throw new IllegalStateException("The player is not online.");
        anvilInventory.open();
    }
    
    public String getRenameText() {
        return anvilInventory.getRenameText();
    }
    
}
