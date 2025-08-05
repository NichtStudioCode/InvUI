package xyz.xenondevs.invui.window;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Unmodifiable;
import xyz.xenondevs.invui.gui.AbstractGui;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.internal.menu.CustomBrewingStandMenu;
import xyz.xenondevs.invui.state.MutableProperty;

import java.util.List;
import java.util.function.Supplier;

final class BrewingWindowImpl extends AbstractSplitWindow<CustomBrewingStandMenu> implements BrewingWindow {
    
    private final AbstractGui inputGui;
    private final AbstractGui fuelGui;
    private final AbstractGui resultGui;
    private final AbstractGui lowerGui;
    private final MutableProperty<Double> brewProgress;
    private final MutableProperty<Double> fuelProgress;
    
    public BrewingWindowImpl(
        Player player,
        Supplier<? extends Component> title,
        AbstractGui inputGui,
        AbstractGui fuelGui,
        AbstractGui resultGui,
        AbstractGui lowerGui,
        MutableProperty<Double> brewProgress,
        MutableProperty<Double> fuelProgress,
        MutableProperty<Boolean> closeable
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
        this.brewProgress = brewProgress;
        this.fuelProgress = fuelProgress;
        
        brewProgress.observeWeak(this, thisRef -> thisRef.menu.setBrewProgress(brewProgress.get()));
        fuelProgress.observeWeak(this, thisRef -> thisRef.menu.setFuelProgress(fuelProgress.get()));
        menu.setBrewProgress(brewProgress.get());
        menu.setFuelProgress(fuelProgress.get());
    }
    
    @Override
    public void setBrewProgress(double progress) {
        brewProgress.set(progress);
    }
    
    @Override
    public double getBrewProgress() {
        return brewProgress.get();
    }
    
    @Override
    public void setFuelProgress(double progress) {
        fuelProgress.set(progress);
    }
    
    @Override
    public double getFuelProgress() {
        return fuelProgress.get();
    }
    
    @Override
    public @Unmodifiable List<Gui> getGuis() {
        return List.of(resultGui, inputGui, fuelGui, lowerGui);
    }
    
    public static final class BuilderImpl
        extends AbstractSplitWindow.AbstractBuilder<BrewingWindow, BrewingWindow.Builder>
        implements BrewingWindow.Builder
    {
        
        private Supplier<? extends Gui> inputGuiSupplier = () -> Gui.empty(1, 1);
        private Supplier<? extends Gui> fuelGuiSupplier = () -> Gui.empty(1, 1);
        private Supplier<? extends Gui> resultGuiSupplier = () -> Gui.empty(3, 1);
        private MutableProperty<Double> brewProgress = MutableProperty.of(0.0);
        private MutableProperty<Double> fuelProgress = MutableProperty.of(0.0);
        
        @Override
        public BrewingWindow.Builder setInputGui(Supplier<? extends Gui> guiSupplier) {
            this.inputGuiSupplier = guiSupplier;
            return this;
        }
        
        @Override
        public BrewingWindow.Builder setFuelGui(Supplier<? extends Gui> guiSupplier) {
            this.fuelGuiSupplier = guiSupplier;
            return this;
        }
        
        @Override
        public BrewingWindow.Builder setResultGui(Supplier<? extends Gui> guiSupplier) {
            this.resultGuiSupplier = guiSupplier;
            return this;
        }
        
        @Override
        public BrewingWindow.Builder setBrewProgress(MutableProperty<Double> progress) {
            this.brewProgress = progress;
            return this;
        }
        
        @Override
        public BrewingWindow.Builder setFuelProgress(MutableProperty<Double> progress) {
            this.fuelProgress = progress;
            return this;
        }
        
        @Override
        public BrewingWindow build(Player viewer) {
            var window = new BrewingWindowImpl(
                viewer,
                titleSupplier,
                (AbstractGui) inputGuiSupplier.get(),
                (AbstractGui) fuelGuiSupplier.get(),
                (AbstractGui) resultGuiSupplier.get(),
                supplyLowerGui(viewer),
                brewProgress,
                fuelProgress,
                closeable
            );
            
            applyModifiers(window);
            
            return window;
        }
        
    }
    
}
