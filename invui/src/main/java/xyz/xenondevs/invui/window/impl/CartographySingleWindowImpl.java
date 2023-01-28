package xyz.xenondevs.invui.window.impl;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.inventoryaccess.InventoryAccess;
import xyz.xenondevs.inventoryaccess.abstraction.inventory.CartographyInventory;
import xyz.xenondevs.inventoryaccess.component.ComponentWrapper;
import xyz.xenondevs.inventoryaccess.map.MapIcon;
import xyz.xenondevs.inventoryaccess.map.MapPatch;
import xyz.xenondevs.invui.gui.AbstractGui;
import xyz.xenondevs.invui.util.MathUtils;
import xyz.xenondevs.invui.window.AbstractSingleWindow;
import xyz.xenondevs.invui.window.CartographyWindow;

import java.util.List;

@SuppressWarnings("deprecation")
public final class CartographySingleWindowImpl extends AbstractSingleWindow implements CartographyWindow {
    
    private final CartographyInventory cartographyInventory;
    private int mapId;
    
    public CartographySingleWindowImpl(Player player, ComponentWrapper title, AbstractGui gui, boolean closeable, boolean retain) {
        super(player.getUniqueId(), title, gui, null, false, closeable, retain);
        if (gui.getWidth() != 2 || gui.getHeight() != 1) throw new IllegalArgumentException("Gui has to be 2x1");
        
        cartographyInventory = InventoryAccess.createCartographyInventory(player, title);
        inventory = cartographyInventory.getBukkitInventory();
        
        initItems();
        resetMap();
        register();
    }
    
    @Override
    public void updateMap(@Nullable MapPatch patch, @Nullable List<MapIcon> icons) {
        InventoryAccess.getPlayerUtils().sendMapUpdate(getViewer(), mapId, (byte) 0, false, patch, icons);
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public void resetMap() {
        mapId = -MathUtils.RANDOM.nextInt(Integer.MAX_VALUE);
        ItemStack map = new ItemStack(Material.FILLED_MAP);
        MapMeta mapMeta = (MapMeta) map.getItemMeta();
        mapMeta.setMapId(mapId);
        map.setItemMeta(mapMeta);
        cartographyInventory.setItem(0, map);
    }
    
    @Override
    protected void setInvItem(int slot, ItemStack itemStack) {
        cartographyInventory.setItem(slot + 1, itemStack);
    }
    
    @Override
    public void handleClick(InventoryClickEvent event) {
        if (event.getSlot() != 0) {
            getGui().handleClick(event.getSlot() - 1, (Player) event.getWhoClicked(), event.getClick(), event);
        } else {
            event.setCancelled(true);
        }
    }
    
    @Override
    public void show() {
        if (isRemoved())
            throw new IllegalStateException("The Window has already been closed.");
        
        Player viewer = getViewer();
        if (viewer == null)
            throw new IllegalStateException("The player is not online.");
        cartographyInventory.open();
    }
    
}
