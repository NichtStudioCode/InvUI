package xyz.xenondevs.invui.window;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Unmodifiable;
import org.jetbrains.annotations.UnmodifiableView;
import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.gui.AbstractGui;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.internal.menu.CustomCartographyMenu;
import xyz.xenondevs.invui.internal.util.FuncUtils;
import xyz.xenondevs.invui.state.MutableProperty;
import xyz.xenondevs.invui.util.ColorPalette;
import xyz.xenondevs.invui.util.ItemUtils;

import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

final class CartographyWindowImpl extends AbstractSplitWindow<CustomCartographyMenu> implements CartographyWindow {
    
    private static final Set<? extends MapIcon> DEFAULT_ICONS = Set.of();
    private static final View DEFAULT_VIEW = View.NORMAL;
    
    private static final int MAP_SIZE = 128;
    private final AbstractGui inputGui;
    private final AbstractGui resultGui;
    private final AbstractGui lowerGui;
    private final MutableProperty<Set<? extends MapIcon>> icons;
    private final MutableProperty<View> view;
    
    public CartographyWindowImpl(
        Player player,
        Supplier<? extends Component> title,
        AbstractGui inputGui,
        AbstractGui resultGui,
        AbstractGui lowerGui,
        MutableProperty<View> view,
        MutableProperty<Set<? extends MapIcon>> icons,
        MutableProperty<Boolean> closeable
    ) {
        super(player, title, lowerGui, 3 + 36, new CustomCartographyMenu(player), closeable);
        if (inputGui.getWidth() != 1 || inputGui.getHeight() != 2)
            throw new IllegalArgumentException("Input gui must be of dimensions 1x2");
        if (resultGui.getWidth() != 1 || resultGui.getHeight() != 1)
            throw new IllegalArgumentException("Result gui must be of dimensions 1x1");
        
        this.inputGui = inputGui;
        this.resultGui = resultGui;
        this.lowerGui = lowerGui;
        this.view = view;
        this.icons = icons;
        
        view.observeWeak(this, thisRef -> thisRef.menu.setView(thisRef.getView()));
        icons.observeWeak(this, thisRef -> thisRef.menu.setIcons(thisRef.getIcons(), isOpen()));
        
        menu.setView(getView());
        menu.setIcons(getIcons(), false);
    }
    
    @Override
    protected void setMenuItem(int slot, @Nullable ItemStack itemStack) {
        if (slot == 0 || slot == 1) {
            super.setMenuItem(slot, ItemUtils.takeOrPlaceholder(itemStack));
        } else {
            super.setMenuItem(slot, itemStack);
        }
    }
    
    @Override
    public void resetMap() {
        menu.resetMap();
    }
    
    @Override
    public void setView(View view) {
        this.view.set(view);
    }
    
    @Override
    public View getView() {
        return FuncUtils.getSafely(view, DEFAULT_VIEW);
    }
    
    @Override
    public void applyPatch(MapPatch patch) {
        menu.applyPatch(patch, isOpen());
    }
    
    @Override
    public void setIcons(Set<? extends MapIcon> icons) {
        this.icons.set(icons);
    }
    
    @Override
    public @UnmodifiableView Set<MapIcon> getIcons() {
        return Collections.unmodifiableSet(FuncUtils.getSafely(icons, DEFAULT_ICONS));
    }
    
    @Override
    public @Unmodifiable List<Gui> getGuis() {
        return List.of(inputGui, resultGui, lowerGui);
    }
    
    public static final class BuilderImpl
        extends AbstractSplitWindow.AbstractBuilder<CartographyWindow, CartographyWindow.Builder>
        implements CartographyWindow.Builder
    {
        
        private Supplier<? extends Gui> inputGuiSupplier = () -> Gui.empty(1, 2);
        private Supplier<? extends Gui> resultGuiSupplier = () -> Gui.empty(1, 1);
        private MutableProperty<Set<? extends MapIcon>> icons = MutableProperty.of(DEFAULT_ICONS);
        private MutableProperty<View> view = MutableProperty.of(DEFAULT_VIEW);
        private byte @Nullable [] canvas;
        
        @Override
        public CartographyWindow.Builder setInputGui(Supplier<? extends Gui> guiSupplier) {
            this.inputGuiSupplier = guiSupplier;
            return this;
        }
        
        @Override
        public CartographyWindow.Builder setResultGui(Supplier<? extends Gui> guiSupplier) {
            this.resultGuiSupplier = guiSupplier;
            return this;
        }
        
        @Override
        public CartographyWindow.Builder setIcons(MutableProperty<Set<? extends MapIcon>> icons) {
            this.icons = icons;
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
        public CartographyWindow.Builder setView(MutableProperty<View> view) {
            this.view = view;
            return this;
        }
        
        @Override
        public CartographyWindow build(Player viewer) {
            var window = new CartographyWindowImpl(
                viewer,
                titleSupplier,
                (AbstractGui) inputGuiSupplier.get(),
                (AbstractGui) resultGuiSupplier.get(),
                supplyLowerGui(viewer),
                view,
                icons,
                closeable
            );
            
            if (canvas != null)
                window.applyPatch(new MapPatch(0, 0, 128, 128, canvas));
            
            applyModifiers(window);
            
            return window;
        }
        
    }
    
}
