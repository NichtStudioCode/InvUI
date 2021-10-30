package de.studiocode.invui.window.impl.single;

import de.studiocode.inventoryaccess.abstraction.inventory.CartographyInventory;
import de.studiocode.inventoryaccess.map.MapIcon;
import de.studiocode.inventoryaccess.map.MapPatch;
import de.studiocode.inventoryaccess.version.InventoryAccess;
import de.studiocode.invui.gui.GUI;
import de.studiocode.invui.util.MathUtils;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings("deprecation")
public class CartographyWindow extends SingleWindow {
    
    private final CartographyInventory cartographyInventory;
    private int mapId;
    
    public CartographyWindow(Player player, String title, GUI gui) {
        this(player, TextComponent.fromLegacyText(title), gui, true);
    }
    
    public CartographyWindow(Player player, String title, GUI gui, boolean closeable) {
        this(player, TextComponent.fromLegacyText(title), gui, closeable);
    }
    
    public CartographyWindow(Player player, BaseComponent[] title, GUI gui) {
        this(player, title, gui, true);
    }
    
    public CartographyWindow(Player player, BaseComponent[] title, GUI gui, boolean closeable) {
        super(player.getUniqueId(), title, gui, null, false, closeable, true);
        if (gui.getWidth() != 2 || gui.getHeight() != 1) throw new IllegalArgumentException("GUI has to be 2x1");
        
        cartographyInventory = InventoryAccess.createCartographyInventory(player, title);
        inventory = cartographyInventory.getBukkitInventory();
        
        initItems();
        resetMap();
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
    
    public void resetMap() {
        mapId = -MathUtils.RANDOM.nextInt(Integer.MAX_VALUE);
        ItemStack map = new ItemStack(Material.FILLED_MAP);
        MapMeta mapMeta = (MapMeta) map.getItemMeta();
        mapMeta.setMapId(mapId);
        map.setItemMeta(mapMeta);
        cartographyInventory.setItem(0, map);
        
        getViewer().getInventory().addItem(map);
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
        if (isClosed()) throw new IllegalStateException("The Window has already been closed.");
        
        Player viewer = getViewer();
        if (viewer == null) throw new IllegalStateException("The player is not online.");
        cartographyInventory.open();
    }
    
}
