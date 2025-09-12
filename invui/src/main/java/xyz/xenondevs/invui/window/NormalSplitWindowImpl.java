package xyz.xenondevs.invui.window;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Unmodifiable;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.internal.menu.CustomPlainMenu;
import xyz.xenondevs.invui.state.MutableProperty;

import java.util.List;
import java.util.function.Supplier;

final class NormalSplitWindowImpl extends AbstractSplitWindow<CustomPlainMenu> {
    
    private final Gui upperGui;
    private final Gui lowerGui;
    
    public NormalSplitWindowImpl(
        Player player,
        Supplier<? extends Component> title,
        Gui upperGui,
        Gui lowerGui,
        MutableProperty<Boolean> closeable,
        MutableProperty<Integer> windowState
    ) {
        super(player, title, lowerGui, upperGui.getSize() + lowerGui.getSize(), new CustomPlainMenu(upperGui.getWidth(), upperGui.getHeight(), player), closeable, windowState);
        this.upperGui = upperGui;
        this.lowerGui = lowerGui;
    }
    
    @Override
    public @Unmodifiable List<Gui> getGuis() {
        return List.of(upperGui, lowerGui);
    }
    
    public static final class BuilderImpl
        extends AbstractSplitWindow.AbstractBuilder<Window, Window.Builder.Normal.Split>
        implements Window.Builder.Normal.Split
    {
        
        private Supplier<? extends Gui> upperGuiSupplier = () -> Gui.empty(9, 6);
        
        @Override
        public Normal.Split setUpperGui(Supplier<? extends Gui> guiSupplier) {
            this.upperGuiSupplier = guiSupplier;
            return this;
        }
        
        @Override
        public Window build(Player viewer) {
            var window = new NormalSplitWindowImpl(
                viewer,
                titleSupplier,
                upperGuiSupplier.get(),
                supplyLowerGui(viewer),
                closeable,
                windowState
            );
            
            applyModifiers(window);
            
            return window;
        }
    }
    
}
