package xyz.xenondevs.invui.window;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.jspecify.annotations.Nullable;
import xyz.xenondevs.inventoryaccess.InventoryAccess;
import xyz.xenondevs.inventoryaccess.abstraction.inventory.CartographyInventory;
import xyz.xenondevs.inventoryaccess.map.MapIcon;
import xyz.xenondevs.inventoryaccess.map.MapPatch;
import xyz.xenondevs.invui.gui.AbstractGui;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.i18n.Languages;
import xyz.xenondevs.invui.item.impl.SimpleItem;
import xyz.xenondevs.invui.util.MathUtils;

import java.util.List;

/**
 * An {@link AbstractSplitWindow} that uses a {@link CartographyInventory} as the upper inventory
 * and the player inventory as the lower inventory.
 * <p>
 * Use the builder obtained by {@link CartographyWindow#split()}, to get an instance of this class.
 */
final class CartographySplitWindowImpl extends AbstractSplitWindow implements CartographyWindow {
    
    private final CartographyInventory cartographyInventory;
    private int mapId;
    
    public CartographySplitWindowImpl(
        Player player,
        @Nullable Component title,
        AbstractGui upperGui,
        AbstractGui lowerGui,
        boolean closeable
    ) {
        super(player, title, createWrappingGui(upperGui), lowerGui, null, closeable);
        
        cartographyInventory = InventoryAccess.createCartographyInventory(
            player,
            title != null ? Languages.getInstance().localized(player, title) : null
        );
        upperInventory = cartographyInventory.getBukkitInventory();
        
        resetMap();
    }
    
    private static AbstractGui createWrappingGui(Gui upperGui) {
        if (upperGui.getWidth() != 2 || upperGui.getHeight() != 1)
            throw new IllegalArgumentException("Gui has to be 2x1");
        
        Gui wrapperGui = Gui.empty(3, 1);
        wrapperGui.fillRectangle(1, 0, upperGui, true);
        return (AbstractGui) wrapperGui;
    }
    
    @Override
    public void updateMap(@Nullable MapPatch patch, @Nullable List<MapIcon> icons) {
        InventoryAccess.getPlayerUtils().sendMapUpdate(getViewer(), mapId, (byte) 0, false, patch, icons);
    }
    
    @Override
    @SuppressWarnings({"deprecation", "DuplicatedCode"})
    public void resetMap() {
        mapId = -MathUtils.RANDOM.nextInt(Integer.MAX_VALUE);
        ItemStack map = new ItemStack(Material.FILLED_MAP);
        MapMeta mapMeta = (MapMeta) map.getItemMeta();
        mapMeta.setMapId(mapId);
        map.setItemMeta(mapMeta);
        getGuis()[0].setItem(0, new SimpleItem(map));
    }
    
    @Override
    protected void openInventory(Player viewer) {
        cartographyInventory.open();
    }
    
    public static final class BuilderImpl
        extends AbstractSplitWindow.AbstractBuilder<CartographyWindow, CartographyWindow.Builder.Split>
        implements CartographyWindow.Builder.Split
    {
        
        @Override
        public CartographyWindow build(Player viewer) {
            if (viewer == null)
                throw new IllegalStateException("Viewer is not defined.");
            if (upperGuiSupplier == null)
                throw new IllegalStateException("Upper Gui is not defined.");
            
            var window = new CartographySplitWindowImpl(
                viewer,
                title,
                (AbstractGui) upperGuiSupplier.get(),
                (AbstractGui) lowerGuiSupplier.get(),
                closeable
            );
            
            applyModifiers(window);
            
            return window;
        }
        
    }
    
}
