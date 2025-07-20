package xyz.xenondevs.invui.window;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Unmodifiable;
import xyz.xenondevs.invui.gui.AbstractGui;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.internal.menu.CustomBrewingStandMenu;

import java.util.List;
import java.util.function.Supplier;

final class BrewerWindowImpl extends AbstractSplitWindow<CustomBrewingStandMenu> implements BrewerWindow {
    
    private final AbstractGui inputGui;
    private final AbstractGui fuelGui;
    private final AbstractGui resultGui;
    private final AbstractGui lowerGui;
    
    public BrewerWindowImpl(
        Player player,
        Supplier<? extends Component> title,
        AbstractGui inputGui,
        AbstractGui fuelGui,
        AbstractGui resultGui,
        AbstractGui lowerGui,
        boolean closeable
    ) {
        super(player, title, lowerGui, 41, new CustomBrewingStandMenu(player), closeable);
        if (inputGui.getWidth() != 1 || inputGui.getHeight() != 1)
            throw new IllegalArgumentException("Input Gui must be of dimensions 1x1");
        if (fuelGui.getWidth() != 1 || fuelGui.getHeight() != 1)
            throw new IllegalArgumentException("Fuel Gui must be of dimensions 1x1");
        if (resultGui.getWidth() != 3 || resultGui.getHeight() != 1)
            throw new IllegalArgumentException("Result Gui must be of dimensions 3x1");
        
        this.inputGui = inputGui;
        this.fuelGui = fuelGui;
        this.resultGui = resultGui;
        this.lowerGui = lowerGui;
    }
    
    @Override
    public @Unmodifiable List<Gui> getGuis() {
        return List.of(resultGui, inputGui, fuelGui, lowerGui);
    }
    
    public static final class BuilderImpl
        extends AbstractSplitWindow.AbstractBuilder<BrewerWindow, BrewerWindow.Builder>
        implements BrewerWindow.Builder
    {
        
        private Supplier<? extends Gui> inputGuiSupplier = () -> Gui.empty(1, 1);
        private Supplier<? extends Gui> fuelGuiSupplier = () -> Gui.empty(1, 1);
        private Supplier<? extends Gui> resultGuiSupplier = () -> Gui.empty(3, 1);
        
        @Override
        public BrewerWindow.Builder setInputGui(Supplier<? extends Gui> guiSupplier) {
            this.inputGuiSupplier = guiSupplier;
            return this;
        }
        
        @Override
        public BrewerWindow.Builder setFuelGui(Supplier<? extends Gui> guiSupplier) {
            this.fuelGuiSupplier = guiSupplier;
            return this;
        }
        
        @Override
        public BrewerWindow.Builder setResultGui(Supplier<? extends Gui> guiSupplier) {
            this.resultGuiSupplier = guiSupplier;
            return this;
        }
        
        @Override
        public BrewerWindow build(Player viewer) {
            var window = new BrewerWindowImpl(
                viewer,
                titleSupplier,
                (AbstractGui) inputGuiSupplier.get(),
                (AbstractGui) fuelGuiSupplier.get(),
                (AbstractGui) resultGuiSupplier.get(),
                supplyLowerGui(viewer),
                closeable
            );
            
            applyModifiers(window);
            
            return window;
        }
        
    }
    
}
