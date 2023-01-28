package de.studiocode.invui.window.impl;

import de.studiocode.inventoryaccess.InventoryAccess;
import de.studiocode.inventoryaccess.abstraction.inventory.CartographyInventory;
import de.studiocode.inventoryaccess.component.ComponentWrapper;
import de.studiocode.inventoryaccess.map.MapIcon;
import de.studiocode.inventoryaccess.map.MapPatch;
import de.studiocode.invui.gui.AbstractGUI;
import de.studiocode.invui.gui.GUI;
import de.studiocode.invui.gui.impl.NormalGUIImpl;
import de.studiocode.invui.util.MathUtils;
import de.studiocode.invui.window.AbstractSplitWindow;
import de.studiocode.invui.window.CartographyWindow;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class CartographySplitWindowImpl extends AbstractSplitWindow implements CartographyWindow {
    
    private final CartographyInventory cartographyInventory;
    private int mapId;
    
    public CartographySplitWindowImpl(Player player, ComponentWrapper title, AbstractGUI upperGui, AbstractGUI lowerGui, boolean closeable, boolean retain) {
        super(player, title, createWrappingGUI(upperGui), lowerGui, null, false, closeable, retain);
        
        cartographyInventory = InventoryAccess.createCartographyInventory(player, title);
        upperInventory = cartographyInventory.getBukkitInventory();
        
        initUpperItems();
        resetMap();
        register();
    }
    
    private static AbstractGUI createWrappingGUI(GUI upperGui) {
        if (upperGui.getWidth() != 2 || upperGui.getHeight() != 1)
            throw new IllegalArgumentException("GUI has to be 2x1");
        
        NormalGUIImpl wrapperGUI = new NormalGUIImpl(3, 1);
        wrapperGUI.fillRectangle(1, 0, upperGui, true);
        return wrapperGUI;
    }
    
    @Override
    public void updateMap(@Nullable MapPatch patch, @Nullable List<MapIcon> icons) {
        InventoryAccess.getPlayerUtils().sendMapUpdate(getViewer(), mapId, (byte) 0, false, patch, icons);
    }
    
    @Override
    @SuppressWarnings("deprecation")
    public void resetMap() {
        mapId = -MathUtils.RANDOM.nextInt(Integer.MAX_VALUE);
        ItemStack map = new ItemStack(Material.FILLED_MAP);
        MapMeta mapMeta = (MapMeta) map.getItemMeta();
        mapMeta.setMapId(mapId);
        map.setItemMeta(mapMeta);
        cartographyInventory.setItem(0, map);
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
