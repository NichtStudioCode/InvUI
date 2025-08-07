package xyz.xenondevs.invui.window;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Unmodifiable;
import xyz.xenondevs.invui.gui.AbstractGui;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.internal.menu.CustomBrewingStandMenu;
import xyz.xenondevs.invui.internal.util.FuncUtils;
import xyz.xenondevs.invui.state.MutableProperty;

import java.util.List;
import java.util.function.Supplier;

final class BrewingWindowImpl extends AbstractSplitWindow<CustomBrewingStandMenu> implements BrewingWindow {
    
    private static final int BREW_PROGRESS_MAGIC_SLOT = 100;
    private static final int FUEL_PROGRESS_MAGIC_SLOT = 101;
    private static final double DEFAULT_BREW_PROGRESS = 0.0;
    private static final double DEFAULT_FUEL_PROGRESS = 0.0;
    
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
        
        brewProgress.observeWeak(this, thisRef -> thisRef.notifyUpdate(BREW_PROGRESS_MAGIC_SLOT));
        fuelProgress.observeWeak(this, thisRef -> thisRef.notifyUpdate(FUEL_PROGRESS_MAGIC_SLOT));
        menu.setBrewProgress(getBrewProgress());
        menu.setFuelProgress(getFuelProgress());
    }
    
    @Override
    protected void update(int slot) {
        switch (slot) {
            case BREW_PROGRESS_MAGIC_SLOT -> menu.setBrewProgress(getBrewProgress());
            case FUEL_PROGRESS_MAGIC_SLOT -> menu.setFuelProgress(getFuelProgress());
            default -> super.update(slot);
        }
    }
    
    @Override
    public void setBrewProgress(double progress) {
        brewProgress.set(progress);
    }
    
    @Override
    public double getBrewProgress() {
        return FuncUtils.getSafely(brewProgress, DEFAULT_BREW_PROGRESS);
    }
    
    @Override
    public void setFuelProgress(double progress) {
        fuelProgress.set(progress);
    }
    
    @Override
    public double getFuelProgress() {
        return FuncUtils.getSafely(fuelProgress, DEFAULT_FUEL_PROGRESS);
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
        private MutableProperty<Double> brewProgress = MutableProperty.of(DEFAULT_BREW_PROGRESS);
        private MutableProperty<Double> fuelProgress = MutableProperty.of(DEFAULT_FUEL_PROGRESS);
        
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
