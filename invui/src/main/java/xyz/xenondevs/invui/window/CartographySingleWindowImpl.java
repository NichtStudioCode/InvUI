package xyz.xenondevs.invui.window;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.gui.AbstractGui;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.i18n.Languages;
import xyz.xenondevs.invui.internal.CartographyInventory;
import xyz.xenondevs.invui.internal.util.MathUtils;
import xyz.xenondevs.invui.internal.util.PlayerUtils;
import xyz.xenondevs.invui.item.ItemWrapper;
import xyz.xenondevs.invui.item.SimpleItem;
import xyz.xenondevs.invui.util.MapIcon;
import xyz.xenondevs.invui.util.MapPatch;

import java.util.List;

final class CartographySingleWindowImpl extends AbstractSingleWindow implements CartographyWindow {
    
    private final CartographyInventory cartographyInventory;
    private int mapId;
    
    public CartographySingleWindowImpl(
        Player player,
        Component title,
        AbstractGui gui,
        boolean closeable
    ) {
        super(player, title, createWrappingGui(gui), null, closeable);
        
        if (gui.getWidth() != 2 || gui.getHeight() != 1)
            throw new IllegalArgumentException("Gui has to be 2x1");
        
        cartographyInventory = new CartographyInventory(player, Languages.getInstance().localized(player, title));
        inventory = cartographyInventory.getBukkitInventory();
        
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
        PlayerUtils.sendMapUpdate(getViewer(), mapId, (byte) 0, false, patch, icons);
    }
    
    @SuppressWarnings({"deprecation", "DuplicatedCode"})
    @Override
    public void resetMap() {
        mapId = -MathUtils.RANDOM.nextInt(Integer.MAX_VALUE);
        ItemStack map = new ItemStack(Material.FILLED_MAP);
        MapMeta mapMeta = (MapMeta) map.getItemMeta();
        mapMeta.setMapId(mapId);
        map.setItemMeta(mapMeta);
        getGui().setItem(0, new SimpleItem(new ItemWrapper(map)));
    }
    
    @Override
    protected void openInventory(Player viewer) {
        cartographyInventory.open();
    }
    
    public static final class BuilderImpl
        extends AbstractSingleWindow.AbstractBuilder<CartographyWindow, CartographyWindow.Builder.Single>
        implements CartographyWindow.Builder.Single
    {
        
        @Override
        public CartographyWindow build(Player viewer) {
            if (guiSupplier == null)
                throw new IllegalStateException("Gui is not defined.");
            
            var window = new CartographySingleWindowImpl(
                viewer,
                title,
                (AbstractGui) guiSupplier.get(),
                closeable
            );
            
            applyModifiers(window);
            
            return window;
        }
        
    }
    
}
