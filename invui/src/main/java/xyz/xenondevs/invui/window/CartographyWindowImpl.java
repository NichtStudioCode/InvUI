package xyz.xenondevs.invui.window;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.gui.AbstractGui;
import xyz.xenondevs.invui.internal.menu.CustomCartographyMenu;
import xyz.xenondevs.invui.util.MapIcon;
import xyz.xenondevs.invui.util.MapPatch;

import java.util.List;
import java.util.function.Supplier;

final class CartographyWindowImpl extends AbstractSplitWindow<CustomCartographyMenu> implements CartographyWindow {
    
    public CartographyWindowImpl(
        Player player,
        Supplier<Component> title,
        AbstractGui upperGui,
        AbstractGui lowerGui,
        boolean closeable
    ) {
        super(player, title, upperGui, lowerGui, new CustomCartographyMenu(player), closeable);
        if (upperGui.getWidth() != 3 || upperGui.getHeight() != 1)
            throw new IllegalArgumentException("Upper gui must be of dimensions 3x1");
        initItems();
    }
    
    @Override
    public void resetMap() {
        menu.resetMap();
    }
    
    @Override
    public void updateMap(@Nullable MapPatch patch, @Nullable List<MapIcon> icons) {
        menu.sendMapUpdate(patch, icons);
    }
    
    public static final class BuilderImpl
        extends AbstractSplitWindow.AbstractBuilder<CartographyWindow, CartographyWindow.Builder>
        implements CartographyWindow.Builder
    {
        
        @Override
        public CartographyWindow build(Player viewer) {
            var window = new CartographyWindowImpl(
                viewer,
                titleSupplier,
                supplyUpperGui(),
                supplyLowerGui(viewer),
                closeable
            );
            
            applyModifiers(window);
            
            return window;
        }
        
    }
    
}
