package xyz.xenondevs.invui.window;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.gui.AbstractGui;
import xyz.xenondevs.invui.internal.menu.CustomCartographyMenu;
import xyz.xenondevs.invui.util.ColorPalette;
import xyz.xenondevs.invui.util.MapIcon;
import xyz.xenondevs.invui.util.MapPatch;

import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

final class CartographyWindowImpl extends AbstractSplitWindow<CustomCartographyMenu> implements CartographyWindow {
    
    private static final int MAP_SIZE = 128;
    
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
    public void applyPatch(MapPatch patch) {
        menu.applyPatch(patch, isOpen());
    }
    
    @Override
    public void addIcon(MapIcon icon) {
        menu.addIcon(icon, isOpen());
    }
    
    @Override
    public void removeIcon(MapIcon icon) {
        menu.removeIcon(icon, isOpen());
    }
    
    @Override
    public void setIcons(Collection<? extends MapIcon> icons) {
        menu.setIcons(icons, isOpen());
    }
    
    public static final class BuilderImpl
        extends AbstractSplitWindow.AbstractBuilder<CartographyWindow, CartographyWindow.Builder>
        implements CartographyWindow.Builder
    {
        
        private final Set<MapIcon> icons = new HashSet<>();
        private byte @Nullable [] canvas;
        
        @Override
        public CartographyWindow.Builder addIcon(MapIcon icon) {
            icons.add(icon);
            return this;
        }
        
        @Override
        public CartographyWindow.Builder setIcons(Collection<? extends MapIcon> icons) {
            this.icons.addAll(icons);
            return this;
        }
        
        @Override
        public CartographyWindow.Builder setMap(byte[] colors) {
            if (colors.length != MAP_SIZE * MAP_SIZE)
                throw new IllegalArgumentException("Map data must be " + MAP_SIZE + "x" + MAP_SIZE + " pixels");
            this.canvas = colors.clone();
            return this;
        }
        
        @Override
        public CartographyWindow.Builder setMap(BufferedImage image) {
            if (image.getWidth() != MAP_SIZE || image.getHeight() != MAP_SIZE)
                throw new IllegalArgumentException("Image must be " + MAP_SIZE + "x" + MAP_SIZE + " pixels");
            canvas = ColorPalette.convertImage(image);
            return this;
        }
        
        @Override
        public CartographyWindow build(Player viewer) {
            var window = new CartographyWindowImpl(
                viewer,
                titleSupplier,
                supplyUpperGui(),
                supplyLowerGui(viewer),
                closeable
            );
            
            window.setIcons(icons);
            if (canvas != null)
                window.applyPatch(new MapPatch(0, 0, 128, 128, canvas));
            
            applyModifiers(window);
            
            return window;
        }
        
    }
    
}
