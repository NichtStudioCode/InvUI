package xyz.xenondevs.invui.window;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Unmodifiable;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.internal.menu.CustomGrindstoneMenu;
import xyz.xenondevs.invui.state.MutableProperty;

import java.util.List;
import java.util.function.Supplier;

final class GrindstoneWindowImpl extends AbstractSplitWindow<CustomGrindstoneMenu> implements GrindstoneWindow {
    
    private final Gui inputGui;
    private final Gui resultGui;
    private final Gui lowerGui;
    
    public GrindstoneWindowImpl(
        Player player,
        Supplier<? extends Component> title,
        Gui inputGui,
        Gui resultGui,
        Gui lowerGui,
        MutableProperty<Boolean> closeable,
        MutableProperty<Integer> windowState
    ) {
        super(player, title, lowerGui, 41, new CustomGrindstoneMenu(player), closeable, windowState);
        if (inputGui.getWidth() != 1 || inputGui.getHeight() != 2)
            throw new IllegalArgumentException("Input Gui must be of dimensions 1x2");
        if (resultGui.getWidth() != 1 || resultGui.getHeight() != 1)
            throw new IllegalArgumentException("Result Gui must be of dimensions 1x1");
        
        this.inputGui = inputGui;
        this.resultGui = resultGui;
        this.lowerGui = lowerGui;
    }
    
    @Override
    public @Unmodifiable List<Gui> getGuis() {
        return List.of(inputGui, resultGui, lowerGui);
    }
    
    public static final class BuilderImpl
        extends AbstractBuilder<GrindstoneWindow, GrindstoneWindow.Builder>
        implements GrindstoneWindow.Builder
    {
        
        private Supplier<? extends Gui> inputGuiSupplier = () -> Gui.empty(1, 2);
        private Supplier<? extends Gui> resultGuiSupplier = () -> Gui.empty(1, 1);
        
        @Override
        public GrindstoneWindow.Builder setInputGui(Supplier<? extends Gui> guiSupplier) {
            this.inputGuiSupplier = guiSupplier;
            return this;
        }
        
        @Override
        public GrindstoneWindow.Builder setResultGui(Supplier<? extends Gui> guiSupplier) {
            this.resultGuiSupplier = guiSupplier;
            return this;
        }
        
        @Override
        public GrindstoneWindow build(Player viewer) {
            var window = new GrindstoneWindowImpl(
                viewer,
                titleSupplier,
                inputGuiSupplier.get(),
                resultGuiSupplier.get(),
                supplyLowerGui(viewer),
                closeable,
                windowState
            );
            
            applyModifiers(window);
            
            return window;
        }
        
    }
    
}
