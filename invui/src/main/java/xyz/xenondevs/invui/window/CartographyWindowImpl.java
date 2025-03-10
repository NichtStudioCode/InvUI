package xyz.xenondevs.invui.window;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.gui.AbstractGui;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.internal.menu.CustomCartographyMenu;
import xyz.xenondevs.invui.internal.util.ItemUtils2;
import xyz.xenondevs.invui.util.ColorPalette;
import xyz.xenondevs.invui.util.MapIcon;
import xyz.xenondevs.invui.util.MapPatch;

import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

final class CartographyWindowImpl extends AbstractSplitWindow<CustomCartographyMenu> implements CartographyWindow {
    
    private static final int MAP_SIZE = 128;
    private final AbstractGui inputGui;
    private final AbstractGui outputGui;
    private final AbstractGui lowerGui;
    
    public CartographyWindowImpl(
        Player player,
        Supplier<Component> title,
        AbstractGui inputGui,
        AbstractGui outputGui,
        AbstractGui lowerGui,
        boolean closeable
    ) {
        super(player, title, lowerGui, 3 + 36, new CustomCartographyMenu(player), closeable);
        if (inputGui.getWidth() != 1 || inputGui.getHeight() != 2)
            throw new IllegalArgumentException("Input gui must be of dimensions 1x2");
        
        this.inputGui = inputGui;
        this.outputGui = outputGui;
        this.lowerGui = lowerGui;
    }
    
    @Override
    protected void setMenuItem(int slot, @Nullable ItemStack itemStack) {
        if (slot == 0) {
            super.setMenuItem(0, ItemUtils2.nonEmpty(itemStack));
        } else {
            super.setMenuItem(slot, itemStack);
        }
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
    
    @Override
    public List<? extends Gui> getGuis() {
        return List.of(inputGui, outputGui, lowerGui);
    }
    
    public static final class BuilderImpl
        extends AbstractSplitWindow.AbstractBuilder<CartographyWindow, CartographyWindow.Builder>
        implements CartographyWindow.Builder
    {
        
        private Supplier<Gui> inputGuiSupplier = () -> Gui.empty(1, 2);
        private Supplier<Gui> outputGuiSupplier = () -> Gui.empty(1, 1);
        private final Set<MapIcon> icons = new HashSet<>();
        private byte @Nullable [] canvas;
        
        @Override
        public CartographyWindow.Builder setInputGui(Supplier<Gui> guiSupplier) {
            this.inputGuiSupplier = guiSupplier;
            return this;
        }
        
        @Override
        public CartographyWindow.Builder setOutputGui(Supplier<Gui> guiSupplier) {
            this.outputGuiSupplier = guiSupplier;
            return this;
        }
        
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
                (AbstractGui) inputGuiSupplier.get(),
                (AbstractGui) outputGuiSupplier.get(),
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
