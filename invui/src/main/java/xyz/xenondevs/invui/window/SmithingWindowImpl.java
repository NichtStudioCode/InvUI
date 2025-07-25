package xyz.xenondevs.invui.window;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Unmodifiable;
import xyz.xenondevs.invui.gui.AbstractGui;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.internal.menu.CustomSmithingTableMenu;
import xyz.xenondevs.invui.state.Property;

import java.util.List;
import java.util.function.Supplier;

final class SmithingWindowImpl extends AbstractSplitWindow<CustomSmithingTableMenu> implements SmithingWindow {
    
    private final AbstractGui upperGui;
    private final AbstractGui lowerGui;
    
    public SmithingWindowImpl(
        Player player,
        Supplier<? extends Component> title,
        AbstractGui upperGui,
        AbstractGui lowerGui,
        Property<? extends Boolean> closeable
    ) {
        super(player, title, lowerGui, 41, new CustomSmithingTableMenu(player), closeable);
        if (upperGui.getWidth() != 4 || upperGui.getHeight() != 1)
            throw new IllegalArgumentException("Upper Gui must be of dimensions 4x1");
        
        this.upperGui = upperGui;
        this.lowerGui = lowerGui;
    }
    
    @Override
    public @Unmodifiable List<Gui> getGuis() {
        return List.of(upperGui, lowerGui);
    }
    
    public static final class BuilderImpl
        extends AbstractBuilder<SmithingWindow, SmithingWindow.Builder>
        implements SmithingWindow.Builder
    {
        
        private Supplier<? extends Gui> upperGuiSupplier = () -> Gui.empty(4, 1);
        
        @Override
        public SmithingWindow.Builder setUpperGui(Supplier<? extends Gui> guiSupplier) {
            this.upperGuiSupplier = guiSupplier;
            return this;
        }
        
        @Override
        public SmithingWindow build(Player viewer) {
            var window = new SmithingWindowImpl(
                viewer,
                titleSupplier,
                (AbstractGui) upperGuiSupplier.get(),
                supplyLowerGui(viewer),
                closeable
            );
            
            applyModifiers(window);
            
            return window;
        }
        
    }
    
}
