package xyz.xenondevs.invui.window;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Unmodifiable;
import xyz.xenondevs.invui.gui.AbstractGui;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.internal.menu.CustomBrewingStandMenu;
import xyz.xenondevs.invui.state.Property;

import java.util.List;
import java.util.function.Supplier;

final class BrewerWindowImpl extends AbstractSplitWindow<CustomBrewingStandMenu> implements BrewerWindow {
    
    private final AbstractGui inputGui;
    private final AbstractGui fuelGui;
    private final AbstractGui resultGui;
    private final AbstractGui lowerGui;
    private Property<? extends Double> brewProgress;
    private Property<? extends Double> fuelProgress;
    
    public BrewerWindowImpl(
        Player player,
        Supplier<? extends Component> title,
        AbstractGui inputGui,
        AbstractGui fuelGui,
        AbstractGui resultGui,
        AbstractGui lowerGui,
        Property<? extends Double> brewProgress,
        Property<? extends Double> fuelProgress,
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
        this.brewProgress = brewProgress;
        this.fuelProgress = fuelProgress;
        
        brewProgress.observeWeak(this, thisRef -> thisRef.menu.setBrewProgress(brewProgress.get()));
        fuelProgress.observeWeak(this, thisRef -> thisRef.menu.setFuelProgress(fuelProgress.get()));
        menu.setBrewProgress(brewProgress.get());
        menu.setFuelProgress(fuelProgress.get());
    }
    
    @Override
    public void setBrewProgress(double progress) {
        if (progress < 0 || progress > 1)
            throw new IllegalArgumentException("Brew progress must be between 0 and 1, but was " + progress);
        
        brewProgress.unobserveWeak(this);
        brewProgress = Property.of(progress);
        menu.setBrewProgress(progress);
    }
    
    @Override
    public double getBrewProgress() {
        return brewProgress.get();
    }
    
    @Override
    public void setFuelProgress(double progress) {
        if (progress < 0 || progress > 1)
            throw new IllegalArgumentException("Fuel progress must be between 0 and 1, but was " + progress);
        
        fuelProgress.unobserveWeak(this);
        fuelProgress = Property.of(progress);
        menu.setFuelProgress(progress);
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
        extends AbstractSplitWindow.AbstractBuilder<BrewerWindow, BrewerWindow.Builder>
        implements BrewerWindow.Builder
    {
        
        private Supplier<? extends Gui> inputGuiSupplier = () -> Gui.empty(1, 1);
        private Supplier<? extends Gui> fuelGuiSupplier = () -> Gui.empty(1, 1);
        private Supplier<? extends Gui> resultGuiSupplier = () -> Gui.empty(3, 1);
        private Property<? extends Double> brewProgress = Property.of(0.0);
        private Property<? extends Double> fuelProgress = Property.of(0.0);
        
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
        public BrewerWindow.Builder setBrewProgress(Property<? extends Double> progress) {
            this.brewProgress = progress;
            return this;
        }
        
        @Override
        public BrewerWindow.Builder setFuelProgress(Property<? extends Double> progress) {
            this.fuelProgress = progress;
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
                brewProgress,
                fuelProgress,
                closeable
            );
            
            applyModifiers(window);
            
            return window;
        }
        
    }
    
}
