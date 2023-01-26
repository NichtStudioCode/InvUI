package de.studiocode.invui.window.impl.merged.split;

import de.studiocode.inventoryaccess.abstraction.inventory.AnvilInventory;
import de.studiocode.inventoryaccess.component.BaseComponentWrapper;
import de.studiocode.inventoryaccess.component.ComponentWrapper;
import de.studiocode.inventoryaccess.version.InventoryAccess;
import de.studiocode.invui.gui.GUI;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public final class AnvilSplitWindow extends SplitWindow {
    
    private final AnvilInventory anvilInventory;
    
    public AnvilSplitWindow(Player player, ComponentWrapper title, GUI upperGui, GUI lowerGui, boolean closeable, Consumer<String> renameHandler) {
        super(player, title, upperGui, lowerGui, null, false, closeable, true);
        
        anvilInventory = InventoryAccess.createAnvilInventory(player, title, renameHandler);
        upperInventory = anvilInventory.getBukkitInventory();
        
        initUpperItems();
        register();
    }
    
    public AnvilSplitWindow(Player player, ComponentWrapper title, GUI upperGui, GUI lowerGui, Consumer<String> renameHandler) {
        this(player, title, upperGui, lowerGui, true, renameHandler);
    }
    
    public AnvilSplitWindow(Player player, BaseComponent[] title, GUI upperGui, GUI lowerGui, boolean closeable, Consumer<String> renameHandler) {
        this(player, new BaseComponentWrapper(title), upperGui, lowerGui, closeable, renameHandler);
    }
    
    public AnvilSplitWindow(Player player, BaseComponent[] title, GUI upperGui, GUI lowerGui, Consumer<String> renameHandler) {
        this(player, title, upperGui, lowerGui, true, renameHandler);
    }
    
    public AnvilSplitWindow(Player player, String title, GUI upperGui, GUI lowerGui, boolean closeable, Consumer<String> renameHandler) {
        this(player, TextComponent.fromLegacyText(title), upperGui, lowerGui, closeable, renameHandler);
    }
    
    public AnvilSplitWindow(Player player, String title, GUI upperGui, GUI lowerGui, Consumer<String> renameHandler) {
        this(player, title, upperGui, lowerGui, true, renameHandler);
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
