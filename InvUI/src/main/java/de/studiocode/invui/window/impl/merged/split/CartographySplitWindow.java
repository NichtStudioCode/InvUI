package de.studiocode.invui.window.impl.merged.split;

import de.studiocode.inventoryaccess.abstraction.inventory.CartographyInventory;
import de.studiocode.inventoryaccess.map.MapIcon;
import de.studiocode.inventoryaccess.map.MapPatch;
import de.studiocode.inventoryaccess.version.InventoryAccess;
import de.studiocode.invui.gui.GUI;
import de.studiocode.invui.gui.impl.SimpleGUI;
import de.studiocode.invui.util.MathUtils;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class CartographySplitWindow extends SplitWindow {
    
    private final CartographyInventory cartographyInventory;
    private int mapId;
    
    public CartographySplitWindow(Player player, String title, GUI upperGui, GUI lowerGui) {
        this(player, TextComponent.fromLegacyText(title), upperGui, lowerGui, true);
    }
    
    public CartographySplitWindow(Player player, String title, GUI upperGui, GUI lowerGui, boolean closeable) {
        this(player, TextComponent.fromLegacyText(title), upperGui, lowerGui, closeable);
    }
    
    public CartographySplitWindow(Player player, BaseComponent[] title, GUI upperGui, GUI lowerGui) {
        this(player, title, upperGui, lowerGui, true);
    }
    
    public CartographySplitWindow(Player player, BaseComponent[] title, GUI upperGui, GUI lowerGui, boolean closeable) {
        super(player, title, createWrappingGUI(upperGui), lowerGui, null, false, closeable, true);
        
        cartographyInventory = InventoryAccess.createCartographyInventory(player, title);
        upperInventory = cartographyInventory.getBukkitInventory();
        
        initUpperItems();
        resetMap();
        register();
    }
    
    private static GUI createWrappingGUI(GUI upperGui) {
        if (upperGui.getWidth() != 2 || upperGui.getHeight() != 1)
            throw new IllegalArgumentException("GUI has to be 2x1");
        
        GUI wrapperGUI = new SimpleGUI(3, 1);
        wrapperGUI.fillRectangle(1, 0, upperGui, true);
        return wrapperGUI;
    }
    
    public void updateMap(@Nullable MapPatch patch, @Nullable List<MapIcon> icons) {
        InventoryAccess.getPlayerUtils().sendMapUpdate(getViewer(), mapId, (byte) 0, false, patch, icons);
    }
    
    public void updateMap(MapPatch patch) {
        updateMap(patch, null);
    }
    
    public void updateMap(List<MapIcon> icons) {
        updateMap(null, icons);
    }
    
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
        if (isClosed()) throw new IllegalStateException("The Window has already been closed.");
        
        Player viewer = getViewer();
        if (viewer == null) throw new IllegalStateException("The player is not online.");
        cartographyInventory.open();
    }
    
}
