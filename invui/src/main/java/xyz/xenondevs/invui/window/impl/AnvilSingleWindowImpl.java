package xyz.xenondevs.invui.window.impl;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.inventoryaccess.InventoryAccess;
import xyz.xenondevs.inventoryaccess.abstraction.inventory.AnvilInventory;
import xyz.xenondevs.inventoryaccess.component.ComponentWrapper;
import xyz.xenondevs.invui.gui.AbstractGui;
import xyz.xenondevs.invui.window.AbstractSingleWindow;
import xyz.xenondevs.invui.window.AnvilWindow;

import java.util.List;
import java.util.function.Consumer;

public final class AnvilSingleWindowImpl extends AbstractSingleWindow implements AnvilWindow {
    
    private final AnvilInventory anvilInventory;
    
    public AnvilSingleWindowImpl(
        @NotNull Player player,
        @Nullable ComponentWrapper title,
        @NotNull AbstractGui gui,
        @Nullable List<@NotNull Consumer<@NotNull String>> renameHandlers,
        boolean closable,
        boolean retain
    ) {
        super(player.getUniqueId(), title, gui, null, false, closable, retain);
        anvilInventory = InventoryAccess.createAnvilInventory(player, title, renameHandlers);
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
